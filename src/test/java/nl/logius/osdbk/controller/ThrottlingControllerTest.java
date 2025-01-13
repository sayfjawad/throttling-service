package nl.logius.osdbk.controller;

import nl.logius.osdbk.service.ThrottlingService;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.http.HttpHeaders;
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

    private final String blankOin = " ";
    private final String oin = "11111111111111111111";
    private final String username = "changeit";
    private final String password = "changeit";

    // fun part
    @DisplayName(""" 
            Given a throttling request for a specific CPA,
            when the throttling service is called,
            then it should return true and verify the service interaction once.
            """)
    @Test
    void getAmountOfPendingTasksForCpa() throws Exception {
        Mockito.when(throttlingService.shouldAfnemerBeThrottled(anyString())).thenReturn(Boolean.TRUE);

        mockMvc.perform(get("/throttling/" + oin).headers(createHeadersWithBasicAuth(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(true)));

        verify(throttlingService, times(1)).shouldAfnemerBeThrottled(oin);
    }

    @Test
    void getAmountOfPendingTasksForCpaNoCPA() throws Exception {

        mockMvc.perform(get("/throttling/" + blankOin).headers(createHeadersWithBasicAuth(username, password)))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("No afnemerOin provided"));

        verify(throttlingService, times(0)).shouldAfnemerBeThrottled(anyString());
    }

     private HttpHeaders createHeadersWithBasicAuth(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }
}