package uk.co.lewisodriscoll.haclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.lewisodriscoll.haclient.model.HaResponse;
import uk.co.lewisodriscoll.haclient.service.EasyBulbService;

import java.awt.*;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LocalHaController {

    private EasyBulbService easyBulbService;

    @Autowired
    public LocalHaController(EasyBulbService easyBulbService) {
        this.easyBulbService = easyBulbService;
    }

    @RequestMapping("/on")
    public ResponseEntity<HaResponse> on() {
        return ResponseEntity.ok(easyBulbService.turnLightOn());
    }

    @RequestMapping("/off")
    public ResponseEntity<HaResponse> off() {
        return ResponseEntity.ok(easyBulbService.turnLightOff());
    }

    @RequestMapping("/white")
    public ResponseEntity<HaResponse> white() {
        return ResponseEntity.ok(easyBulbService.turnLightWhite());
    }

    @RequestMapping("/colour")
    public ResponseEntity<HaResponse> colour(@RequestParam int r, @RequestParam int g, @RequestParam int b) {
        Color colour = new Color(r, g, b);
        return ResponseEntity.ok(easyBulbService.setLightColour(colour));
    }

}
