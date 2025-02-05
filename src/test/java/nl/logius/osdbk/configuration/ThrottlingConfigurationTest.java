package nl.logius.osdbk.configuration;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ThrottlingConfigurationTest {
    
    @InjectMocks
    private ThrottlingConfiguration throttlingConfiguration;
    
    private final String oin1 = "11111111111111111111";
    private final String oin2 = "22222222222222222222";
    
    @Test
    void throttlingConfiguration() {
        
        List<ThrottlingConfiguration.Afnemer> afnemers = new ArrayList<>();
        ThrottlingConfiguration.Afnemer afnemer1 = new ThrottlingConfiguration.Afnemer();
        afnemer1.setThrottleValue(10);
        afnemer1.setOin(oin1);
        afnemers.add(afnemer1);
        ThrottlingConfiguration.Afnemer afnemer2 = new ThrottlingConfiguration.Afnemer();
        afnemer2.setThrottleValue(5);
        afnemer2.setOin(oin2);
        afnemers.add(afnemer2);
        // Wat moet deze test bewijzen??? Of is het enkel om een line coverage te realiseren!
        throttlingConfiguration.setAfnemers(afnemers);
        
        ThrottlingConfiguration.Afnemer afnemerForOin1 = throttlingConfiguration.getAfnemers().get(0);
        ThrottlingConfiguration.Afnemer afnemerForOin2 = throttlingConfiguration.getAfnemers().get(1);
        assertEquals(afnemerForOin1, afnemer1);
        assertEquals(afnemerForOin2, afnemer2);
    }
}
