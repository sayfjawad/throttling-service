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
        final var afnemers = new ArrayList<ThrottlingConfiguration.Afnemer>();
        final var afnemer = new ThrottlingConfiguration.Afnemer();
        afnemer.setOin(oin);
        afnemer.setThrottleValue(10);
        afnemers.add(afnemer);
        when(throttlingConfiguration.getAfnemers()).thenReturn(afnemers);
        parameters = new HashMap<>();
        parameters.put("afnemerOin", OIN_PREFIX + oin);
        ReflectionTestUtils.setField(throttlingService, "combinedCountSQL", "query1");
    }

    @Test
    void testCapacityAvailable() {

        int combinedRecordsCount = 1;
        when(jdbcTemplate.queryForObject("query1", parameters,Integer.class)).thenReturn(combinedRecordsCount);

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled(oin);

        assertTrue(canSent);
        verify(jdbcTemplate, times(1))
                .queryForObject("query1", parameters,Integer.class);
    }

    @Test
    void testNoCapacityAvailable() {

        int combinedRecordsCount = 16;
        when(jdbcTemplate.queryForObject("query1", parameters,Integer.class))
                .thenReturn(combinedRecordsCount);

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled(oin);

        assertFalse(canSent);
        verify(jdbcTemplate, times(1)).queryForObject("query1", parameters,Integer.class);
    }

    /**
     * Tests the scenario where an afnemer (client) is not configured for throttling.
     *
     * This test verifies the behavior of the {@code shouldAfnemerBeThrottled} method
     * when a valid OIN (Organisatie Identificatie Nummer) that is not part of the
     * throttling configuration list is provided as an argument.
     *
     * The expected result is that the method allows the message to be sent,
     * returning {@code true}. Additionally, the method should not query the database
     * for any throttling-related information.
     *
     * Assertions:
     * - The method returns {@code true} when the afnemer is not configured for throttling.
     * - The database query related to throttling is not executed.
     *
     * Mocks and Verifications:
     * - Ensures that the {@code queryForObject} method of {@code jdbcTemplate}
     *   is not invoked.
     */
    @Test
    void testAfnemerNotConfiguredForThrottling() {

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled("12345");

        assertTrue(canSent);
        verify(jdbcTemplate, times(0))
                .queryForObject("query1", parameters,Integer.class);
    }
}