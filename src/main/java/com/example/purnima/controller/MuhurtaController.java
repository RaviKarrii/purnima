package com.example.purnima.controller;

import com.example.purnima.api.MuhurtaCalculator;
import com.example.purnima.model.MuhurtaResult;
import com.example.purnima.service.DefaultMuhurtaCalculator;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/muhurta")
public class MuhurtaController {

    private final MuhurtaCalculator muhurtaCalculator;

    public MuhurtaController() {
        this.muhurtaCalculator = new DefaultMuhurtaCalculator();
    }

    @GetMapping("/calculate")
    public MuhurtaResult calculateMuhurta(
            @RequestParam String date,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false) String zoneId) {
        
        LocalDate localDate = LocalDate.parse(date);
        ZoneId zone = zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
        
        return muhurtaCalculator.calculateMuhurta(localDate, latitude, longitude, zone);
    }
}
