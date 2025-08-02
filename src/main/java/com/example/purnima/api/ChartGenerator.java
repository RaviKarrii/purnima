package com.example.purnima.api;

import com.example.purnima.model.BirthData;
import com.example.purnima.model.ChartResult;

import java.time.LocalDateTime;

/**
 * Interface for generating various types of astrological charts.
 * Supports birth charts, compatibility charts, transit charts, and more.
 */
public interface ChartGenerator {
    
    /**
     * Generate a birth chart (Janma Kundali) for given birth data.
     * 
     * @param birthData Birth data containing date, time, and location
     * @return ChartResult containing the birth chart
     */
    ChartResult generateBirthChart(BirthData birthData);
    
    /**
     * Generate a compatibility chart between two individuals.
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return ChartResult containing the compatibility chart
     */
    ChartResult generateCompatibilityChart(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Generate a transit chart for a specific date and time.
     * 
     * @param birthData Original birth data
     * @param transitDateTime Date and time for transit calculation
     * @return ChartResult containing the transit chart
     */
    ChartResult generateTransitChart(BirthData birthData, LocalDateTime transitDateTime);
    
    /**
     * Generate a horoscope chart (daily, weekly, monthly, yearly).
     * 
     * @param birthData Birth data
     * @param periodType Type of horoscope period
     * @param startDate Start date of the period
     * @return ChartResult containing the horoscope chart
     */
    ChartResult generateHoroscopeChart(BirthData birthData, PeriodType periodType, LocalDateTime startDate);
    
    /**
     * Generate a planetary position chart for current time.
     * 
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return ChartResult containing planetary positions
     */
    ChartResult generatePlanetaryPositionChart(double latitude, double longitude);
    
    /**
     * Generate a chart in specific format (text, JSON, XML, etc.).
     * 
     * @param chartResult Original chart result
     * @param format Desired output format
     * @return String representation of the chart in specified format
     */
    String generateChartInFormat(ChartResult chartResult, ChartFormat format);
    
    /**
     * Generate a detailed chart with all planetary aspects and relationships.
     * 
     * @param birthData Birth data
     * @return DetailedChartResult containing comprehensive chart information
     */
    DetailedChartResult generateDetailedChart(BirthData birthData);
    
    /**
     * Generate a divisional chart (Varga) for specific purposes.
     * 
     * @param birthData Birth data
     * @param vargaType Type of divisional chart
     * @return ChartResult containing the divisional chart
     */
    ChartResult generateDivisionalChart(BirthData birthData, VargaType vargaType);
    
    /**
     * Generate a chart showing planetary periods (Dasha).
     * 
     * @param birthData Birth data
     * @param dashaType Type of dasha system
     * @return DashaResult containing dasha information
     */
    DashaResult generateDashaChart(BirthData birthData, DashaType dashaType);
    
    /**
     * Represents different types of horoscope periods.
     */
    enum PeriodType {
        DAILY("Daily"),
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        YEARLY("Yearly");
        
        private final String displayName;
        
        PeriodType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Represents different chart output formats.
     */
    enum ChartFormat {
        TEXT("text"),
        JSON("json"),
        XML("xml"),
        HTML("html"),
        CSV("csv");
        
        private final String format;
        
        ChartFormat(String format) {
            this.format = format;
        }
        
        public String getFormat() {
            return format;
        }
    }
    
    /**
     * Represents different types of divisional charts (Vargas).
     */
    enum VargaType {
        RASHI(1, "Rashi"),
        HORA(2, "Hora"),
        DREKKANA(3, "Drekkana"),
        CHATURTHAMSA(4, "Chaturthamsa"),
        SAPTAMSA(7, "Saptamsa"),
        NAVAMSA(9, "Navamsa"),
        DASHAMSA(10, "Dashamsa"),
        DWADASHAMSA(12, "Dwadashamsa"),
        SHODASHAMSA(16, "Shodashamsa"),
        VIMSAMSA(20, "Vimsamsa"),
        CHATURVIMSAMSA(24, "Chaturvimsamsa"),
        SAPTAVIMSAMSA(27, "Saptavimsamsa"),
        TRIMSAMSA(30, "Trimsamsa"),
        KHAVEDAMSA(40, "Khavedamsa"),
        AKSHAVEDAMSA(45, "Akshavedamsa"),
        SHASTIAMSA(60, "Shastiamsa");
        
        private final int division;
        private final String name;
        
        VargaType(int division, String name) {
            this.division = division;
            this.name = name;
        }
        
        public int getDivision() {
            return division;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * Represents different types of dasha systems.
     */
    enum DashaType {
        VIMSHOTTARI("Vimshottari"),
        ASHTOTTARI("Ashtottari"),
        YOGINI("Yogini"),
        KALACHAKRA("Kalachakra");
        
        private final String name;
        
        DashaType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * Represents detailed chart information including aspects and relationships.
     */
    class DetailedChartResult extends ChartResult {
        private final AspectInfo[] aspects;
        private final RelationshipInfo[] relationships;
        private final StrengthInfo[] planetaryStrengths;
        
        public DetailedChartResult(ChartResult baseResult, AspectInfo[] aspects, 
                                 RelationshipInfo[] relationships, StrengthInfo[] planetaryStrengths) {
            super(baseResult.getBirthData(), baseResult.getPlanetaryPositions(), baseResult.getHouses());
            this.aspects = aspects;
            this.relationships = relationships;
            this.planetaryStrengths = planetaryStrengths;
        }
        
        public AspectInfo[] getAspects() { return aspects; }
        public RelationshipInfo[] getRelationships() { return relationships; }
        public StrengthInfo[] getPlanetaryStrengths() { return planetaryStrengths; }
    }
    
    /**
     * Represents planetary aspect information.
     */
    class AspectInfo {
        private final String planet1;
        private final String planet2;
        private final double aspectAngle;
        private final String aspectType;
        private final boolean isHarmful;
        
        public AspectInfo(String planet1, String planet2, double aspectAngle, 
                         String aspectType, boolean isHarmful) {
            this.planet1 = planet1;
            this.planet2 = planet2;
            this.aspectAngle = aspectAngle;
            this.aspectType = aspectType;
            this.isHarmful = isHarmful;
        }
        
        // Getters
        public String getPlanet1() { return planet1; }
        public String getPlanet2() { return planet2; }
        public double getAspectAngle() { return aspectAngle; }
        public String getAspectType() { return aspectType; }
        public boolean isHarmful() { return isHarmful; }
    }
    
    /**
     * Represents planetary relationship information.
     */
    class RelationshipInfo {
        private final String planet1;
        private final String planet2;
        private final String relationshipType;
        private final String description;
        
        public RelationshipInfo(String planet1, String planet2, String relationshipType, String description) {
            this.planet1 = planet1;
            this.planet2 = planet2;
            this.relationshipType = relationshipType;
            this.description = description;
        }
        
        // Getters
        public String getPlanet1() { return planet1; }
        public String getPlanet2() { return planet2; }
        public String getRelationshipType() { return relationshipType; }
        public String getDescription() { return description; }
    }
    
    /**
     * Represents planetary strength information.
     */
    class StrengthInfo {
        private final String planet;
        private final double shadbala;
        private final double ashtakavarga;
        private final String strengthLevel;
        
        public StrengthInfo(String planet, double shadbala, double ashtakavarga, String strengthLevel) {
            this.planet = planet;
            this.shadbala = shadbala;
            this.ashtakavarga = ashtakavarga;
            this.strengthLevel = strengthLevel;
        }
        
        // Getters
        public String getPlanet() { return planet; }
        public double getShadbala() { return shadbala; }
        public double getAshtakavarga() { return ashtakavarga; }
        public String getStrengthLevel() { return strengthLevel; }
    }
    
    /**
     * Represents dasha information.
     */
    class DashaResult {
        private final String currentDasha;
        private final String currentAntardasha;
        private final LocalDateTime dashaStart;
        private final LocalDateTime dashaEnd;
        private final DashaPeriod[] dashaPeriods;
        
        public DashaResult(String currentDasha, String currentAntardasha, 
                          LocalDateTime dashaStart, LocalDateTime dashaEnd, DashaPeriod[] dashaPeriods) {
            this.currentDasha = currentDasha;
            this.currentAntardasha = currentAntardasha;
            this.dashaStart = dashaStart;
            this.dashaEnd = dashaEnd;
            this.dashaPeriods = dashaPeriods;
        }
        
        // Getters
        public String getCurrentDasha() { return currentDasha; }
        public String getCurrentAntardasha() { return currentAntardasha; }
        public LocalDateTime getDashaStart() { return dashaStart; }
        public LocalDateTime getDashaEnd() { return dashaEnd; }
        public DashaPeriod[] getDashaPeriods() { return dashaPeriods; }
    }
    
    /**
     * Represents a dasha period.
     */
    class DashaPeriod {
        private final String planet;
        private final LocalDateTime startDate;
        private final LocalDateTime endDate;
        private final int duration;
        
        public DashaPeriod(String planet, LocalDateTime startDate, LocalDateTime endDate, int duration) {
            this.planet = planet;
            this.startDate = startDate;
            this.endDate = endDate;
            this.duration = duration;
        }
        
        // Getters
        public String getPlanet() { return planet; }
        public LocalDateTime getStartDate() { return startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public int getDuration() { return duration; }
    }
} 