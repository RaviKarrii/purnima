package com.example.purnima;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;
import com.example.purnima.model.BirthData;

/**
 * Unit tests for the PurnimaAstrology class.
 */
public class AppTest {
    
    /**
     * Test the PurnimaAstrology constructor and basic functionality.
     */
    @Test
    public void testPurnimaAstrologyCreation() {
        PurnimaAstrology astrology = new PurnimaAstrology();
        assertNotNull("PurnimaAstrology should not be null", astrology);
    }
    
    /**
     * Test the createBirthData method.
     */
    @Test
    public void testCreateBirthData() {
        LocalDateTime birthTime = LocalDateTime.of(1990, 5, 15, 14, 30);
        BirthData birthData = PurnimaAstrology.createBirthData(birthTime, 19.0760, 72.8777, "Mumbai");
        
        assertNotNull("BirthData should not be null", birthData);
        assertEquals("Birth date should match", birthTime, birthData.getBirthDateTime());
        assertEquals("Latitude should match", 19.0760, birthData.getLatitude(), 0.001);
        assertEquals("Longitude should match", 72.8777, birthData.getLongitude(), 0.001);
        assertEquals("Place name should match", "Mumbai", birthData.getPlaceName());
    }
    
    /**
     * Test the getVersion method.
     */
    @Test
    public void testGetVersion() {
        String version = PurnimaAstrology.getVersion();
        assertNotNull("Version should not be null", version);
        assertTrue("Version should contain 'Purnima'", version.contains("Purnima"));
        assertTrue("Version should contain 'Swiss Ephemeris'", version.contains("Swiss Ephemeris"));
    }
} 