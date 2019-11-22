package uk.co.lewisodriscoll.haclient.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.lewisodriscoll.haclient.domain.HaColour;
import uk.co.lewisodriscoll.haclient.model.HaAction;

import java.awt.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ActionIngestionServiceTest {

    private static final HaColour givenColour = new HaColour(80, 160, 33);

    private HaAction givenTurnOnAction() {
        return HaAction.builder()
                .service("easybulb")
                .action("TurnOn")
                .build();
    }

    private HaAction givenTurnOffAction() {
        return HaAction.builder()
                .service("easybulb")
                .action("TurnOff")
                .build();
    }

    private HaAction givenSetColorAction() {
        return HaAction.builder()
                .service("easybulb")
                .action("SetColor")
                .value(givenColour.toString())
                .build();
    }

    private HaAction givenSetWhiteAction() {
        return HaAction.builder()
                .service("easybulb")
                .action("SetColor")
                .value((new HaColour(Color.WHITE)).toString())
                .build();
    }

    @Mock
    private EasybulbService easybulbService;

    @InjectMocks
    private ActionIngestionService actionIngestionService;

    @Before
    public void setup() {
        this.actionIngestionService = new ActionIngestionService(easybulbService);
    }

    @Test
    public void testIngestsTurnOnAction() {
        actionIngestionService.ingest(givenTurnOnAction());
        verify(easybulbService, times(1)).turnLightOn();
    }

    @Test
    public void testIngestsTurnOffAction() {
        actionIngestionService.ingest(givenTurnOffAction());
        verify(easybulbService, times(1)).turnLightOff();
    }

    @Test
    public void testIngestsSetColorAction() {
        actionIngestionService.ingest(givenSetColorAction());
        verify(easybulbService, times(1)).setLightColour(givenColour.getEasybulbHue());
    }

    @Test
    public void testIngestsSetWhiteAction() {
        actionIngestionService.ingest(givenSetWhiteAction());
        verify(easybulbService, times(1)).turnLightWhite();
    }

}
