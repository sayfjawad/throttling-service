package nl.logius.osdbk;

import nl.logius.osdbk.controller.ThrottlingController;
import nl.logius.osdbk.persistence.repository.EbmsMessageRepository;
import nl.logius.osdbk.service.ThrottlingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ThrottlingServiceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class ThrottlingServiceApplicationTests {

    @Autowired
    private ThrottlingController throttlingController;

    @Autowired
    private ThrottlingService throttlingService;

    @Autowired
    private EbmsMessageRepository ebmsMessageRepository;

    @Test
    void testContextLoads() {
        assertThat(throttlingController).isNotNull();
        assertThat(throttlingService).isNotNull();
        assertThat(ebmsMessageRepository).isNotNull();
    }

    @Test
    void testApplicationStartup() {
        ThrottlingServiceApplication.main(new String[]{});
    }

}
