package nl.logius.osdbk.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import nl.logius.osdbk.configuration.ThrottlingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ThrottlingService {

    private static final Logger logger = LoggerFactory.getLogger(ThrottlingService.class);

    @Autowired
    private ThrottlingConfiguration throttlingConfiguration;

    @Value("${throttling.sql.ready-to-send}")
    private String readyToBeSentSQL;

    @Value("${throttling.sql.already-sent}")
    private String alreadySentSQL;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final String OIN_PREFIX = "urn:osb:oin:";

    public boolean shouldAfnemerBeThrottled(String afnemerOin) {

        Optional<ThrottlingConfiguration.Afnemer> optionalThrottledAfnemer = throttlingConfiguration.getAfnemers().stream()
                .filter(throttledAfnemer -> afnemerOin.equalsIgnoreCase(throttledAfnemer.getOin()))
                .findAny();

        if (optionalThrottledAfnemer.isPresent()) {

            ThrottlingConfiguration.Afnemer throttledAfnemer = optionalThrottledAfnemer.get();
            int throttleValue = throttledAfnemer.getThrottleValue();

            CompletableFuture<Integer> amountOfRecordsReadyToBeSent = getAmountOfRecordsReadyToBeSentForAfnemer(afnemerOin);
            CompletableFuture<Integer> amountOfRecordsAlreadySentInLastSecond = getAmountOfRecordsAlreadySentInLastSecondForAfnemer(afnemerOin);

            int numberOfTasksInLastSecond = Stream.of(amountOfRecordsReadyToBeSent, amountOfRecordsAlreadySentInLastSecond)
                    .map(CompletableFuture::join)
                    .mapToInt(Integer::intValue)
                    .sum();

            if (numberOfTasksInLastSecond < throttleValue) {
                logger.info("Afnemer {} with throttle value {} can handle {} more messages. Message will be sent", afnemerOin, throttleValue, (throttleValue - numberOfTasksInLastSecond));
                return true;
            } else {
                logger.info("Afnemer {} with throttle value {} can not handle more messages. Message can not be sent", afnemerOin, throttleValue);
                return false;
            }
        } else {
                logger.info("Afnemer {} is not throttled. Message will be sent", afnemerOin);
            return true;
        }
    }

    @Async
    private CompletableFuture<Integer> getAmountOfRecordsReadyToBeSentForAfnemer(String afnemerOin) {
        int amountOfRecordsReadyToBeSent = jdbcTemplate.queryForObject(readyToBeSentSQL, Integer.class, OIN_PREFIX + afnemerOin);
        return CompletableFuture.completedFuture(amountOfRecordsReadyToBeSent);
    }

    @Async
    private CompletableFuture<Integer> getAmountOfRecordsAlreadySentInLastSecondForAfnemer(String afnemerOin) {
        int amountOfRecordsAlreadySent = jdbcTemplate.queryForObject(alreadySentSQL, Integer.class, OIN_PREFIX + afnemerOin);
        return CompletableFuture.completedFuture(amountOfRecordsAlreadySent);
    }
}
