package nl.logius.osdbk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {

    private final JdbcTemplate jdbcTemplate;

    public AsyncService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${throttling.sql.ready-to-send}")
    private String readyToBeSentSQL;

    @Value("${throttling.sql.already-sent}")
    private String alreadySentSQL;

    private static final String OIN_PREFIX = "urn:osb:oin:";

    @Async
    public CompletableFuture<Integer> getAmountOfRecordsReadyToBeSentForAfnemer(String afnemerOin) {
        int amountOfRecordsReadyToBeSent = jdbcTemplate.queryForObject(readyToBeSentSQL, Integer.class, OIN_PREFIX + afnemerOin);
        return CompletableFuture.completedFuture(amountOfRecordsReadyToBeSent);
    }

    @Async
    public CompletableFuture<Integer> getAmountOfRecordsAlreadySentInLastSecondForAfnemer(String afnemerOin) {
        int amountOfRecordsAlreadySent = jdbcTemplate.queryForObject(alreadySentSQL, Integer.class, OIN_PREFIX + afnemerOin);
        return CompletableFuture.completedFuture(amountOfRecordsAlreadySent);
    }
}
