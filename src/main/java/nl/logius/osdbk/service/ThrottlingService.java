package nl.logius.osdbk.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import nl.logius.osdbk.configuration.ThrottlingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ThrottlingService {

    private static final Logger logger = LoggerFactory.getLogger(ThrottlingService.class);

    private final ThrottlingConfiguration throttlingConfiguration;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Value("${throttling.sql.combined-count}")
    private String combinedCountSQL;

    private static final String OIN_PREFIX = "urn:osb:oin:";

    @Autowired
    public ThrottlingService(ThrottlingConfiguration throttlingConfiguration, NamedParameterJdbcTemplate jdbcTemplate) {
        this.throttlingConfiguration = throttlingConfiguration;
        this.jdbcTemplate = jdbcTemplate;
    }


    public boolean shouldAfnemerBeThrottled(String afnemerOin) {

        Optional<ThrottlingConfiguration.Afnemer> optionalThrottledAfnemer = throttlingConfiguration.getAfnemers().stream()
                .filter(throttledAfnemer -> afnemerOin.equalsIgnoreCase(throttledAfnemer.getOin()))
                .findAny();

        if (optionalThrottledAfnemer.isPresent()) {

            ThrottlingConfiguration.Afnemer throttledAfnemer = optionalThrottledAfnemer.get();
            int throttleValue = throttledAfnemer.getThrottleValue();

            int numberOfTasksInLastSecond = getCombinedMessageCount(afnemerOin);

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

    private int getCombinedMessageCount(String afnemerOin) {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("afnemerOin", OIN_PREFIX + afnemerOin);

        return jdbcTemplate.queryForObject(combinedCountSQL, parameters, Integer.class);
    }

}
