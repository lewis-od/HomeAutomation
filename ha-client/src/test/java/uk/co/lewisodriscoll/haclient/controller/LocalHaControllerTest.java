package uk.co.lewisodriscoll.haclient.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.lewisodriscoll.haclient.HaClientApplication;
import uk.co.lewisodriscoll.haclient.model.HaAction;
import uk.co.lewisodriscoll.haclient.model.HaResponse;
import uk.co.lewisodriscoll.haclient.service.ActionIngestionService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = HaClientApplication.class)
@WebMvcTest(controllers = LocalHaController.class)
public class LocalHaControllerTest {

    private HaAction givenOnAction() {
        return HaAction.builder()
                .service("easybulb")
                .action("TurnOn")
                .build();
    }

    private HaResponse successResponse() {
        return HaResponse.builder()
                .status(HaResponse.Status.SUCCESS)
                .message("Action completed")
                .build();
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActionIngestionService ingestionService;

    @Test
    public void testGetOn() throws Exception {
        given(ingestionService.ingest(givenOnAction())).willReturn(successResponse());

        mockMvc.perform(get("/api/on")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").isString());
    }

}
