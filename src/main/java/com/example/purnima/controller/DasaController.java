package com.example.purnima.controller;

import com.example.purnima.api.DasaCalculator;
import com.example.purnima.model.BirthData;
import com.example.purnima.model.DasaResult;
import com.example.purnima.service.VimshottariDasaCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/dasa")
public class DasaController {

    private final DasaCalculator dasaCalculator;

    @Autowired
    public DasaController(MessageSource messageSource) {
        this.dasaCalculator = new VimshottariDasaCalculator(messageSource);
    }

    @GetMapping("/vimshottari")
    public List<DasaResult> getVimshottariDasa(
            @RequestParam String birthTime,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "Unknown") String placeName,
            @RequestParam(required = false) String zoneId) {
        
        LocalDateTime dateTime = LocalDateTime.parse(birthTime);
        ZoneId zone = zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
        
        BirthData birthData = new BirthData(dateTime, latitude, longitude, placeName, zone);
        return dasaCalculator.calculateMahadasas(birthData);
    }
    
    @GetMapping("/current")
    public DasaResult getCurrentDasa(
            @RequestParam String birthTime,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "Unknown") String placeName,
            @RequestParam(required = false) String zoneId) {
        
        LocalDateTime dateTime = LocalDateTime.parse(birthTime);
        ZoneId zone = zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
        
        BirthData birthData = new BirthData(dateTime, latitude, longitude, placeName, zone);
        return dasaCalculator.getCurrentDasa(birthData);
    }
}
