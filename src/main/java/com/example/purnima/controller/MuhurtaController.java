package com.example.purnima.controller;

import com.example.purnima.api.MuhurtaCalculator;
import com.example.purnima.model.MuhurtaResult;
import com.example.purnima.service.DefaultMuhurtaCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/muhurta")
public class MuhurtaController {

    private final MuhurtaCalculator muhurtaCalculator;

    @Autowired
    public MuhurtaController(MessageSource messageSource) {
        this.muhurtaCalculator = new DefaultMuhurtaCalculator(messageSource);
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
