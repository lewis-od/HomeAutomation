package uk.co.lewisodriscoll.haclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.lewisodriscoll.haclient.helper.ColourHelper;
import uk.co.lewisodriscoll.haclient.model.HaAction;
import uk.co.lewisodriscoll.haclient.model.HaResponse;

import java.awt.Color;

@Service
public class ActionIngestionService {

    private EasybulbService easybulbService;

    private Logger log = LoggerFactory.getLogger(ActionIngestionService.class);

    @Autowired
    public ActionIngestionService(EasybulbService easybulbService) {
        this.easybulbService = easybulbService;
    }

    public HaResponse ingest(HaAction action) {
        log.info("Ingesting action: " + action.toString());

        switch (action.getService()) {
            case "easybulb":
                return ingestEasybulbAction(action);

            default:
                String errorMessage = "Unknown service: " + action.getService();
                log.error(errorMessage);
                return HaResponse.builder()
                        .status(HaResponse.Status.ERROR)
                        .message(errorMessage)
                        .build();
        }
    }

    private HaResponse ingestEasybulbAction(HaAction action) {
        switch (action.getAction()) {
            case "TurnOn":
                return easybulbService.turnLightOn();

            case "TurnOff":
                return easybulbService.turnLightOff();

            case "SetColor":
                Color colour = null;
                try {
                    colour = ColourHelper.stringToColour(action.getValue());
                } catch (NumberFormatException e) {
                    return HaResponse.builder()
                            .status(HaResponse.Status.ERROR)
                            .message("Invalid colour format: " + action.getValue())
                            .build();
                }

                float saturation = ColourHelper.getSaturation(colour);

                // Easybulb doesn't handle low saturation well - turn white
                if (saturation < 0.5f) {
                    return easybulbService.turnLightWhite();
                }

                return easybulbService.setLightColour(ColourHelper.colourToEasybulbHue(colour));

            case "SetBrightness":
                int percentage;
                try {
                    percentage = Integer.parseInt(action.getValue());
                } catch (NumberFormatException e) {
                    return HaResponse.builder()
                            .status(HaResponse.Status.ERROR)
                            .message("Invalid brightness: " + action.getValue())
                            .build();
                }

                return easybulbService.setLightBrightness(ColourHelper.percentageToEasybulb(percentage));

            default:
                String errorMessage = "Unknown action: " + action.getAction();
                log.error(errorMessage);
                return HaResponse.builder()
                        .status(HaResponse.Status.ERROR)
                        .message(errorMessage)
                        .build();
        }
    }

}
