package uk.co.lewisodriscoll.haclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.lewisodriscoll.haclient.model.HaResponse;
import uk.co.lewisodriscoll.haclient.service.EasyBulbService;

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

}
