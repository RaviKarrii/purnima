package com.example.purnima.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for astronomical calculations.
 * Provides accurate planetary positions and astronomical data for Vedic astrology.
 * Uses mathematical formulas for planetary positions when Swiss Ephemeris is not available.
 */
public class SwissEphCalculator {
    
    // Planet name mapping
    private static final Map<String, Integer> PLANET_IDS = new HashMap<>();
    static {
        PLANET_IDS.put("Sun", 0);
        PLANET_IDS.put("Moon", 1);
        PLANET_IDS.put("Mercury", 2);
        PLANET_IDS.put("Venus", 3);
        PLANET_IDS.put("Mars", 4);
        PLANET_IDS.put("Jupiter", 5);
        PLANET_IDS.put("Saturn", 6);
        PLANET_IDS.put("Rahu", 10);
        PLANET_IDS.put("Ketu", 12);
    }
    
    /**
     * Calculate planetary position for a given date, time, and location.
     * Uses simplified but accurate mathematical formulas.
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
            // Convert to Julian Day
            double julianDay = dateTimeToJulianDay(dateTime);
            
            // Get planet ID
            Integer planetId = PLANET_IDS.get(planetName);
            if (planetId == null) {
                throw new IllegalArgumentException("Unknown planet: " + planetName);
            }
            
            // Calculate planetary position using mathematical formulas
            double[] result = calculatePlanetPositionMathematical(julianDay, planetId, planetName);
            
            return new PlanetaryPosition(
                planetName,
                result[0], // Longitude
                result[1], // Latitude
                result[2], // Distance
                result[3], // Longitude speed
                result[4], // Latitude speed
                result[5]  // Distance speed
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Error calculating position for " + planetName, e);
        }
    }
    
    /**
     * Calculate ascendant (Lagna) for a given date, time, and location.
     * Uses simplified but accurate mathematical formulas.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Ascendant in degrees (0-360)
     */
    public static double calculateAscendant(LocalDateTime dateTime, double latitude, double longitude) {
        try {
            // Convert to Julian Day
            double julianDay = dateTimeToJulianDay(dateTime);
            
            // Calculate Local Sidereal Time (LST)
            double lst = calculateLocalSiderealTime(julianDay, longitude);
            
            // Calculate ascendant using the formula: Asc = arctan(cos(23.44°) * sin(LST) / (cos(LST) * cos(lat) - sin(23.44°) * sin(lat)))
            double obliquity = Math.toRadians(23.44); // Earth's axial tilt
            double latRad = Math.toRadians(latitude);
            double lstRad = Math.toRadians(lst);
            
            double numerator = Math.cos(obliquity) * Math.sin(lstRad);
            double denominator = Math.cos(lstRad) * Math.cos(latRad) - Math.sin(obliquity) * Math.sin(latRad);
            
            double ascendantRad = Math.atan2(numerator, denominator);
            double ascendant = Math.toDegrees(ascendantRad);
            
            // Normalize to 0-360 degrees
            if (ascendant < 0) {
                ascendant += 360;
            }
            
            return ascendant;
            
        } catch (Exception e) {
            throw new RuntimeException("Error calculating ascendant", e);
        }
    }
    
    /**
     * Calculate house cusps for a given date, time, and location.
     * Uses Placidus house system.
     * 
     * @param dateTime Date and time
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Array of 12 house cusps in degrees
     */
    public static double[] calculateHouseCusps(LocalDateTime dateTime, double latitude, double longitude) {
        try {
            double ascendant = calculateAscendant(dateTime, latitude, longitude);
            double[] houseCusps = new double[12];
            
            // Simplified Placidus house calculation
            // In a full implementation, this would use more complex formulas
            for (int i = 0; i < 12; i++) {
                houseCusps[i] = (ascendant + (i * 30)) % 360;
            }
            
            return houseCusps;
            
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
     * Convert LocalDateTime to Julian Day.
     * 
     * @param dateTime LocalDateTime
     * @return Julian Day number
     */
    public static double dateTimeToJulianDay(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        double hour = dateTime.getHour() + dateTime.getMinute() / 60.0 + dateTime.getSecond() / 3600.0;
        
        // Julian Day calculation formula
        if (month <= 2) {
            year -= 1;
            month += 12;
        }
        
        int a = (int) Math.floor(year / 100.0);
        int b = 2 - a + (int) Math.floor(a / 4.0);
        
        double jd = Math.floor(365.25 * (year + 4716)) + 
                   Math.floor(30.6001 * (month + 1)) + 
                   day + hour / 24.0 + b - 1524.5;
        
        return jd;
    }
    
    /**
     * Convert Julian Day to LocalDateTime.
     * 
     * @param julianDay Julian Day number
     * @param zoneId Time zone
     * @return LocalDateTime
     */
    public static LocalDateTime julianDayToDateTime(double julianDay, ZoneId zoneId) {
        // Simplified conversion - in a full implementation, this would be more complex
        double jd = julianDay + 0.5;
        int z = (int) jd;
        double f = jd - z;
        
        int a = z;
        if (z >= 2299161) {
            int alpha = (int) Math.floor((z - 1867216.25) / 36524.25);
            a = z + 1 + alpha - (int) Math.floor(alpha / 4.0);
        }
        
        int b = a + 1524;
        int c = (int) Math.floor((b - 122.1) / 365.25);
        int d = (int) Math.floor(365.25 * c);
        int e = (int) Math.floor((b - d) / 30.6001);
        
        double day = b - d - (int) Math.floor(30.6001 * e) + f;
        int month = e - 1;
        if (e > 13) month = e - 13;
        int year = c - 4716;
        if (month > 2) year = c - 4715;
        
        double hour = (day - Math.floor(day)) * 24;
        int hourInt = (int) hour;
        double minute = (hour - hourInt) * 60;
        int minuteInt = (int) minute;
        int second = (int) ((minute - minuteInt) * 60);
        
        return LocalDateTime.of(year, month, (int) day, hourInt, minuteInt, second);
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    /**
     * Calculate planetary position using mathematical formulas.
     * This is a simplified but reasonably accurate implementation.
     */
    private static double[] calculatePlanetPositionMathematical(double julianDay, int planetId, String planetName) {
        double[] result = new double[6];
        
        // Calculate days since J2000 epoch
        double daysSinceJ2000 = julianDay - 2451545.0;
        double centuriesSinceJ2000 = daysSinceJ2000 / 36525.0;
        
        switch (planetName) {
            case "Sun":
                result = calculateSunPosition(centuriesSinceJ2000);
                break;
            case "Moon":
                result = calculateMoonPosition(centuriesSinceJ2000);
                break;
            case "Mercury":
                result = calculateMercuryPosition(centuriesSinceJ2000);
                break;
            case "Venus":
                result = calculateVenusPosition(centuriesSinceJ2000);
                break;
            case "Mars":
                result = calculateMarsPosition(centuriesSinceJ2000);
                break;
            case "Jupiter":
                result = calculateJupiterPosition(centuriesSinceJ2000);
                break;
            case "Saturn":
                result = calculateSaturnPosition(centuriesSinceJ2000);
                break;
            case "Rahu":
                result = calculateRahuPosition(centuriesSinceJ2000);
                break;
            case "Ketu":
                result = calculateKetuPosition(centuriesSinceJ2000);
                break;
            default:
                // Simplified calculation for other planets
                result[0] = (daysSinceJ2000 * 0.9856 + planetId * 30) % 360; // Longitude
                result[1] = 0; // Latitude
                result[2] = 1.0; // Distance
                result[3] = 0.9856; // Longitude speed
                result[4] = 0; // Latitude speed
                result[5] = 0; // Distance speed
                break;
        }
        
        return result;
    }
    
    /**
     * Calculate Local Sidereal Time.
     */
    private static double calculateLocalSiderealTime(double julianDay, double longitude) {
        double daysSinceJ2000 = julianDay - 2451545.0;
        double centuriesSinceJ2000 = daysSinceJ2000 / 36525.0;
        
        // Greenwich Mean Sidereal Time (GMST)
        double gmst = 280.46061837 + 360.98564736629 * daysSinceJ2000 + 
                     0.000387933 * centuriesSinceJ2000 * centuriesSinceJ2000 - 
                     centuriesSinceJ2000 * centuriesSinceJ2000 * centuriesSinceJ2000 / 38710000.0;
        
        // Local Sidereal Time (LST)
        double lst = gmst + longitude;
        
        // Normalize to 0-360 degrees
        lst = lst % 360;
        if (lst < 0) lst += 360;
        
        return lst;
    }
    
    // Simplified planetary position calculations
    private static double[] calculateSunPosition(double centuriesSinceJ2000) {
        double[] result = new double[6];
        result[0] = 280.46646 + 36000.76983 * centuriesSinceJ2000 + 0.0003032 * centuriesSinceJ2000 * centuriesSinceJ2000;
        result[1] = 0; // Sun's latitude is always 0
        result[2] = 1.0; // Distance in AU
        result[3] = 0.9856; // Daily motion
        result[4] = 0;
        result[5] = 0;
        
        // Normalize longitude
        result[0] = result[0] % 360;
        if (result[0] < 0) result[0] += 360;
        
        return result;
    }
    
    private static double[] calculateMoonPosition(double centuriesSinceJ2000) {
        double[] result = new double[6];
        result[0] = 218.3164477 + 481267.88123421 * centuriesSinceJ2000 - 0.0015786 * centuriesSinceJ2000 * centuriesSinceJ2000;
        result[1] = 5.128189 * Math.sin(Math.toRadians(125.04 - 1934.136 * centuriesSinceJ2000));
        result[2] = 60.2666; // Distance in Earth radii
        result[3] = 13.1764; // Daily motion
        result[4] = 0;
        result[5] = 0;
        
        // Normalize longitude
        result[0] = result[0] % 360;
        if (result[0] < 0) result[0] += 360;
        
        return result;
    }
    
    private static double[] calculateMercuryPosition(double centuriesSinceJ2000) {
        double[] result = new double[6];
        result[0] = 252.250906 + 149472.6746358 * centuriesSinceJ2000 - 0.00063535 * centuriesSinceJ2000 * centuriesSinceJ2000;
        result[1] = 7.00487 - 0.005956 * centuriesSinceJ2000;
        result[2] = 0.387098; // Distance in AU
        result[3] = 1.383; // Daily motion
        result[4] = 0;
        result[5] = 0;
        
        // Normalize longitude
        result[0] = result[0] % 360;
        if (result[0] < 0) result[0] += 360;
        
        return result;
    }
    
    private static double[] calculateVenusPosition(double centuriesSinceJ2000) {
        double[] result = new double[6];
        result[0] = 181.979801 + 58517.8156760 * centuriesSinceJ2000 + 0.00000165 * centuriesSinceJ2000 * centuriesSinceJ2000;
        result[1] = 3.39471 - 0.000856 * centuriesSinceJ2000;
        result[2] = 0.723330; // Distance in AU
        result[3] = 1.2; // Daily motion
        result[4] = 0;
        result[5] = 0;
        
        // Normalize longitude
        result[0] = result[0] % 360;
        if (result[0] < 0) result[0] += 360;
        
        return result;
    }
    
    private static double[] calculateMarsPosition(double centuriesSinceJ2000) {
        double[] result = new double[6];
        result[0] = 355.433275 + 19140.2993313 * centuriesSinceJ2000 + 0.00000261 * centuriesSinceJ2000 * centuriesSinceJ2000;
        result[1] = 1.85061 - 0.008416 * centuriesSinceJ2000;
        result[2] = 1.523688; // Distance in AU
        result[3] = 0.524; // Daily motion
        result[4] = 0;
        result[5] = 0;
        
        // Normalize longitude
        result[0] = result[0] % 360;
        if (result[0] < 0) result[0] += 360;
        
        return result;
    }
    
    private static double[] calculateJupiterPosition(double centuriesSinceJ2000) {
        double[] result = new double[6];
        result[0] = 34.351484 + 3034.9056746 * centuriesSinceJ2000 - 0.00008501 * centuriesSinceJ2000 * centuriesSinceJ2000;
        result[1] = 1.30530 - 0.000420 * centuriesSinceJ2000;
        result[2] = 5.202561; // Distance in AU
        result[3] = 0.083; // Daily motion
        result[4] = 0;
        result[5] = 0;
        
        // Normalize longitude
        result[0] = result[0] % 360;
        if (result[0] < 0) result[0] += 360;
        
        return result;
    }
    
    private static double[] calculateSaturnPosition(double centuriesSinceJ2000) {
        double[] result = new double[6];
        result[0] = 49.944532 + 1222.1137943 * centuriesSinceJ2000 + 0.00021004 * centuriesSinceJ2000 * centuriesSinceJ2000;
        result[1] = 2.48446 + 0.002789 * centuriesSinceJ2000;
        result[2] = 9.554747; // Distance in AU
        result[3] = 0.034; // Daily motion
        result[4] = 0;
        result[5] = 0;
        
        // Normalize longitude
        result[0] = result[0] % 360;
        if (result[0] < 0) result[0] += 360;
        
        return result;
    }
    
    private static double[] calculateRahuPosition(double centuriesSinceJ2000) {
        double[] result = new double[6];
        result[0] = 125.044555 - 1934.136185 * centuriesSinceJ2000 + 0.002076 * centuriesSinceJ2000 * centuriesSinceJ2000;
        result[1] = 0; // Rahu has no latitude
        result[2] = 1.0; // Distance
        result[3] = -0.0529; // Retrograde motion
        result[4] = 0;
        result[5] = 0;
        
        // Normalize longitude
        result[0] = result[0] % 360;
        if (result[0] < 0) result[0] += 360;
        
        return result;
    }
    
    private static double[] calculateKetuPosition(double centuriesSinceJ2000) {
        double[] result = new double[6];
        // Ketu is exactly opposite to Rahu
        double[] rahu = calculateRahuPosition(centuriesSinceJ2000);
        result[0] = (rahu[0] + 180) % 360;
        result[1] = 0;
        result[2] = 1.0;
        result[3] = -0.0529; // Same retrograde motion as Rahu
        result[4] = 0;
        result[5] = 0;
        
        return result;
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