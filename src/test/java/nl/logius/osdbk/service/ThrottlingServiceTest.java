package nl.logius.osdbk.service;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import nl.logius.osdbk.configuration.ThrottlingConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThrottlingServiceTest {

    @InjectMocks
    private ThrottlingService throttlingService;

    @Mock
    private ThrottlingConfiguration throttlingConfiguration;
    
    @Mock
    AsyncService asyncService;

    private final String oin = "11111111111111111111";

    @BeforeEach
    public void setup() {
        List<ThrottlingConfiguration.Afnemer> afnemers = new ArrayList<>();
        ThrottlingConfiguration.Afnemer afnemer = new ThrottlingConfiguration.Afnemer();
        afnemer.setOin(oin);
        afnemer.setThrottleValue(10);
        afnemers.add(afnemer);
        when(throttlingConfiguration.getAfnemers()).thenReturn(afnemers);
    }

    @Test
    void testCapacityAvailable() {

        int amountOfRecordsReadyToBeSent = 1;
        int amountOfRecordsAlreadySentInLastSecond = 1;
        when(asyncService.getAmountOfRecordsReadyToBeSentForAfnemer(oin)).thenReturn(CompletableFuture.completedFuture(amountOfRecordsReadyToBeSent));
        when(asyncService.getAmountOfRecordsAlreadySentInLastSecondForAfnemer(oin)).thenReturn(CompletableFuture.completedFuture(amountOfRecordsAlreadySentInLastSecond));

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled(oin);

        assertTrue(canSent);
        verify(asyncService, times(1)).getAmountOfRecordsReadyToBeSentForAfnemer(oin);
        verify(asyncService, times(1)).getAmountOfRecordsAlreadySentInLastSecondForAfnemer(oin);
    }

    @Test
    void testNoCapacityAvailable() {

        int amountOfRecordsReadyToBeSent = 1;
        int amountOfRecordsAlreadySentInLastSecond = 15;
        when(asyncService.getAmountOfRecordsReadyToBeSentForAfnemer(oin)).thenReturn(CompletableFuture.completedFuture(amountOfRecordsReadyToBeSent));
        when(asyncService.getAmountOfRecordsAlreadySentInLastSecondForAfnemer(oin)).thenReturn(CompletableFuture.completedFuture(amountOfRecordsAlreadySentInLastSecond));

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled(oin);

        assertFalse(canSent);
        verify(asyncService, times(1)).getAmountOfRecordsReadyToBeSentForAfnemer(oin);
        verify(asyncService, times(1)).getAmountOfRecordsAlreadySentInLastSecondForAfnemer(oin);
    }

    @Test
    void testAfnemerNotConfiguredForThrottling() {

        //test
        boolean canSent = throttlingService.shouldAfnemerBeThrottled("12345");

        assertTrue(canSent);
        verify(asyncService, times(0)).getAmountOfRecordsReadyToBeSentForAfnemer(oin);
        verify(asyncService, times(0)).getAmountOfRecordsAlreadySentInLastSecondForAfnemer(oin);
    }
}