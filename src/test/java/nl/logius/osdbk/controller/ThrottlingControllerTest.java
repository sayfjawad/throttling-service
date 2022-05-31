package nl.logius.osdbk.controller;

import nl.logius.osdbk.service.ThrottlingService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ThrottlingController.class)
class ThrottlingControllerTest {

    @MockBean
    private ThrottlingService throttlingService;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CompletableFuture<Integer> amountOfRecordsReadyToBeSentForCpa;

    @Mock
    private CompletableFuture<Integer> amountOfRecordsAlreadySentInLastSecondForCpa;

    private String cpaId = "DGL-VERWERKEN-1-0$1-0_00000004003214345001-123456789012345678900001_928792FC79F211E8B020005056810F3E";
    private String blankCpaId = " ";

    @BeforeEach
    public void setUp() throws Exception {

        amountOfRecordsReadyToBeSentForCpa = CompletableFuture.completedFuture(1);
        amountOfRecordsAlreadySentInLastSecondForCpa = CompletableFuture.completedFuture(2);
    }

    @Test
    void getAmountOfPendingTasksForCpa() throws Exception {
        Mockito.when(throttlingService.getAmountOfRecordsReadyToBeSentForCpa(cpaId)).thenReturn(amountOfRecordsReadyToBeSentForCpa);
        Mockito.when(throttlingService.getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaId)).thenReturn(amountOfRecordsAlreadySentInLastSecondForCpa);

        mockMvc.perform(get("/throttling/" + cpaId + "/pendingTasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.amountOfPendingTasks", Matchers.is(3)));

        verify(throttlingService, times(1)).getAmountOfRecordsReadyToBeSentForCpa(cpaId);
        verify(throttlingService, times(1)).getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaId);
    }

    @Test
    void getAmountOfPendingTasksForCpaNoCPA() throws Exception {

        mockMvc.perform(get("/throttling/" + blankCpaId + "/pendingTasks"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("No cpaId provided"));

        verify(throttlingService, times(0)).getAmountOfRecordsReadyToBeSentForCpa(cpaId);
        verify(throttlingService, times(0)).getAmountOfRecordsAlreadySentInLastSecondForCpa(cpaId);
    }
}