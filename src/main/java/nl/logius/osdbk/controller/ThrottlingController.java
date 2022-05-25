package nl.logius.osdbk.controller;

import nl.logius.osdbk.model.PendingTasks;
import nl.logius.osdbk.service.ThrottlingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@Component
public class ThrottlingController {

    private static final Logger logger = LoggerFactory.getLogger(ThrottlingController.class);

    private final ThrottlingService throttlingService;

    public ThrottlingController(ThrottlingService throttlingService) {
        this.throttlingService = throttlingService;
    }

    @GetMapping("/throttling/{cpaid}/pendingTasks")
    @ResponseBody
    public PendingTasks getAmountOfPendingTasksForCpa(@PathVariable String cpaid) {

        if(cpaid.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cpaId provided");
        }

        CompletableFuture<Integer> amountOfRecordsReadyToBeSent = throttlingService.getAmountOfRecordsReadyToBeSentForCpa(cpaid);
        CompletableFuture<Integer> amountOfRecordsAlreadySentInLastSecond = throttlingService.getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaid);

        int amountOfPendingTasks = Stream.of(amountOfRecordsReadyToBeSent, amountOfRecordsAlreadySentInLastSecond)
                .map(CompletableFuture::join)
                .mapToInt(Integer::intValue).sum();

        PendingTasks pendingTasks = new PendingTasks();
        pendingTasks.setCpaId(cpaid);
        pendingTasks.setAmountOfPendingTasks(amountOfPendingTasks);

        logger.info("throttling-service getAmountOfPendingTasksForCpa executed for cpaId: {} and amountOfPendingTasks: {}", cpaid, pendingTasks);
        return pendingTasks;
    }

}
