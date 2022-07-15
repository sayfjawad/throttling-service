package nl.logius.osdbk.service;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.ExecutionException;
import nl.logius.osdbk.configuration.ThrottlingConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class ThrottlingServiceTest {

    @InjectMocks
    private ThrottlingService throttlingService;

    @Mock
    private ThrottlingConfiguration throttlingConfiguration;
    
    @Mock
    JdbcTemplate jdbcTemplate;

    private String cpaId = "DGL-VERWERKEN-1-0$1-0_00000004003214345001-123456789012345678900001_928792FC79F211E8B020005056810F3E";
    private String oin = "11111111111111111111";
    private String url = "http://localhost:8080/throttling/{cpaId}";
    private static final String OIN_PREFIX = "urn:osb:oin:";
    
    @BeforeEach
    public void setup() {
        List<ThrottlingConfiguration.Afnemer> afnemers = new ArrayList<>();
        ThrottlingConfiguration.Afnemer afnemer = new ThrottlingConfiguration.Afnemer();
        afnemer.setOin(oin);
        afnemer.setThrottleValue(10);
        afnemers.add(afnemer);
        when(throttlingConfiguration.getAfnemers()).thenReturn(afnemers);
        ReflectionTestUtils.setField(throttlingService, "readyToBeSentSQL", "query1");
        ReflectionTestUtils.setField(throttlingService, "alreadySentSQL", "query2");
    }

    @Test
    void testCapacityAvailable() throws ExecutionException, InterruptedException {

        int amountOfRecordsReadyToBeSent = 1;
        int amountOfRecordsAlreadySentInLastSecond = 1;
        when(jdbcTemplate.queryForObject("query1", Integer.class, OIN_PREFIX + oin)).thenReturn(amountOfRecordsReadyToBeSent);
        when(jdbcTemplate.queryForObject("query2", Integer.class, OIN_PREFIX + oin)).thenReturn(amountOfRecordsAlreadySentInLastSecond);

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled(oin);
        
        assertEquals(true, canSent);
        verify(jdbcTemplate, times(1)).queryForObject("query1", Integer.class, OIN_PREFIX + oin);
        verify(jdbcTemplate, times(1)).queryForObject("query2", Integer.class, OIN_PREFIX + oin);
    }

    @Test
    void testNoCapacityAvailable() throws ExecutionException, InterruptedException {

        int amountOfRecordsReadyToBeSent = 1;
        int amountOfRecordsAlreadySentInLastSecond = 15;
        when(jdbcTemplate.queryForObject("query1", Integer.class, OIN_PREFIX + oin)).thenReturn(amountOfRecordsReadyToBeSent);
        when(jdbcTemplate.queryForObject("query2", Integer.class, OIN_PREFIX + oin)).thenReturn(amountOfRecordsAlreadySentInLastSecond);

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled(oin);
        
        assertEquals(false, canSent);
        verify(jdbcTemplate, times(1)).queryForObject("query1", Integer.class, OIN_PREFIX + oin);
        verify(jdbcTemplate, times(1)).queryForObject("query2", Integer.class, OIN_PREFIX + oin);
    }

    @Test
    void testAfnemerNotConfiguredForThrottling() throws ExecutionException, InterruptedException {

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled("12345");
        
        assertEquals(true, canSent);
        verify(jdbcTemplate, times(0)).queryForObject("query1", Integer.class, OIN_PREFIX + oin);
        verify(jdbcTemplate, times(0)).queryForObject("query2", Integer.class, OIN_PREFIX + oin);
    }
}