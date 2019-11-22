package uk.co.lewisodriscoll.haclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.lewisodriscoll.haclient.domain.HaColour;
import uk.co.lewisodriscoll.haclient.exception.InvalidColourFormatException;
import uk.co.lewisodriscoll.haclient.helper.BrightnessHelper;
import uk.co.lewisodriscoll.haclient.model.HaAction;
import uk.co.lewisodriscoll.haclient.model.HaResponse;

@Service
public class ActionIngestionService {

    private final EasybulbService easybulbService;

    private final Logger log = LoggerFactory.getLogger(ActionIngestionService.class);

    @Autowired
    public ActionIngestionService(EasybulbService easybulbService) {
        this.easybulbService = easybulbService;
    }

    public HaResponse ingest(final HaAction action) {
        log.trace("Ingesting action: " + action.toString());

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

    private HaResponse ingestEasybulbAction(final HaAction action) {
        switch (action.getAction()) {
            case "TurnOn":
                return easybulbService.turnLightOn();

            case "TurnOff":
                return easybulbService.turnLightOff();

            case "SetColor":
                HaColour colour = null;
                try {
                    colour = new HaColour(action.getValue());
                } catch (InvalidColourFormatException e) {
                    return HaResponse.builder()
                            .status(HaResponse.Status.ERROR)
                            .message(e.getMessage())
                            .build();
                }

                // Easybulb doesn't handle low saturation well - turn white
                if (colour.getSaturation() < 0.5f) {
                    return easybulbService.turnLightWhite();
                }

                return easybulbService.setLightColour(colour.getEasybulbHue());

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

                return easybulbService.setLightBrightness(BrightnessHelper.percentageToEasybulb(percentage));

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
