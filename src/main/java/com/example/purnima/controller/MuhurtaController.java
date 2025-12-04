package com.example.purnima.controller;

import com.example.purnima.api.MuhurtaCalculator;
import com.example.purnima.model.MuhurtaResult;
import com.example.purnima.service.DefaultMuhurtaCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import com.example.purnima.model.MuhurtaSlot;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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
    @GetMapping("/vehicle")
    public List<MuhurtaSlot> findVehicleMuhurta(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false) String zoneId) {
        
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        ZoneId zone = zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
        
        return muhurtaCalculator.findVehiclePurchaseMuhurta(startTime, endTime, latitude, longitude, zone);
    }

    @GetMapping("/marriage")
    public List<MuhurtaSlot> findMarriageMuhurta(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false) String zoneId) {
        
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        ZoneId zone = zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
        
        return muhurtaCalculator.findMarriageMuhurta(startTime, endTime, latitude, longitude, zone);
    }

    @GetMapping("/griha-pravesh")
    public List<MuhurtaSlot> findGrihaPraveshMuhurta(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false) String zoneId) {
        
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        ZoneId zone = zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
        
        return muhurtaCalculator.findGrihaPraveshMuhurta(startTime, endTime, latitude, longitude, zone);
    }

    @GetMapping("/business")
    public List<MuhurtaSlot> findNewBusinessMuhurta(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false) String zoneId) {
        
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        ZoneId zone = zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
        
        return muhurtaCalculator.findNewBusinessMuhurta(startTime, endTime, latitude, longitude, zone);
    }

    @GetMapping("/namakarana")
    public List<MuhurtaSlot> findNamakaranaMuhurta(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false) String zoneId) {
        
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        ZoneId zone = zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
        
        return muhurtaCalculator.findNamakaranaMuhurta(startTime, endTime, latitude, longitude, zone);
    }

    @GetMapping("/property")
    public List<MuhurtaSlot> findPropertyPurchaseMuhurta(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false) String zoneId) {
        
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        ZoneId zone = zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
        
        return muhurtaCalculator.findPropertyPurchaseMuhurta(startTime, endTime, latitude, longitude, zone);
    }
}
