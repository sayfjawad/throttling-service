package nl.logius.osdbk.controller;

import nl.logius.osdbk.service.ThrottlingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThrottlingController {

    private static final Logger logger = LoggerFactory.getLogger(ThrottlingController.class);

    @Autowired
    private ThrottlingService throttlingService;

    @GetMapping("/throttling/{cpaId}")
    public int getAmountOfPendingTasksForCpa(@PathVariable String cpaId) {

        if(cpaId.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cpaId provided");
        }

        CompletableFuture<Integer> amountOfRecordsReadyToBeSent = throttlingService.getAmountOfRecordsReadyToBeSentForCpa(cpaId);
        CompletableFuture<Integer> amountOfRecordsAlreadySentInLastSecond = throttlingService.getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaId);

        int totalAmountOfRecords = Stream.of(amountOfRecordsReadyToBeSent, amountOfRecordsAlreadySentInLastSecond)
                .map(CompletableFuture::join)
                .mapToInt(Integer::intValue)
                .sum();

        logger.info("CPA {} has a combined total of {} pending tasks and handled tasks.", cpaId, totalAmountOfRecords);
        return totalAmountOfRecords;
    }

}
