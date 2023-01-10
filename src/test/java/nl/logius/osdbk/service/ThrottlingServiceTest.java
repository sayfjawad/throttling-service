package nl.logius.osdbk.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;
import nl.logius.osdbk.configuration.ThrottlingConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@SpringBootTest
class ThrottlingServiceTest {

    @InjectMocks
    private ThrottlingService throttlingService;

    @Mock
    private ThrottlingConfiguration throttlingConfiguration;

    @Mock
    NamedParameterJdbcTemplate jdbcTemplate;

    private final String oin = "11111111111111111111";
    private static final String OIN_PREFIX = "urn:osb:oin:";
    Map<String, String> parameters;

    @BeforeEach
    public void setup() {
        List<ThrottlingConfiguration.Afnemer> afnemers = new ArrayList<>();
        ThrottlingConfiguration.Afnemer afnemer = new ThrottlingConfiguration.Afnemer();
        afnemer.setOin(oin);
        afnemer.setThrottleValue(10);
        afnemers.add(afnemer);
        when(throttlingConfiguration.getAfnemers()).thenReturn(afnemers);
        parameters = new HashMap<>();
        parameters.put("afnemerOin", OIN_PREFIX + oin);
        ReflectionTestUtils.setField(throttlingService, "combinedCountSql", "query1");
    }

    @Test
    void testCapacityAvailable() {

        int combinedRecordsCount = 1;
        when(jdbcTemplate.queryForObject("query1", parameters,Integer.class)).thenReturn(combinedRecordsCount);

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled(oin);

        assertTrue(canSent);
        verify(jdbcTemplate, times(1)).queryForObject("query1", parameters,Integer.class);
    }

    @Test
    void testNoCapacityAvailable() {

        int combinedRecordsCount = 16;
        when(jdbcTemplate.queryForObject("query1", parameters,Integer.class)).thenReturn(combinedRecordsCount);

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled(oin);

        assertFalse(canSent);
        verify(jdbcTemplate, times(1)).queryForObject("query1", parameters,Integer.class);
    }

    @Test
    void testAfnemerNotConfiguredForThrottling() {

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled("12345");

        assertTrue(canSent);
        verify(jdbcTemplate, times(0)).queryForObject("query1", parameters,Integer.class);
    }
}