package com.example.purnima.api;

import com.example.purnima.model.MuhurtaResult;
import com.example.purnima.model.MuhurtaSlot;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Interface for Muhurta calculations.
 */
public interface MuhurtaCalculator {
    MuhurtaResult calculateMuhurta(LocalDate date, double latitude, double longitude, ZoneId zoneId);
    
    List<MuhurtaSlot> findVehiclePurchaseMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId);
    
    List<MuhurtaSlot> findMarriageMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId);
    
    List<MuhurtaSlot> findGrihaPraveshMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId);
    
    List<MuhurtaSlot> findNewBusinessMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId);
    
    List<MuhurtaSlot> findNamakaranaMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId);
    
    List<MuhurtaSlot> findPropertyPurchaseMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId);
}
