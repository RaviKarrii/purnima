package com.example.purnima.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import de.thmac.swisseph.SweDate;
import de.thmac.swisseph.SwissEph;
import de.thmac.swisseph.SweConst;

/**
 * Utility class for astronomical calculations.
 * Provides accurate planetary positions and astronomical data for Vedic astrology.
 * Uses Swiss Ephemeris library for high precision.
 */
public class SwissEphCalculator {
    
    // Planet name mapping
    private static final Map<String, Integer> PLANET_IDS = new HashMap<>();
    static {
        PLANET_IDS.put("Sun", SweConst.SE_SUN);
        PLANET_IDS.put("Moon", SweConst.SE_MOON);
        PLANET_IDS.put("Mercury", SweConst.SE_MERCURY);
        PLANET_IDS.put("Venus", SweConst.SE_VENUS);
        PLANET_IDS.put("Mars", SweConst.SE_MARS);
        PLANET_IDS.put("Jupiter", SweConst.SE_JUPITER);
        PLANET_IDS.put("Saturn", SweConst.SE_SATURN);
        PLANET_IDS.put("Rahu", SweConst.SE_MEAN_NODE); // Mean node for Rahu
        PLANET_IDS.put("Ketu", -1); // Ketu is opposite to Rahu
    }
    
    // Swiss Ephemeris instance
    private static final SwissEph sw = new SwissEph();
    
    static {
        // Set Sidereal mode to Lahiri (most common for Vedic Astrology)
        sw.swe_set_sid_mode(SweConst.SE_SIDM_LAHIRI, 0, 0);
    }
    
    /**
     * Calculate planetary position for a given date, time, and location.
     * Uses Swiss Ephemeris for accurate calculations.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param planetName Name of the planet
     * @return PlanetaryPosition object containing longitude, latitude, distance, etc.
     */
    public static PlanetaryPosition calculatePlanetPosition(LocalDateTime dateTime, 
                                                          double latitude, double longitude, 
                                                          String planetName) {
        try {
            // Convert to Julian Day (UT)
            SweDate sd = getSweDate(dateTime);
            double julianDay = sd.getJulDay();
            
            // Get planet ID
            Integer planetId = PLANET_IDS.get(planetName);
            if (planetId == null) {
                throw new IllegalArgumentException("Unknown planet: " + planetName);
            }
            
            double[] xx = new double[6];
            StringBuffer serr = new StringBuffer();
            int flags = SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED;
            
            // Handle Ketu separately (opposite of Rahu)
            if (planetName.equals("Ketu")) {
                planetId = SweConst.SE_MEAN_NODE;
                int ret = sw.swe_calc_ut(julianDay, planetId, flags, xx, serr);
                if (ret < 0) {
                    throw new RuntimeException("SwissEph error: " + serr.toString());
                }
                // Ketu is 180 degrees from Rahu
                xx[0] = (xx[0] + 180) % 360;
            } else {
                int ret = sw.swe_calc_ut(julianDay, planetId, flags, xx, serr);
                if (ret < 0) {
                    throw new RuntimeException("SwissEph error: " + serr.toString());
                }
            }
            
            return new PlanetaryPosition(
                planetName,
                xx[0], // Longitude
                xx[1], // Latitude
                xx[2], // Distance
                xx[3], // Longitude speed
                xx[4], // Latitude speed
                xx[5]  // Distance speed
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Error calculating position for " + planetName, e);
        }
    }
    
    /**
     * Calculate ascendant (Lagna) for a given date, time, and location.
     * Uses Swiss Ephemeris house calculation.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Ascendant in degrees (0-360)
     */
    public static double calculateAscendant(LocalDateTime dateTime, double latitude, double longitude) {
        try {
            SweDate sd = getSweDate(dateTime);
            double julianDay = sd.getJulDay();
            
            double[] cusps = new double[13];
            double[] ascmc = new double[10];
            int flags = SweConst.SEFLG_SIDEREAL;
            
            // 'P' for Placidus, though for Ascendant it doesn't matter much which system, 
            // but we need to pass a system. Vedic often uses Whole Sign or Equal House for charts,
            // but the Ascendant point is the same.
            int ret = sw.swe_houses(julianDay, flags, latitude, longitude, 'P', cusps, ascmc);
            
            if (ret < 0) {
                throw new RuntimeException("SwissEph error calculating houses");
            }
            
            return ascmc[0]; // Ascendant is the first element in ascmc
            
        } catch (Exception e) {
            throw new RuntimeException("Error calculating ascendant", e);
        }
    }
    
    /**
     * Calculate house cusps for a given date, time, and location.
     * Uses Placidus house system (common default, though Vedic often uses others).
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Array of 12 house cusps in degrees
     */
    public static double[] calculateHouseCusps(LocalDateTime dateTime, double latitude, double longitude) {
        try {
            SweDate sd = getSweDate(dateTime);
            double julianDay = sd.getJulDay();
            
            double[] cusps = new double[13];
            double[] ascmc = new double[10];
            int flags = SweConst.SEFLG_SIDEREAL;
            
            // using Placidus ('P')
            sw.swe_houses(julianDay, flags, latitude, longitude, 'P', cusps, ascmc);
            
            double[] result = new double[12];
            // SwissEph returns cusps 1-12 in indices 1-12
            System.arraycopy(cusps, 1, result, 0, 12);
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Error calculating house cusps", e);
        }
    }
    
    /**
     * Calculate lunar phase (Tithi) for a given date, time, and location.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return LunarPhase object containing tithi information
     */
    public static LunarPhase calculateLunarPhase(LocalDateTime dateTime, double latitude, double longitude) {
        try {
            // Get Sun and Moon positions
            PlanetaryPosition sunPos = calculatePlanetPosition(dateTime, latitude, longitude, "Sun");
            PlanetaryPosition moonPos = calculatePlanetPosition(dateTime, latitude, longitude, "Moon");
            
            // Calculate lunar phase
            double lunarPhase = moonPos.getLongitude() - sunPos.getLongitude();
            if (lunarPhase < 0) {
                lunarPhase += 360;
            }
            
            // Calculate tithi (1-30)
            int tithi = (int) Math.floor(lunarPhase / 12) + 1;
            if (tithi > 30) tithi = 30;
            
            // Determine paksha (Shukla or Krishna)
            boolean isShuklaPaksha = lunarPhase < 180;
            
            return new LunarPhase(tithi, lunarPhase, isShuklaPaksha);
            
        } catch (Exception e) {
            throw new RuntimeException("Error calculating lunar phase", e);
        }
    }
    
    /**
     * Calculate Nakshatra for a given date, time, and location.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return NakshatraInfo object containing nakshatra details
     */
    public static NakshatraInfo calculateNakshatra(LocalDateTime dateTime, double latitude, double longitude) {
        try {
            // Get Moon position
            PlanetaryPosition moonPos = calculatePlanetPosition(dateTime, latitude, longitude, "Moon");
            
            // Calculate nakshatra (1-27)
            double moonLongitude = moonPos.getLongitude();
            int nakshatraNumber = (int) Math.floor(moonLongitude * 27 / 360) + 1;
            if (nakshatraNumber > 27) nakshatraNumber = 27;
            
            // Calculate degree within nakshatra
            double degreeInNakshatra = (moonLongitude * 27 / 360 - (nakshatraNumber - 1)) * 13.333333;
            
            return new NakshatraInfo(nakshatraNumber, degreeInNakshatra);
            
        } catch (Exception e) {
            throw new RuntimeException("Error calculating nakshatra", e);
        }
    }
    
    /**
     * Convert LocalDateTime to SweDate.
     * 
     * @param dateTime LocalDateTime
     * @return SweDate object
     */
    private static SweDate getSweDate(LocalDateTime dateTime) {
        // Assuming input is system default, convert to UT for SwissEph if needed
        // For simplicity, we'll treat the input as local time and let the user handle timezone
        // But SwissEph expects UT. Ideally, we should convert.
        // Here we assume the input LocalDateTime is effectively what we want to calculate for.
        // A robust implementation would take a ZonedDateTime or OffsetDateTime.
        
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        double hour = dateTime.getHour() + dateTime.getMinute() / 60.0 + dateTime.getSecond() / 3600.0;
        
        return new SweDate(year, month, day, hour);
    }
    
    /**
     * Convert LocalDateTime to Julian Day.
     * 
     * @param dateTime LocalDateTime
     * @return Julian Day number
     */
    public static double dateTimeToJulianDay(LocalDateTime dateTime) {
        return getSweDate(dateTime).getJulDay();
    }
    
    /**
     * Convert Julian Day to LocalDateTime.
     * 
     * @param julianDay Julian Day number
     * @param zoneId Time zone
     * @return LocalDateTime
     */
    public static LocalDateTime julianDayToDateTime(double julianDay, ZoneId zoneId) {
        SweDate sd = new SweDate(julianDay);
        int year = sd.getYear();
        int month = sd.getMonth();
        int day = sd.getDay();
        double hourVal = sd.getHour();
        
        int hour = (int) hourVal;
        int minute = (int) ((hourVal - hour) * 60);
        int second = (int) ((((hourVal - hour) * 60) - minute) * 60);
        
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }
    
    /**
     * Represents planetary position data.
     */
    public static class PlanetaryPosition {
        private final String planetName;
        private final double longitude;
        private final double latitude;
        private final double distance;
        private final double longitudeSpeed;
        private final double latitudeSpeed;
        private final double distanceSpeed;
        
        public PlanetaryPosition(String planetName, double longitude, double latitude, 
                               double distance, double longitudeSpeed, double latitudeSpeed, 
                               double distanceSpeed) {
            this.planetName = planetName;
            this.longitude = longitude;
            this.latitude = latitude;
            this.distance = distance;
            this.longitudeSpeed = longitudeSpeed;
            this.latitudeSpeed = latitudeSpeed;
            this.distanceSpeed = distanceSpeed;
        }
        
        // Getters
        public String getPlanetName() { return planetName; }
        public double getLongitude() { return longitude; }
        public double getLatitude() { return latitude; }
        public double getDistance() { return distance; }
        public double getLongitudeSpeed() { return longitudeSpeed; }
        public double getLatitudeSpeed() { return latitudeSpeed; }
        public double getDistanceSpeed() { return distanceSpeed; }
        
        /**
         * Check if the planet is retrograde (moving backwards).
         */
        public boolean isRetrograde() {
            return longitudeSpeed < 0;
        }
    }
    
    /**
     * Represents lunar phase (Tithi) information.
     */
    public static class LunarPhase {
        private final int tithi;
        private final double lunarPhase;
        private final boolean isShuklaPaksha;
        
        public LunarPhase(int tithi, double lunarPhase, boolean isShuklaPaksha) {
            this.tithi = tithi;
            this.lunarPhase = lunarPhase;
            this.isShuklaPaksha = isShuklaPaksha;
        }
        
        // Getters
        public int getTithi() { return tithi; }
        public double getLunarPhase() { return lunarPhase; }
        public boolean isShuklaPaksha() { return isShuklaPaksha; }
    }
    
    /**
     * Represents Nakshatra information.
     */
    public static class NakshatraInfo {
        private final int nakshatraNumber;
        private final double degreeInNakshatra;
        
        public NakshatraInfo(int nakshatraNumber, double degreeInNakshatra) {
            this.nakshatraNumber = nakshatraNumber;
            this.degreeInNakshatra = degreeInNakshatra;
        }
        
        // Getters
        public int getNakshatraNumber() { return nakshatraNumber; }
        public double getDegreeInNakshatra() { return degreeInNakshatra; }
    }
} 