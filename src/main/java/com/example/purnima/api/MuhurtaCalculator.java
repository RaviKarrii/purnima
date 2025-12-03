package com.example.purnima.api;

import com.example.purnima.model.MuhurtaResult;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Interface for Muhurta calculations.
 */
public interface MuhurtaCalculator {
    
    /**
     * Calculate Muhurta details for a given date and location.
     * 
     * @param date The date
     * @param latitude Latitude
     * @param longitude Longitude
     * @param zoneId TimeZone
     * @return MuhurtaResult
     */
    MuhurtaResult calculateMuhurta(LocalDate date, double latitude, double longitude, ZoneId zoneId);
}
