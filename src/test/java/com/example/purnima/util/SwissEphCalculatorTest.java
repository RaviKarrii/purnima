package com.example.purnima.util;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;
import com.example.purnima.util.SwissEphCalculator.PlanetaryPosition;
import com.example.purnima.util.SwissEphCalculator.LunarPhase;
import com.example.purnima.util.SwissEphCalculator.NakshatraInfo;

public class SwissEphCalculatorTest {

    @Test
    public void testCalculatePlanetPosition() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        double latitude = 19.0760;
        double longitude = 72.8777;

        PlanetaryPosition sunPos = SwissEphCalculator.calculatePlanetPosition(dateTime, latitude, longitude, "Sun");
        assertNotNull(sunPos);
        assertEquals("Sun", sunPos.getPlanetName());
        assertTrue(sunPos.getLongitude() >= 0 && sunPos.getLongitude() < 360);

        PlanetaryPosition moonPos = SwissEphCalculator.calculatePlanetPosition(dateTime, latitude, longitude, "Moon");
        assertNotNull(moonPos);
        assertEquals("Moon", moonPos.getPlanetName());
        assertTrue(moonPos.getLongitude() >= 0 && moonPos.getLongitude() < 360);
    }

    @Test
    public void testCalculateAscendant() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        double latitude = 19.0760;
        double longitude = 72.8777;

        double ascendant = SwissEphCalculator.calculateAscendant(dateTime, latitude, longitude);
        assertTrue("Ascendant should be between 0 and 360", ascendant >= 0 && ascendant < 360);
    }

    @Test
    public void testCalculateHouseCusps() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        double latitude = 19.0760;
        double longitude = 72.8777;

        double[] cusps = SwissEphCalculator.calculateHouseCusps(dateTime, latitude, longitude);
        assertNotNull(cusps);
        assertEquals(12, cusps.length);
        for (double cusp : cusps) {
            assertTrue("House cusp should be between 0 and 360", cusp >= 0 && cusp < 360);
        }
    }

    @Test
    public void testCalculateLunarPhase() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        double latitude = 19.0760;
        double longitude = 72.8777;

        LunarPhase phase = SwissEphCalculator.calculateLunarPhase(dateTime, latitude, longitude);
        assertNotNull(phase);
        assertTrue("Tithi should be between 1 and 30", phase.getTithi() >= 1 && phase.getTithi() <= 30);
    }

    @Test
    public void testCalculateNakshatra() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        double latitude = 19.0760;
        double longitude = 72.8777;

        NakshatraInfo nakshatra = SwissEphCalculator.calculateNakshatra(dateTime, latitude, longitude);
        assertNotNull(nakshatra);
        assertTrue("Nakshatra number should be between 1 and 27", nakshatra.getNakshatraNumber() >= 1 && nakshatra.getNakshatraNumber() <= 27);
    }
}
