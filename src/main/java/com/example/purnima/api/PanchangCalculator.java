package com.example.purnima.api;

import com.example.purnima.model.BirthData;
import com.example.purnima.model.PanchangResult;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Interface for calculating Panchang (five elements) information.
 * Panchang consists of Tithi, Vara, Nakshatra, Yoga, and Karana.
 */
public interface PanchangCalculator {
    
    /**
     * Calculate complete Panchang for a specific date and location.
     * 
     * @param date Date for which Panchang is to be calculated
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return PanchangResult containing all five elements
     */
    PanchangResult calculatePanchang(LocalDate date, double latitude, double longitude);
    
    /**
     * Calculate complete Panchang for a specific date-time and location.
     * 
     * @param dateTime Date and time for which Panchang is to be calculated
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return PanchangResult containing all five elements
     */
    PanchangResult calculatePanchang(LocalDateTime dateTime, double latitude, double longitude);
    
    /**
     * Calculate complete Panchang using BirthData.
     * 
     * @param birthData Birth data containing date, time, and location
     * @return PanchangResult containing all five elements
     */
    PanchangResult calculatePanchang(BirthData birthData);
    
    /**
     * Calculate Tithi (lunar day) for a given date and location.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Tithi information
     */
    PanchangResult.TithiInfo calculateTithi(LocalDateTime dateTime, double latitude, double longitude);
    
    /**
     * Calculate Vara (weekday) for a given date.
     * 
     * @param date Date
     * @return Vara information
     */
    PanchangResult.VaraInfo calculateVara(LocalDate date);
    
    /**
     * Calculate Nakshatra (lunar mansion) for a given date and location.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Nakshatra information
     */
    PanchangResult.NakshatraInfo calculateNakshatra(LocalDateTime dateTime, double latitude, double longitude);
    
    /**
     * Calculate Yoga (combination of solar and lunar longitudes) for a given date and location.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Yoga information
     */
    PanchangResult.YogaInfo calculateYoga(LocalDateTime dateTime, double latitude, double longitude);
    
    /**
     * Calculate Karana (half of tithi) for a given date and location.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Karana information
     */
    PanchangResult.KaranaInfo calculateKarana(LocalDateTime dateTime, double latitude, double longitude);
    
    /**
     * Get auspicious timings (Muhurta) for a given date and location.
     * 
     * @param date Date
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Auspicious timings
     */
    PanchangResult.MuhurtaInfo getAuspiciousTimings(LocalDate date, double latitude, double longitude);
} 