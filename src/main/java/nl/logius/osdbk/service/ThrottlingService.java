package nl.logius.osdbk.service;

import nl.logius.osdbk.persistence.repository.EbmsMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ThrottlingService {

    private static final Logger logger = LoggerFactory.getLogger(ThrottlingService.class);

    @Autowired
    EbmsMessageRepository repository;

    @Async
    public CompletableFuture<Integer> getAmountOfRecordsReadyToBeSentForCpa(String cpaid){
        int amountOfRecordsReadyToBeSent = repository.getAmountOfRecordsReadyToBeSentForCpa(cpaid).intValue();
        logger.info("throttling-service getAmountOfRecordsReadyToBeSentForCpa executed for cpaId: {} and amountOfRecordsReadyToBeSent: {} ", cpaid, amountOfRecordsReadyToBeSent);
        return CompletableFuture.completedFuture(amountOfRecordsReadyToBeSent);
    }

    @Async
    public CompletableFuture<Integer> getAmountOfRecordsAlreadySentInLastSecondForCpa(String cpaid){
        int amountOfRecordsAlreadySent = repository.getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaid).intValue();
        logger.info("throttling-service getAmountOfRecordsAlreadySentInLastSecondForCpa executed for cpaId: {} and amountOfRecordsAlreadySent: {} ", cpaid, amountOfRecordsAlreadySent);
        return CompletableFuture.completedFuture(amountOfRecordsAlreadySent);
    }
}
