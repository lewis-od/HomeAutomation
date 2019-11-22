package uk.co.lewisodriscoll.haclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.lewisodriscoll.haclient.domain.HaColour;
import uk.co.lewisodriscoll.haclient.model.HaAction;
import uk.co.lewisodriscoll.haclient.model.HaResponse;
import uk.co.lewisodriscoll.haclient.service.ActionIngestionService;

import java.awt.*;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LocalHaController {

    private ActionIngestionService ingestionService;

    @Autowired
    public LocalHaController(ActionIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @GetMapping("/on")
    public ResponseEntity<HaResponse> on() {
        HaAction action = HaAction.builder()
                .service("easybulb")
                .action("TurnOn")
                .build();

        return responseToEntity(ingestionService.ingest(action));
    }

    @GetMapping("/off")
    public ResponseEntity<HaResponse> off() {
        HaAction action = HaAction.builder()
                .service("easybulb")
                .action("TurnOff")
                .build();

        return responseToEntity(ingestionService.ingest(action));
    }

    @GetMapping("/white")
    public ResponseEntity<HaResponse> white() {
        HaColour whiteColour = new HaColour(Color.WHITE);
        HaAction action = HaAction.builder()
                .service("easybulb")
                .action("SetColor")
                .value(whiteColour.toString())
                .build();

        return responseToEntity(ingestionService.ingest(action));
    }

    @GetMapping("/colour")
    public ResponseEntity<HaResponse> colour(@RequestParam int r, @RequestParam int g, @RequestParam int b) {
        HaColour colour = new HaColour(r, g, b);
        HaAction action = HaAction.builder()
                .service("easybulb")
                .action("SetColor")
                .value(colour.toString())
                .build();

        return responseToEntity(ingestionService.ingest(action));
    }

    @GetMapping("/brightness")
    public ResponseEntity<HaResponse> brightness(@RequestParam int percentage) {
        HaAction action = HaAction.builder()
                .service("easybulb")
                .action("SetBrightness")
                .value(String.valueOf(percentage))
                .build();

        return responseToEntity(ingestionService.ingest(action));
    }

    private ResponseEntity<HaResponse> responseToEntity(HaResponse response) {
        if (response.getStatus() == HaResponse.Status.SUCCESS) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(402).body(response);
    }

}
