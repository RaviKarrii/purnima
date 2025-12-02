package com.example.purnima.controller;

import com.example.purnima.PurnimaAstrology;
import com.example.purnima.model.AsthakootResult;
import com.example.purnima.model.BirthData;
import com.example.purnima.model.ChartResult;
import com.example.purnima.model.PanchangResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class AstrologyController {

    private final PurnimaAstrology astrology;

    @Autowired
    public AstrologyController(PurnimaAstrology astrology) {
        this.astrology = astrology;
    }

    @GetMapping("/panchang")
    public ResponseEntity<PanchangResult> getPanchang(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam String placeName,
            @RequestParam(required = false) String timezone) {
        
        PanchangResult result;
        if (timezone != null && !timezone.isEmpty()) {
            try {
                java.time.ZoneId zoneId = java.time.ZoneId.of(timezone);
                result = astrology.calculatePanchang(date, latitude, longitude, placeName, zoneId);
            } catch (Exception e) {
                // Fallback to default if timezone is invalid
                result = astrology.calculatePanchang(date, latitude, longitude, placeName);
            }
        } else {
            result = astrology.calculatePanchang(date, latitude, longitude, placeName);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/chart")
    public ResponseEntity<ChartResult> getBirthChart(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime birthTime,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam String placeName) {
        
        BirthData birthData = PurnimaAstrology.createBirthData(birthTime, latitude, longitude, placeName);
        ChartResult result = astrology.generateBirthChart(birthData);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/compatibility")
    public ResponseEntity<AsthakootResult> getCompatibility(@RequestBody CompatibilityRequest request) {
        AsthakootResult result = astrology.calculateAsthakoot(request.getMaleBirthData(), request.getFemaleBirthData());
        return ResponseEntity.ok(result);
    }
    
    // Inner class for request body
    public static class CompatibilityRequest {
        private BirthData maleBirthData;
        private BirthData femaleBirthData;

        // Getters and Setters
        public BirthData getMaleBirthData() { return maleBirthData; }
        public void setMaleBirthData(BirthData maleBirthData) { this.maleBirthData = maleBirthData; }
        
        public BirthData getFemaleBirthData() { return femaleBirthData; }
        public void setFemaleBirthData(BirthData femaleBirthData) { this.femaleBirthData = femaleBirthData; }
    }
}
