package uk.co.lewisodriscoll.haclient.service;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.lewisodriscoll.haclient.helper.ColourHelper;
import uk.co.lewisodriscoll.haclient.model.HaAction;
import uk.co.lewisodriscoll.haclient.model.HaResponse;

import java.awt.Color;

@Service
public class ActionIngestionService {

    private EasybulbService easybulbService;

    private Logger log = Logger.getLogger(ActionIngestionService.class);

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
                String[] parts = action.getValue().split(", ");

                if (parts.length != 3) {
                    log.error("Invalid colour: " + action.getAction());
                }

                float[] hsb = {0.0f, 0.0f, 0.0f};
                try {
                    for (int i = 0; i < 3; i++) {
                        hsb[i] = Float.parseFloat(parts[i]);
                    }
                } catch (NumberFormatException e) {
                    log.error("Invalid float format");
                    log.error(e);
                }
                Color colour = Color.getHSBColor(hsb[0] / 360.0f, hsb[1], hsb[2]);

                // Easybulb doesn't handle low saturation well - turn white
                if (hsb[1] < 0.5f) {
                    return easybulbService.turnLightWhite();
                }

                return easybulbService.setLightColour(ColourHelper.colourToEasybulbHue(colour));

            case "SetBrightness":
                int percentage = Integer.parseInt(action.getValue());
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
