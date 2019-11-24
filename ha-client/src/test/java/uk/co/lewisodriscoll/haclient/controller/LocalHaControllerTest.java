package uk.co.lewisodriscoll.haclient.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.lewisodriscoll.haclient.HaClientApplication;
import uk.co.lewisodriscoll.haclient.domain.HaColour;
import uk.co.lewisodriscoll.haclient.helper.BrightnessHelper;
import uk.co.lewisodriscoll.haclient.model.HaAction;
import uk.co.lewisodriscoll.haclient.model.HaResponse;
import uk.co.lewisodriscoll.haclient.service.ActionIngestionService;

import java.awt.Color;

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

    private HaAction givenOffAction() {
        return HaAction.builder()
                .service("easybulb")
                .action("TurnOff")
                .build();
    }

    private final Color givenColour = new Color(255, 164, 48);
    private HaAction givenSetColourAction() {
        HaColour givenHaColour = new HaColour(givenColour);
        return HaAction.builder()
                .service("easybulb")
                .action("SetColor")
                .value(givenHaColour.toString())
                .build();
    }

    private final int givenBrightness = 75;
    private HaAction givenSetBrightnesAction() {
        return HaAction.builder()
                .service("easybulb")
                .action("SetBrightness")
                .value(String.valueOf(givenBrightness))
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

    @Test
    public void testGetOff() throws Exception {
        given(ingestionService.ingest(givenOffAction())).willReturn(successResponse());

        mockMvc.perform(get("/api/off")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").isString());
    }

    @Test
    public void testGetColour() throws Exception {
        given(ingestionService.ingest(givenSetColourAction())).willReturn(successResponse());

        String endpoint = String.format("/api/colour?r=%s&g=%s&b=%s",
                givenColour.getRed(), givenColour.getGreen(), givenColour.getBlue());

        mockMvc.perform(get(endpoint)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").isString());
    }

    @Test
    public void testGetBrightness() throws Exception {
        given(ingestionService.ingest(givenSetBrightnesAction())).willReturn(successResponse());

        String endpoint = String.format("/api/brightness?percentage=%d", givenBrightness);

        mockMvc.perform(get(endpoint)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").isString());
    }

}
