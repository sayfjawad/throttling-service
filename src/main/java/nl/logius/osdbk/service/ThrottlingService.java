package nl.logius.osdbk.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import nl.logius.osdbk.configuration.ThrottlingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ThrottlingService {

    private static final Logger logger = LoggerFactory.getLogger(ThrottlingService.class);

    private final ThrottlingConfiguration throttlingConfiguration;
    private final AsyncService asyncService;

    @Autowired
    public ThrottlingService(ThrottlingConfiguration throttlingConfiguration, AsyncService asyncService) {
        this.throttlingConfiguration = throttlingConfiguration;
        this.asyncService = asyncService;
    }

    public boolean shouldAfnemerBeThrottled(String afnemerOin) {

        Optional<ThrottlingConfiguration.Afnemer> optionalThrottledAfnemer = throttlingConfiguration.getAfnemers().stream()
                .filter(throttledAfnemer -> afnemerOin.equalsIgnoreCase(throttledAfnemer.getOin()))
                .findAny();

        if (optionalThrottledAfnemer.isPresent()) {

            ThrottlingConfiguration.Afnemer throttledAfnemer = optionalThrottledAfnemer.get();
            int throttleValue = throttledAfnemer.getThrottleValue();

            CompletableFuture<Integer> amountOfRecordsReadyToBeSent = asyncService.getAmountOfRecordsReadyToBeSentForAfnemer(afnemerOin);
            CompletableFuture<Integer> amountOfRecordsAlreadySentInLastSecond = asyncService.getAmountOfRecordsAlreadySentInLastSecondForAfnemer(afnemerOin);

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

}
