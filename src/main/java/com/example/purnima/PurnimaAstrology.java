package com.example.purnima;

import com.example.purnima.api.AsthakootCalculator;
import com.example.purnima.api.PanchangCalculator;
import com.example.purnima.api.ChartGenerator;
import com.example.purnima.model.*;
import com.example.purnima.service.DefaultAsthakootCalculator;
import com.example.purnima.service.DefaultPanchangCalculator;
import com.example.purnima.service.AccurateChartGenerator;
import com.example.purnima.service.VimshottariDasaCalculator;
import com.example.purnima.service.DefaultMuhurtaCalculator;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

/**
 * Main entry point for Purnima Vedic Astrology library.
 * Provides unified access to asthakoot, panchang, and chart generation functionality.
 * Now uses Swiss Ephemeris for accurate astronomical calculations.
 */
public class PurnimaAstrology {
    
    private final AsthakootCalculator asthakootCalculator;
    private final PanchangCalculator panchangCalculator;
    private final ChartGenerator chartGenerator;
    private final com.example.purnima.api.DasaCalculator dasaCalculator;
    private final com.example.purnima.api.MuhurtaCalculator muhurtaCalculator;
    private final ReloadableResourceBundleMessageSource messageSource;
    
    /**
     * Default constructor using accurate implementations with Swiss Ephemeris.
     */
    public PurnimaAstrology() {
        this.messageSource = new ReloadableResourceBundleMessageSource();
        this.messageSource.setBasename("classpath:messages");
        this.messageSource.setDefaultEncoding("UTF-8");
        
        this.asthakootCalculator = new DefaultAsthakootCalculator();
        this.panchangCalculator = new DefaultPanchangCalculator(messageSource);
        this.chartGenerator = new AccurateChartGenerator(messageSource); // Now uses Swiss Ephemeris
        this.dasaCalculator = new VimshottariDasaCalculator(messageSource);
        this.muhurtaCalculator = new DefaultMuhurtaCalculator(messageSource);
    }
    
    /**
     * Constructor with custom implementations.
     */
    public PurnimaAstrology(AsthakootCalculator asthakootCalculator, 
                           PanchangCalculator panchangCalculator, 
                           ChartGenerator chartGenerator) {
        this.messageSource = new ReloadableResourceBundleMessageSource();
        this.messageSource.setBasename("classpath:messages");
        this.messageSource.setDefaultEncoding("UTF-8");

        this.asthakootCalculator = asthakootCalculator;
        this.panchangCalculator = panchangCalculator;
        this.chartGenerator = chartGenerator;
        // For custom constructors, we might default to standard Dasa/Muhurta or leave them null?
        // Better to initialize them with defaults if not provided, or add them to constructor.
        // For backward compatibility, we initialize defaults.
        this.dasaCalculator = new VimshottariDasaCalculator(messageSource);
        this.muhurtaCalculator = new DefaultMuhurtaCalculator(messageSource);
    }
    
    /**
     * Constructor that allows choosing between accurate and simplified calculations.
     * 
     * @param useAccurateCalculations If true, uses Swiss Ephemeris; if false, uses simplified calculations
     */
    public PurnimaAstrology(boolean useAccurateCalculations) {
        this.messageSource = new ReloadableResourceBundleMessageSource();
        this.messageSource.setBasename("classpath:messages");
        this.messageSource.setDefaultEncoding("UTF-8");

        this.asthakootCalculator = new DefaultAsthakootCalculator();
        this.panchangCalculator = new DefaultPanchangCalculator(messageSource);
        
        if (useAccurateCalculations) {
            this.chartGenerator = new AccurateChartGenerator(messageSource); // Swiss Ephemeris
        } else {
            this.chartGenerator = new com.example.purnima.service.DefaultChartGenerator(); // Simplified
        }
        
        this.dasaCalculator = new VimshottariDasaCalculator(messageSource);
        this.muhurtaCalculator = new DefaultMuhurtaCalculator(messageSource);
    }
    
    // ==================== ASTHAKOOT METHODS ====================
    
    /**
     * Calculate Asthakoot compatibility between two individuals.
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return AsthakootResult containing compatibility analysis
     */
    public AsthakootResult calculateAsthakoot(BirthData maleBirthData, BirthData femaleBirthData) {
        return asthakootCalculator.calculateAsthakoot(maleBirthData, femaleBirthData);
    }
    
    /**
     * Calculate Asthakoot compatibility with detailed breakdown.
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return Detailed compatibility analysis as string
     */
    public String getAsthakootAnalysis(BirthData maleBirthData, BirthData femaleBirthData) {
        AsthakootResult result = calculateAsthakoot(maleBirthData, femaleBirthData);
        return result.getDetailedBreakdown();
    }
    
    // ==================== PANCHANG METHODS ====================
    
    /**
     * Calculate Panchang for a specific date and location.
     * 
     * @param date Date for Panchang calculation
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param placeName Name of the place
     * @return PanchangResult containing all five elements
     */
    public PanchangResult calculatePanchang(LocalDate date, double latitude, double longitude, String placeName) {
        return panchangCalculator.calculatePanchang(date, latitude, longitude, placeName);
    }

    /**
     * Calculate Panchang for a specific date and location with TimeZone.
     * 
     * @param date Date for Panchang calculation
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param placeName Name of the place
     * @param zoneId TimeZone of the location
     * @return PanchangResult containing all five elements
     */
    public PanchangResult calculatePanchang(LocalDate date, double latitude, double longitude, String placeName, ZoneId zoneId) {
        return panchangCalculator.calculatePanchang(date, latitude, longitude, placeName, zoneId);
    }
    
    /**
     * Calculate Panchang for a specific date-time and location.
     * 
     * @param dateTime Date and time for Panchang calculation
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param placeName Name of the place
     * @return PanchangResult containing all five elements
     */
    public PanchangResult calculatePanchang(LocalDateTime dateTime, double latitude, double longitude, String placeName) {
        return panchangCalculator.calculatePanchang(dateTime, latitude, longitude, placeName);
    }

    /**
     * Calculate Panchang for a specific date-time and location with TimeZone.
     * 
     * @param dateTime Date and time for Panchang calculation
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param placeName Name of the place
     * @param zoneId TimeZone of the location
     * @return PanchangResult containing all five elements
     */
    public PanchangResult calculatePanchang(LocalDateTime dateTime, double latitude, double longitude, String placeName, ZoneId zoneId) {
        return panchangCalculator.calculatePanchang(dateTime, latitude, longitude, placeName, zoneId);
    }
    

    
    // ==================== CHART GENERATION METHODS ====================
    
    /**
     * Generate birth chart for given birth data using Swiss Ephemeris.
     * 
     * @param birthData Birth data containing date, time, and location
     * @return ChartResult containing the birth chart
     */
    public ChartResult generateBirthChart(BirthData birthData) {
        return chartGenerator.generateBirthChart(birthData);
    }
    
    /**
     * Generate compatibility chart between two individuals.
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return ChartResult containing the compatibility chart
     */
    public ChartResult generateCompatibilityChart(BirthData maleBirthData, BirthData femaleBirthData) {
        return chartGenerator.generateCompatibilityChart(maleBirthData, femaleBirthData);
    }
    

    
    /**
     * Generate chart in specific format (JSON, XML, HTML, CSV).
     * 
     * @param birthData Birth data
     * @param format Desired output format
     * @return String representation of the chart in specified format
     */
    public String generateChartInFormat(BirthData birthData, ChartGenerator.ChartFormat format) {
        ChartResult result = generateBirthChart(birthData);
        return chartGenerator.generateChartInFormat(result, format);
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Create BirthData object with default timezone.
     * 
     * @param birthDateTime Birth date and time
     * @param latitude Latitude
     * @param longitude Longitude
     * @param placeName Place name
     * @return BirthData object
     */
    public static BirthData createBirthData(LocalDateTime birthDateTime, double latitude, double longitude, String placeName) {
        return new BirthData(birthDateTime, latitude, longitude, placeName);
    }
    
    /**
     * Create BirthData object with specific timezone.
     * 
     * @param birthDateTime Birth date and time
     * @param latitude Latitude
     * @param longitude Longitude
     * @param placeName Place name
     * @param timeZone Timezone
     * @return BirthData object
     */
    public static BirthData createBirthData(LocalDateTime birthDateTime, double latitude, double longitude, String placeName, ZoneId timeZone) {
        return new BirthData(birthDateTime, latitude, longitude, placeName, timeZone);
    }
    
    /**
     * Get library version information.
     * 
     * @return Version information
     */
    public static String getVersion() {
        return "Purnima Vedic Astrology Library v1.0.0 (with Swiss Ephemeris)";
    }
    
    /**
     * Get library description and capabilities.
     * 
     * @return Library description
     */
    public static String getDescription() {
        return "A comprehensive Vedic Astrology library providing:\n" +
               "- Asthakoot (eight-fold compatibility) calculations\n" +
               "- Panchang (five elements) calculations\n" +
               "- Birth chart generation and analysis (using Swiss Ephemeris)\n" +
               "- Compatibility charts\n" +
               "- Transit charts and horoscopes\n" +
               "- Divisional charts (Vargas)\n" +
               "- Planetary periods (Dasha) calculations\n" +
               "- Accurate astronomical calculations via Swiss Ephemeris";
    }
    
    // ==================== DASA METHODS ====================
    
    /**
     * Calculate Vimshottari Dasa Mahadasas.
     * 
     * @param birthData Birth data
     * @return List of Mahadasas
     */
    public java.util.List<com.example.purnima.model.DasaResult> calculateVimshottariDasa(BirthData birthData) {
        return dasaCalculator.calculateMahadasas(birthData);
    }
    
    /**
     * Get current Dasa period.
     * 
     * @param birthData Birth data
     * @return Current Dasa period
     */
    public com.example.purnima.model.DasaResult getCurrentDasa(BirthData birthData) {
        return dasaCalculator.getCurrentDasa(birthData);
    }
    
    // ==================== MUHURTA METHODS ====================
    
    /**
     * Calculate Muhurta details (Choghadiya, Hora, etc.).
     * 
     * @param date Date
     * @param latitude Latitude
     * @param longitude Longitude
     * @param zoneId TimeZone
     * @return MuhurtaResult
     */
    public com.example.purnima.model.MuhurtaResult calculateMuhurta(LocalDate date, double latitude, double longitude, ZoneId zoneId) {
        return muhurtaCalculator.calculateMuhurta(date, latitude, longitude, zoneId);
    }
}