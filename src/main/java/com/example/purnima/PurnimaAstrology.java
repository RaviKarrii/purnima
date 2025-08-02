package com.example.purnima;

import com.example.purnima.api.AsthakootCalculator;
import com.example.purnima.api.PanchangCalculator;
import com.example.purnima.api.ChartGenerator;
import com.example.purnima.model.*;
import com.example.purnima.service.DefaultAsthakootCalculator;
import com.example.purnima.service.DefaultPanchangCalculator;
import com.example.purnima.service.AccurateChartGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Main entry point for Purnima Vedic Astrology library.
 * Provides unified access to asthakoot, panchang, and chart generation functionality.
 * Now uses Swiss Ephemeris for accurate astronomical calculations.
 */
public class PurnimaAstrology {
    
    private final AsthakootCalculator asthakootCalculator;
    private final PanchangCalculator panchangCalculator;
    private final ChartGenerator chartGenerator;
    
    /**
     * Default constructor using accurate implementations with Swiss Ephemeris.
     */
    public PurnimaAstrology() {
        this.asthakootCalculator = new DefaultAsthakootCalculator();
        this.panchangCalculator = new DefaultPanchangCalculator();
        this.chartGenerator = new AccurateChartGenerator(); // Now uses Swiss Ephemeris
    }
    
    /**
     * Constructor with custom implementations.
     */
    public PurnimaAstrology(AsthakootCalculator asthakootCalculator, 
                           PanchangCalculator panchangCalculator, 
                           ChartGenerator chartGenerator) {
        this.asthakootCalculator = asthakootCalculator;
        this.panchangCalculator = panchangCalculator;
        this.chartGenerator = chartGenerator;
    }
    
    /**
     * Constructor that allows choosing between accurate and simplified calculations.
     * 
     * @param useAccurateCalculations If true, uses Swiss Ephemeris; if false, uses simplified calculations
     */
    public PurnimaAstrology(boolean useAccurateCalculations) {
        this.asthakootCalculator = new DefaultAsthakootCalculator();
        this.panchangCalculator = new DefaultPanchangCalculator();
        
        if (useAccurateCalculations) {
            this.chartGenerator = new AccurateChartGenerator(); // Swiss Ephemeris
        } else {
            this.chartGenerator = new com.example.purnima.service.DefaultChartGenerator(); // Simplified
        }
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
        return panchangCalculator.calculatePanchang(date, latitude, longitude);
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
        return panchangCalculator.calculatePanchang(dateTime, latitude, longitude);
    }
    
    /**
     * Get Panchang summary for a specific date and location.
     * 
     * @param date Date for Panchang calculation
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param placeName Name of the place
     * @return Formatted Panchang summary
     */
    public String getPanchangSummary(LocalDate date, double latitude, double longitude, String placeName) {
        PanchangResult result = calculatePanchang(date, latitude, longitude, placeName);
        return result.getSummary();
    }
    
    /**
     * Get detailed Panchang information for a specific date and location.
     * 
     * @param date Date for Panchang calculation
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param placeName Name of the place
     * @return Detailed Panchang information
     */
    public String getPanchangDetails(LocalDate date, double latitude, double longitude, String placeName) {
        PanchangResult result = calculatePanchang(date, latitude, longitude, placeName);
        return result.getDetailedInfo();
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
     * Get birth chart summary.
     * 
     * @param birthData Birth data
     * @return Formatted birth chart summary
     */
    public String getBirthChartSummary(BirthData birthData) {
        ChartResult result = generateBirthChart(birthData);
        return result.getSummary();
    }
    
    /**
     * Get detailed birth chart information.
     * 
     * @param birthData Birth data
     * @return Detailed birth chart information
     */
    public String getBirthChartDetails(BirthData birthData) {
        ChartResult result = generateBirthChart(birthData);
        return result.getDetailedInfo();
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
    
    // ==================== MAIN METHOD FOR DEMO ====================
    
    /**
     * Main method demonstrating basic usage of the library.
     */
    public static void main(String[] args) {
        System.out.println("=== " + getVersion() + " ===");
        System.out.println(getDescription());
        System.out.println();
        
        if (args.length == 0) {
            System.out.println("Usage Examples:");
            System.out.println("1. Calculate Panchang: java -jar purnima.jar panchang 2024-01-15 19.0760 72.8777 Mumbai");
            System.out.println("2. Generate Birth Chart: java -jar purnima.jar chart 1990-05-15T14:30:00 19.0760 72.8777 Mumbai");
            System.out.println("3. Generate Chart in JSON: java -jar purnima.jar json 1990-05-15T14:30:00 19.0760 72.8777 Mumbai");
            System.out.println("4. Calculate Compatibility: java -jar purnima.jar compatibility");
            System.out.println();
            System.out.println("For detailed usage, please refer to the documentation.");
            return;
        }
        
        PurnimaAstrology astrology = new PurnimaAstrology(true); // Use accurate calculations
        
        try {
            String command = args[0].toLowerCase();
            
            switch (command) {
                case "panchang":
                    if (args.length >= 5) {
                        LocalDate date = LocalDate.parse(args[1]);
                        double lat = Double.parseDouble(args[2]);
                        double lon = Double.parseDouble(args[3]);
                        String place = args[4];
                        
                        System.out.println("Calculating Panchang for " + date + " at " + place + "...");
                        String panchang = astrology.getPanchangSummary(date, lat, lon, place);
                        System.out.println(panchang);
                    } else {
                        System.out.println("Usage: panchang <date> <latitude> <longitude> <place>");
                    }
                    break;
                    
                case "chart":
                    if (args.length >= 5) {
                        LocalDateTime birthTime = LocalDateTime.parse(args[1]);
                        double lat = Double.parseDouble(args[2]);
                        double lon = Double.parseDouble(args[3]);
                        String place = args[4];
                        
                        BirthData birthData = createBirthData(birthTime, lat, lon, place);
                        System.out.println("Generating birth chart for " + place + " (using Swiss Ephemeris)...");
                        String chart = astrology.getBirthChartSummary(birthData);
                        System.out.println(chart);
                    } else {
                        System.out.println("Usage: chart <birthDateTime> <latitude> <longitude> <place>");
                    }
                    break;
                    
                case "json":
                    if (args.length >= 5) {
                        LocalDateTime birthTime = LocalDateTime.parse(args[1]);
                        double lat = Double.parseDouble(args[2]);
                        double lon = Double.parseDouble(args[3]);
                        String place = args[4];
                        
                        BirthData birthData = createBirthData(birthTime, lat, lon, place);
                        System.out.println("Generating JSON chart for " + place + "...");
                        String jsonChart = astrology.generateChartInFormat(birthData, ChartGenerator.ChartFormat.JSON);
                        System.out.println(jsonChart);
                    } else {
                        System.out.println("Usage: json <birthDateTime> <latitude> <longitude> <place>");
                    }
                    break;
                    
                case "html":
                    if (args.length >= 5) {
                        LocalDateTime birthTime = LocalDateTime.parse(args[1]);
                        double lat = Double.parseDouble(args[2]);
                        double lon = Double.parseDouble(args[3]);
                        String place = args[4];
                        
                        BirthData birthData = createBirthData(birthTime, lat, lon, place);
                        System.out.println("Generating HTML chart for " + place + "...");
                        String htmlChart = astrology.generateChartInFormat(birthData, ChartGenerator.ChartFormat.HTML);
                        System.out.println(htmlChart);
                    } else {
                        System.out.println("Usage: html <birthDateTime> <latitude> <longitude> <place>");
                    }
                    break;
                    
                case "compatibility":
                    System.out.println("Asthakoot compatibility calculation requires birth data for both individuals.");
                    System.out.println("This feature will be available in the full implementation.");
                    break;
                    
                default:
                    System.out.println("Unknown command: " + command);
                    System.out.println("Available commands: panchang, chart, json, html, compatibility");
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please check your input parameters and try again.");
            e.printStackTrace();
        }
    }
} 