package nl.logius.osdbk.controller;

import nl.logius.osdbk.service.ThrottlingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
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

    private String blankOin = " ";
    private String oin = "11111111111111111111";

    @Test
    void getAmountOfPendingTasksForCpa() throws Exception {
        Mockito.when(throttlingService.shouldAfnemerBeThrottled(anyString())).thenReturn(Boolean.TRUE);

        mockMvc.perform(get("/throttling/" + oin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(true)));

        verify(throttlingService, times(1)).shouldAfnemerBeThrottled(oin);
    }

    @Test
    void getAmountOfPendingTasksForCpaNoCPA() throws Exception {

        mockMvc.perform(get("/throttling/" + blankOin))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("No afnemerOin provided"));

        verify(throttlingService, times(0)).shouldAfnemerBeThrottled(anyString());
    }
}