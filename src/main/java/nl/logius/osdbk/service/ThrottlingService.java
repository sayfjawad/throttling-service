package nl.logius.osdbk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ThrottlingService {

    @Value("${spring.throttling-sql.ready-to-send}")
    private String readyToBeSentSQL;

    @Value("${spring.throttling-sql.already-sent}")
    private String alreadySentSQL;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Async
    public CompletableFuture<Integer> getAmountOfRecordsReadyToBeSentForCpa(String cpaid) {
        int amountOfRecordsReadyToBeSent = jdbcTemplate.queryForObject(readyToBeSentSQL, Integer.class, cpaid);
        return CompletableFuture.completedFuture(amountOfRecordsReadyToBeSent);
    }

    @Async
    public CompletableFuture<Integer> getAmountOfRecordsAlreadySentInLastSecondForCpa(String cpaid) {
        int amountOfRecordsAlreadySent = jdbcTemplate.queryForObject(alreadySentSQL, Integer.class, cpaid);
        return CompletableFuture.completedFuture(amountOfRecordsAlreadySent);
    }
}
