package nl.logius.osdbk.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
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

    private final Semaphore semaphore = new Semaphore(1);

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

    /**
     * Create a semaphore with a counter of 1 and acquire the semaphore before accessing the database.
     * Once the listener is finished accessing the database, it releases the semaphore.
     * This way, only one JMS listener will be able to access the database at a time, ensuring that multiple listeners
     * do not read from the database simultaneously.
     */
    private int getCombinedMessageCount(String afnemerOin) {

        int count = 0;
        try {
            semaphore.acquire();

            Map<String, String> parameters = Collections.singletonMap("afnemerOin", OIN_PREFIX + afnemerOin);
            count  = jdbcTemplate.queryForObject(combinedCountSQL, parameters, Integer.class);
        } catch (InterruptedException e) {
            logger.error("InterruptedException occurred while acquiring semaphore", e);
            Thread.currentThread().interrupt();
        } catch (DataAccessException e) {
            logger.error("DataAccessException occurred while querying database", e);
        } catch (Exception e) {
            logger.error("Exception occurred while executing getCombinedMessageCount", e);
        }
        finally {
            semaphore.release();
        }
        return count;
    }

}
