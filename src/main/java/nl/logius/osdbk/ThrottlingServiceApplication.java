package nl.logius.osdbk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ThrottlingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThrottlingServiceApplication.class, args);
    }
}
