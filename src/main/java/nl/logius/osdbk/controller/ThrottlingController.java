package nl.logius.osdbk.controller;

import nl.logius.osdbk.service.ThrottlingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThrottlingController {

    private final ThrottlingService throttlingService;

    @Autowired
    public ThrottlingController (ThrottlingService throttlingService) {
        this.throttlingService = throttlingService;
    }

    @GetMapping("/throttling/{afnemerOin}")
    public boolean shouldAfnemerBeThrottled(@PathVariable String afnemerOin) {

        if(afnemerOin.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No afnemerOin provided");
        }

        return throttlingService.shouldAfnemerBeThrottled(afnemerOin);
    }

}
