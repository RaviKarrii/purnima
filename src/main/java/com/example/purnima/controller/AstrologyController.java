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
            @RequestParam String placeName) {
        
        PanchangResult result = astrology.calculatePanchang(date, latitude, longitude, placeName);
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
        BirthData maleData = PurnimaAstrology.createBirthData(
                request.getMaleBirthTime(), 
                request.getMaleLatitude(), 
                request.getMaleLongitude(), 
                request.getMalePlaceName());
        
        BirthData femaleData = PurnimaAstrology.createBirthData(
                request.getFemaleBirthTime(), 
                request.getFemaleLatitude(), 
                request.getFemaleLongitude(), 
                request.getFemalePlaceName());
        
        AsthakootResult result = astrology.calculateAsthakoot(maleData, femaleData);
        return ResponseEntity.ok(result);
    }
    
    // Inner class for request body
    public static class CompatibilityRequest {
        private LocalDateTime maleBirthTime;
        private double maleLatitude;
        private double maleLongitude;
        private String malePlaceName;
        
        private LocalDateTime femaleBirthTime;
        private double femaleLatitude;
        private double femaleLongitude;
        private String femalePlaceName;

        // Getters and Setters
        public LocalDateTime getMaleBirthTime() { return maleBirthTime; }
        public void setMaleBirthTime(LocalDateTime maleBirthTime) { this.maleBirthTime = maleBirthTime; }
        public double getMaleLatitude() { return maleLatitude; }
        public void setMaleLatitude(double maleLatitude) { this.maleLatitude = maleLatitude; }
        public double getMaleLongitude() { return maleLongitude; }
        public void setMaleLongitude(double maleLongitude) { this.maleLongitude = maleLongitude; }
        public String getMalePlaceName() { return malePlaceName; }
        public void setMalePlaceName(String malePlaceName) { this.malePlaceName = malePlaceName; }

        public LocalDateTime getFemaleBirthTime() { return femaleBirthTime; }
        public void setFemaleBirthTime(LocalDateTime femaleBirthTime) { this.femaleBirthTime = femaleBirthTime; }
        public double getFemaleLatitude() { return femaleLatitude; }
        public void setFemaleLatitude(double femaleLatitude) { this.femaleLatitude = femaleLatitude; }
        public double getFemaleLongitude() { return femaleLongitude; }
        public void setFemaleLongitude(double femaleLongitude) { this.femaleLongitude = femaleLongitude; }
        public String getFemalePlaceName() { return femalePlaceName; }
        public void setFemalePlaceName(String femalePlaceName) { this.femalePlaceName = femalePlaceName; }
    }
}
