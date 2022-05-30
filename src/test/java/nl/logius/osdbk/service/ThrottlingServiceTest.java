package nl.logius.osdbk.service;

import nl.logius.osdbk.persistence.repository.EbmsMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThrottlingServiceTest {

    @InjectMocks
    private ThrottlingService throttlingService;

    @Mock
    private EbmsMessageRepository ebmsMessageRepository;

    private String cpaId = "DGL-VERWERKEN-1-0$1-0_00000004003214345001-123456789012345678900001_928792FC79F211E8B020005056810F3E";

    @Test
    void getAmountOfRecordsReadyToBeSentForCpa() throws ExecutionException, InterruptedException {

        Long amountOfRecordsReadyToBeSentForCpa = 1L;
        when(ebmsMessageRepository.getAmountOfRecordsReadyToBeSentForCpa(cpaId)).thenReturn(amountOfRecordsReadyToBeSentForCpa);

        //test
        Integer amount = throttlingService.getAmountOfRecordsReadyToBeSentForCpa(cpaId).get();
        assertEquals(1, amount);
        verify(ebmsMessageRepository, times(1)).getAmountOfRecordsReadyToBeSentForCpa(cpaId);
    }

    @Test
    void getAmountOfRecordsAlreadySentInLastSecondForCpa() throws ExecutionException, InterruptedException {

        Long amountOfRecordsAlreadySentInLastSecondForCpa = 2L;
        when(ebmsMessageRepository.getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaId)).thenReturn(amountOfRecordsAlreadySentInLastSecondForCpa);

        //test
        Integer amount = throttlingService.getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaId).get();
        assertEquals(2, amount);
        verify(ebmsMessageRepository, times(1)).getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaId);
    }
}