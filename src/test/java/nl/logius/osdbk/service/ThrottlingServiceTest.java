package nl.logius.osdbk.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThrottlingServiceTest {

    @InjectMocks
    private ThrottlingService throttlingService;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Value("${spring.throttling-sql.ready-to-send}")
    private String readyToBeSentSQL;

    @Value("${spring.throttling-sql.already-sent}")
    private String alreadySentSQL;

    private String cpaId = "DGL-VERWERKEN-1-0$1-0_00000004003214345001-123456789012345678900001_928792FC79F211E8B020005056810F3E";

    @Test
    void getAmountOfRecordsReadyToBeSentForCpa() throws ExecutionException, InterruptedException {

        int amountOfRecordsReadyToBeSentForCpa = 1;
        when(jdbcTemplate.queryForObject(readyToBeSentSQL, Integer.class, cpaId)).thenReturn(amountOfRecordsReadyToBeSentForCpa);

        //test
        Integer amount = throttlingService.getAmountOfRecordsReadyToBeSentForCpa(cpaId).get();
        assertEquals(1, amount);
        verify(jdbcTemplate, times(1)).queryForObject(readyToBeSentSQL, Integer.class, cpaId);
    }

    @Test
    void getAmountOfRecordsAlreadySentInLastSecondForCpa() throws ExecutionException, InterruptedException {

        int amountOfRecordsAlreadySentInLastSecondForCpa = 2;
        when(jdbcTemplate.queryForObject(alreadySentSQL, Integer.class, cpaId)).thenReturn(amountOfRecordsAlreadySentInLastSecondForCpa);

        //test
        Integer amount = throttlingService.getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaId).get();
        assertEquals(2, amount);
        verify(jdbcTemplate, times(1)).queryForObject(alreadySentSQL, Integer.class, cpaId);
    }
}