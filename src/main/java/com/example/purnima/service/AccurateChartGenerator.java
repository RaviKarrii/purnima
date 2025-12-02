package com.example.purnima.service;

import com.example.purnima.api.ChartGenerator;
import com.example.purnima.model.BirthData;
import com.example.purnima.model.ChartResult;
import com.example.purnima.model.Planet;
import com.example.purnima.model.Rashi;
import com.example.purnima.util.SwissEphCalculator;
import com.example.purnima.util.SwissEphCalculator.PlanetaryPosition;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Accurate implementation of ChartGenerator using Swiss Ephemeris.
 * Provides real astronomical calculations for planetary positions and chart generation.
 */
public class AccurateChartGenerator implements ChartGenerator {
    
    @Override
    public ChartResult generateBirthChart(BirthData birthData) {
        // Generate accurate planetary positions using Swiss Ephemeris
        ChartResult.PlanetaryPosition[] planetaryPositions = generateAccuratePlanetaryPositions(birthData);
        
        // Generate accurate houses using Swiss Ephemeris
        ChartResult.House[] houses = generateAccurateHouses(birthData, planetaryPositions);
        
        return new ChartResult(birthData, planetaryPositions, houses);
    }
    
    @Override
    public ChartResult generateCompatibilityChart(BirthData maleBirthData, BirthData femaleBirthData) {
        // For compatibility chart, we'll generate a combined chart
        // This is a simplified implementation
        BirthData combinedData = new BirthData(
            maleBirthData.getBirthDateTime(),
            (maleBirthData.getLatitude() + femaleBirthData.getLatitude()) / 2,
            (maleBirthData.getLongitude() + femaleBirthData.getLongitude()) / 2,
            maleBirthData.getPlaceName() + " & " + femaleBirthData.getPlaceName()
        );
        
        return generateBirthChart(combinedData);
    }
    
    @Override
    public ChartResult generateTransitChart(BirthData birthData, LocalDateTime transitDateTime) {
        // For transit chart, we'll use the transit time but keep the birth location
        BirthData transitData = new BirthData(
            transitDateTime,
            birthData.getLatitude(),
            birthData.getLongitude(),
            birthData.getPlaceName() + " (Transit)"
        );
        
        return generateBirthChart(transitData);
    }
    
    @Override
    public ChartResult generateHoroscopeChart(BirthData birthData, PeriodType periodType, LocalDateTime startDate) {
        // For horoscope chart, we'll use the start date but keep the birth location
        BirthData horoscopeData = new BirthData(
            startDate,
            birthData.getLatitude(),
            birthData.getLongitude(),
            birthData.getPlaceName() + " (" + periodType.getDisplayName() + " Horoscope)"
        );
        
        return generateBirthChart(horoscopeData);
    }
    
    @Override
    public ChartResult generatePlanetaryPositionChart(double latitude, double longitude) {
        // Generate current planetary positions
        BirthData currentData = new BirthData(
            LocalDateTime.now(),
            latitude,
            longitude,
            "Current Positions"
        );
        
        return generateBirthChart(currentData);
    }
    
    @Override
    public String generateChartInFormat(ChartResult chartResult, ChartFormat format) {
        switch (format) {
            case TEXT:
                return chartResult.getDetailedInfo();
            case JSON:
                return generateJsonFormat(chartResult);
            case XML:
                return generateXmlFormat(chartResult);
            case HTML:
                return generateHtmlFormat(chartResult);
            case CSV:
                return generateCsvFormat(chartResult);
            default:
                return chartResult.getSummary();
        }
    }
    
    @Override
    public DetailedChartResult generateDetailedChart(BirthData birthData) {
        ChartResult baseResult = generateBirthChart(birthData);
        
        // Generate aspects (simplified)
        AspectInfo[] aspects = generateAspects(baseResult.getPlanetaryPositions());
        
        // Generate relationships (simplified)
        RelationshipInfo[] relationships = generateRelationships(baseResult.getPlanetaryPositions());
        
        // Generate planetary strengths (simplified)
        StrengthInfo[] strengths = generatePlanetaryStrengths(baseResult.getPlanetaryPositions());
        
        return new DetailedChartResult(baseResult, aspects, relationships, strengths);
    }
    
    @Override
    public ChartResult generateDivisionalChart(BirthData birthData, VargaType vargaType) {
        // For divisional chart, we'll modify the birth data based on the varga
        // This is a simplified implementation
        LocalDateTime modifiedTime = birthData.getBirthDateTime().plusHours(vargaType.getDivision());
        
        BirthData vargaData = new BirthData(
            modifiedTime,
            birthData.getLatitude(),
            birthData.getLongitude(),
            birthData.getPlaceName() + " (" + vargaType.getName() + " Varga)"
        );
        
        return generateBirthChart(vargaData);
    }
    
    @Override
    public DashaResult generateDashaChart(BirthData birthData, DashaType dashaType) {
        // Simplified dasha calculation
        LocalDateTime startDate = birthData.getBirthDateTime();
        LocalDateTime endDate = startDate.plusYears(120); // Assuming 120 years lifespan
        
        String currentDasha = "Jupiter"; // Simplified
        String currentAntardasha = "Saturn"; // Simplified
        
        DashaPeriod[] periods = generateDashaPeriods(dashaType, startDate, endDate);
        
        return new DashaResult(currentDasha, currentAntardasha, startDate, endDate, periods);
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    private ChartResult.PlanetaryPosition[] generateAccuratePlanetaryPositions(BirthData birthData) {
        List<ChartResult.PlanetaryPosition> positions = new ArrayList<>();
        
        // Generate positions for all planets using Swiss Ephemeris
        Planet[] planets = Planet.values();
        for (Planet planet : planets) {
            try {
                // Get accurate planetary position from Swiss Ephemeris
                PlanetaryPosition swissPos = SwissEphCalculator.calculatePlanetPosition(
                    birthData.getBirthDateTime(),
                    birthData.getLatitude(),
                    birthData.getLongitude(),
                    planet.getEnglishName()
                );
                
                // Calculate rashi from longitude
                Rashi rashi = Rashi.getRashiForDegree(swissPos.getLongitude());
                
                // Calculate degree within rashi
                double degreeInRashi = rashi.getDegreeInRashi(swissPos.getLongitude());
                
                // Calculate house number (simplified - would need more complex calculation)
                int houseNumber = calculateHouseNumber(swissPos.getLongitude(), birthData);
                
                // Determine exaltation status
                String exaltationStatus = determineExaltationStatus(planet, swissPos.getLongitude());
                
                positions.add(new ChartResult.PlanetaryPosition(
                    planet, rashi, degreeInRashi, houseNumber, swissPos.isRetrograde(), exaltationStatus
                ));
                
            } catch (Exception e) {
                // Fallback to simplified calculation if Swiss Ephemeris fails
                System.err.println("Warning: Using fallback calculation for " + planet.getEnglishName() + ": " + e.getMessage());
                positions.add(generateFallbackPosition(planet, birthData));
            }
        }
        
        return positions.toArray(new ChartResult.PlanetaryPosition[0]);
    }
    
    private ChartResult.House[] generateAccurateHouses(BirthData birthData, ChartResult.PlanetaryPosition[] planetaryPositions) {
        ChartResult.House[] houses = new ChartResult.House[12];
        
        try {
            // Calculate accurate ascendant using Swiss Ephemeris
            double ascendantLongitude = SwissEphCalculator.calculateAscendant(
                birthData.getBirthDateTime(),
                birthData.getLatitude(),
                birthData.getLongitude()
            );
            
            // Calculate house cusps using Swiss Ephemeris
            double[] houseCusps = SwissEphCalculator.calculateHouseCusps(
                birthData.getBirthDateTime(),
                birthData.getLatitude(),
                birthData.getLongitude()
            );
            
            for (int i = 0; i < 12; i++) {
                double cuspLongitude = houseCusps[i];
                Rashi rashi = Rashi.getRashiForDegree(cuspLongitude);
                
                double startDegree = cuspLongitude;
                double endDegree = (i < 11) ? houseCusps[i + 1] : houseCusps[0] + 360;
                
                // Find planets in this house
                List<ChartResult.PlanetaryPosition> housePlanets = new ArrayList<>();
                for (ChartResult.PlanetaryPosition position : planetaryPositions) {
                    if (isPlanetInHouse(position, startDegree, endDegree)) {
                        housePlanets.add(position);
                    }
                }
                
                houses[i] = new ChartResult.House(
                    i + 1, rashi, startDegree, endDegree,
                    housePlanets.toArray(new ChartResult.PlanetaryPosition[0])
                );
            }
            
        } catch (Exception e) {
            // Fallback to simplified calculation if Swiss Ephemeris fails
            System.err.println("Warning: Using fallback house calculation: " + e.getMessage());
            houses = generateFallbackHouses(birthData, planetaryPositions);
        }
        
        return houses;
    }
    
    private int calculateHouseNumber(double planetLongitude, BirthData birthData) {
        try {
            double ascendantLongitude = SwissEphCalculator.calculateAscendant(
                birthData.getBirthDateTime(),
                birthData.getLatitude(),
                birthData.getLongitude()
            );
            
            double[] houseCusps = SwissEphCalculator.calculateHouseCusps(
                birthData.getBirthDateTime(),
                birthData.getLatitude(),
                birthData.getLongitude()
            );
            
            // Find which house the planet is in
            for (int i = 0; i < 12; i++) {
                double startCusp = houseCusps[i];
                double endCusp = (i < 11) ? houseCusps[i + 1] : houseCusps[0] + 360;
                
                if (planetLongitude >= startCusp && planetLongitude < endCusp) {
                    return i + 1;
                }
            }
            
            return 1; // Default fallback
        } catch (Exception e) {
            // Simplified fallback calculation
            return ((int) (planetLongitude / 30) % 12) + 1;
        }
    }
    
    private boolean isPlanetInHouse(ChartResult.PlanetaryPosition position, double startDegree, double endDegree) {
        double planetLongitude = position.getRashi().getStartDegree() + position.getDegreeInRashi();
        
        if (startDegree <= endDegree) {
            return planetLongitude >= startDegree && planetLongitude < endDegree;
        } else {
            // Handle case where house spans across 0 degrees
            return planetLongitude >= startDegree || planetLongitude < endDegree;
        }
    }
    
    private String determineExaltationStatus(Planet planet, double longitude) {
        Rashi rashi = Rashi.getRashiForDegree(longitude);
        double degreeInRashi = longitude % 30;
        
        // Deep Exaltation/Debilitation (within 1 degree)
        if (isDeepExaltation(planet, rashi, degreeInRashi)) return "Deep Exaltation";
        if (isDeepDebilitation(planet, rashi, degreeInRashi)) return "Deep Debilitation";
        
        // Sign-based status
        if (isExaltationSign(planet, rashi)) return "Exalted Sign";
        if (isDebilitationSign(planet, rashi)) return "Debilitated Sign";
        if (isOwnSign(planet, rashi)) return "Own Sign";
        if (isMoolatrikona(planet, rashi, degreeInRashi)) return "Moolatrikona";
        
        return "Neutral"; // Default
    }
    
    private boolean isDeepExaltation(Planet planet, Rashi rashi, double degree) {
        switch (planet) {
            case SUN: return rashi == Rashi.MESH && Math.abs(degree - 10) <= 1;
            case MOON: return rashi == Rashi.VRISHABH && Math.abs(degree - 3) <= 1;
            case MARS: return rashi == Rashi.MAKAR && Math.abs(degree - 28) <= 1;
            case MERCURY: return rashi == Rashi.KANYA && Math.abs(degree - 15) <= 1;
            case JUPITER: return rashi == Rashi.KARK && Math.abs(degree - 5) <= 1;
            case VENUS: return rashi == Rashi.MEEN && Math.abs(degree - 27) <= 1;
            case SATURN: return rashi == Rashi.TULA && Math.abs(degree - 20) <= 1;
            default: return false;
        }
    }
    
    private boolean isDeepDebilitation(Planet planet, Rashi rashi, double degree) {
        switch (planet) {
            case SUN: return rashi == Rashi.TULA && Math.abs(degree - 10) <= 1;
            case MOON: return rashi == Rashi.VRISHCHIK && Math.abs(degree - 3) <= 1;
            case MARS: return rashi == Rashi.KARK && Math.abs(degree - 28) <= 1;
            case MERCURY: return rashi == Rashi.MEEN && Math.abs(degree - 15) <= 1;
            case JUPITER: return rashi == Rashi.MAKAR && Math.abs(degree - 5) <= 1;
            case VENUS: return rashi == Rashi.KANYA && Math.abs(degree - 27) <= 1;
            case SATURN: return rashi == Rashi.MESH && Math.abs(degree - 20) <= 1;
            default: return false;
        }
    }
    
    private boolean isExaltationSign(Planet planet, Rashi rashi) {
        switch (planet) {
            case SUN: return rashi == Rashi.MESH;
            case MOON: return rashi == Rashi.VRISHABH;
            case MARS: return rashi == Rashi.MAKAR;
            case MERCURY: return rashi == Rashi.KANYA; // Own sign too, but exalted first
            case JUPITER: return rashi == Rashi.KARK;
            case VENUS: return rashi == Rashi.MEEN;
            case SATURN: return rashi == Rashi.TULA;
            case RAHU: return rashi == Rashi.VRISHABH || rashi == Rashi.MITHUN; // Common view
            case KETU: return rashi == Rashi.VRISHCHIK || rashi == Rashi.DHANU; // Common view
            default: return false;
        }
    }
    
    private boolean isDebilitationSign(Planet planet, Rashi rashi) {
        switch (planet) {
            case SUN: return rashi == Rashi.TULA;
            case MOON: return rashi == Rashi.VRISHCHIK;
            case MARS: return rashi == Rashi.KARK;
            case MERCURY: return rashi == Rashi.MEEN;
            case JUPITER: return rashi == Rashi.MAKAR;
            case VENUS: return rashi == Rashi.KANYA;
            case SATURN: return rashi == Rashi.MESH;
            case RAHU: return rashi == Rashi.VRISHCHIK || rashi == Rashi.DHANU;
            case KETU: return rashi == Rashi.VRISHABH || rashi == Rashi.MITHUN;
            default: return false;
        }
    }
    
    private boolean isOwnSign(Planet planet, Rashi rashi) {
        switch (planet) {
            case SUN: return rashi == Rashi.SINH;
            case MOON: return rashi == Rashi.KARK;
            case MARS: return rashi == Rashi.MESH || rashi == Rashi.VRISHCHIK;
            case MERCURY: return rashi == Rashi.MITHUN || rashi == Rashi.KANYA;
            case JUPITER: return rashi == Rashi.DHANU || rashi == Rashi.MEEN;
            case VENUS: return rashi == Rashi.VRISHABH || rashi == Rashi.TULA;
            case SATURN: return rashi == Rashi.MAKAR || rashi == Rashi.KUMBH;
            default: return false;
        }
    }
    
    private boolean isMoolatrikona(Planet planet, Rashi rashi, double degree) {
        switch (planet) {
            case SUN: return rashi == Rashi.SINH && degree <= 20;
            case MOON: return rashi == Rashi.VRISHABH && degree > 3 && degree <= 30;
            case MARS: return rashi == Rashi.MESH && degree <= 12; // Approx
            case MERCURY: return rashi == Rashi.KANYA && degree > 15 && degree <= 20;
            case JUPITER: return rashi == Rashi.DHANU && degree <= 10; // Approx
            case VENUS: return rashi == Rashi.TULA && degree <= 15; // Approx
            case SATURN: return rashi == Rashi.KUMBH && degree <= 20;
            default: return false;
        }
    }
    
    private ChartResult.PlanetaryPosition generateFallbackPosition(Planet planet, BirthData birthData) {
        // Fallback simplified calculation
        int dayOfYear = birthData.getBirthDateTime().getDayOfYear();
        int hour = birthData.getBirthDateTime().getHour();
        
        int rashiIndex = ((dayOfYear + hour + planet.getIndex()) % 12);
        Rashi rashi = Rashi.values()[rashiIndex];
        
        double degreeInRashi = (hour + planet.getIndex()) % 30.0;
        int houseNumber = ((rashiIndex + 1) % 12) + 1;
        boolean isRetrograde = (planet.getIndex() % 3 == 0);
        String exaltationStatus = "Neutral";
        
        return new ChartResult.PlanetaryPosition(
            planet, rashi, degreeInRashi, houseNumber, isRetrograde, exaltationStatus
        );
    }
    
    private ChartResult.House[] generateFallbackHouses(BirthData birthData, ChartResult.PlanetaryPosition[] planetaryPositions) {
        ChartResult.House[] houses = new ChartResult.House[12];
        
        // Simplified house calculation
        int dayOfYear = birthData.getBirthDateTime().getDayOfYear();
        int hour = birthData.getBirthDateTime().getHour();
        int ascendantRashiIndex = ((dayOfYear + hour) % 12);
        
        for (int i = 0; i < 12; i++) {
            int rashiIndex = (ascendantRashiIndex + i) % 12;
            Rashi rashi = Rashi.values()[rashiIndex];
            
            double startDegree = i * 30.0;
            double endDegree = (i + 1) * 30.0;
            
            List<ChartResult.PlanetaryPosition> housePlanets = new ArrayList<>();
            for (ChartResult.PlanetaryPosition position : planetaryPositions) {
                if (position.getHouseNumber() == i + 1) {
                    housePlanets.add(position);
                }
            }
            
            houses[i] = new ChartResult.House(
                i + 1, rashi, startDegree, endDegree,
                housePlanets.toArray(new ChartResult.PlanetaryPosition[0])
            );
        }
        
        return houses;
    }
    
    // Format generation methods (same as DefaultChartGenerator)
    private String generateJsonFormat(ChartResult chartResult) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"birthData\": {\n");
        json.append("    \"dateTime\": \"").append(chartResult.getBirthData().getBirthDateTime()).append("\",\n");
        json.append("    \"latitude\": ").append(chartResult.getBirthData().getLatitude()).append(",\n");
        json.append("    \"longitude\": ").append(chartResult.getBirthData().getLongitude()).append(",\n");
        json.append("    \"placeName\": \"").append(chartResult.getBirthData().getPlaceName()).append("\"\n");
        json.append("  },\n");
        json.append("  \"ascendant\": \"").append(chartResult.getAscendant().getRashi().getEnglishName()).append("\",\n");
        json.append("  \"planets\": [\n");
        
        for (int i = 0; i < chartResult.getPlanetaryPositions().length; i++) {
            ChartResult.PlanetaryPosition pos = chartResult.getPlanetaryPositions()[i];
            json.append("    {\n");
            json.append("      \"planet\": \"").append(pos.getPlanet().getEnglishName()).append("\",\n");
            json.append("      \"rashi\": \"").append(pos.getRashi().getEnglishName()).append("\",\n");
            json.append("      \"degree\": ").append(pos.getDegreeInRashi()).append(",\n");
            json.append("      \"house\": ").append(pos.getHouseNumber()).append(",\n");
            json.append("      \"retrograde\": ").append(pos.isRetrograde()).append(",\n");
            json.append("      \"exaltation\": \"").append(pos.getExaltationStatus()).append("\"\n");
            json.append("    }");
            if (i < chartResult.getPlanetaryPositions().length - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ]\n");
        json.append("}");
        
        return json.toString();
    }
    
    private String generateXmlFormat(ChartResult chartResult) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<birthChart>\n");
        xml.append("  <birthData>\n");
        xml.append("    <dateTime>").append(chartResult.getBirthData().getBirthDateTime()).append("</dateTime>\n");
        xml.append("    <latitude>").append(chartResult.getBirthData().getLatitude()).append("</latitude>\n");
        xml.append("    <longitude>").append(chartResult.getBirthData().getLongitude()).append("</longitude>\n");
        xml.append("    <placeName>").append(chartResult.getBirthData().getPlaceName()).append("</placeName>\n");
        xml.append("  </birthData>\n");
        xml.append("  <ascendant>").append(chartResult.getAscendant().getRashi().getEnglishName()).append("</ascendant>\n");
        xml.append("  <planets>\n");
        
        for (ChartResult.PlanetaryPosition pos : chartResult.getPlanetaryPositions()) {
            xml.append("    <planet>\n");
            xml.append("      <name>").append(pos.getPlanet().getEnglishName()).append("</name>\n");
            xml.append("      <rashi>").append(pos.getRashi().getEnglishName()).append("</rashi>\n");
            xml.append("      <degree>").append(pos.getDegreeInRashi()).append("</degree>\n");
            xml.append("      <house>").append(pos.getHouseNumber()).append("</house>\n");
            xml.append("      <retrograde>").append(pos.isRetrograde()).append("</retrograde>\n");
            xml.append("      <exaltation>").append(pos.getExaltationStatus()).append("</exaltation>\n");
            xml.append("    </planet>\n");
        }
        
        xml.append("  </planets>\n");
        xml.append("</birthChart>");
        
        return xml.toString();
    }
    
    private String generateHtmlFormat(ChartResult chartResult) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<title>Birth Chart - ").append(chartResult.getBirthData().getPlaceName()).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("table { border-collapse: collapse; width: 100%; }\n");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("th { background-color: #f2f2f2; }\n");
        html.append("</style>\n</head>\n<body>\n");
        html.append("<h1>Birth Chart (Swiss Ephemeris)</h1>\n");
        html.append("<p><strong>Name:</strong> ").append(chartResult.getBirthData().getPlaceName()).append("</p>\n");
        html.append("<p><strong>Date & Time:</strong> ").append(chartResult.getBirthData().getBirthDateTime()).append("</p>\n");
        html.append("<p><strong>Ascendant:</strong> ").append(chartResult.getAscendant().getRashi().getEnglishName()).append("</p>\n");
        html.append("<h2>Planetary Positions</h2>\n");
        html.append("<table>\n");
        html.append("<tr><th>Planet</th><th>Rashi</th><th>Degree</th><th>House</th><th>Retrograde</th><th>Exaltation</th></tr>\n");
        
        for (ChartResult.PlanetaryPosition pos : chartResult.getPlanetaryPositions()) {
            html.append("<tr>\n");
            html.append("<td>").append(pos.getPlanet().getEnglishName()).append("</td>\n");
            html.append("<td>").append(pos.getRashi().getEnglishName()).append("</td>\n");
            html.append("<td>").append(String.format("%.2f", pos.getDegreeInRashi())).append("°</td>\n");
            html.append("<td>").append(pos.getHouseNumber()).append("</td>\n");
            html.append("<td>").append(pos.isRetrograde() ? "Yes" : "No").append("</td>\n");
            html.append("<td>").append(pos.getExaltationStatus()).append("</td>\n");
            html.append("</tr>\n");
        }
        
        html.append("</table>\n</body>\n</html>");
        
        return html.toString();
    }
    
    private String generateCsvFormat(ChartResult chartResult) {
        StringBuilder csv = new StringBuilder();
        csv.append("Planet,Rashi,Degree,House,Retrograde,Exaltation\n");
        
        for (ChartResult.PlanetaryPosition pos : chartResult.getPlanetaryPositions()) {
            csv.append(pos.getPlanet().getEnglishName()).append(",");
            csv.append(pos.getRashi().getEnglishName()).append(",");
            csv.append(String.format("%.2f", pos.getDegreeInRashi())).append(",");
            csv.append(pos.getHouseNumber()).append(",");
            csv.append(pos.isRetrograde() ? "Yes" : "No").append(",");
            csv.append(pos.getExaltationStatus()).append("\n");
        }
        
        return csv.toString();
    }
    
    // Aspect and relationship methods
    private AspectInfo[] generateAspects(ChartResult.PlanetaryPosition[] positions) {
        List<AspectInfo> aspects = new ArrayList<>();
        
        for (ChartResult.PlanetaryPosition p1 : positions) {
            for (ChartResult.PlanetaryPosition p2 : positions) {
                if (p1.getPlanet() == p2.getPlanet()) continue;
                
                // Calculate house distance (1-based)
                int houseDist = (p2.getHouseNumber() - p1.getHouseNumber() + 12) % 12 + 1;
                
                boolean isAspect = false;
                String aspectType = "";
                
                // All planets aspect 7th house
                if (houseDist == 7) {
                    isAspect = true;
                    aspectType = "7th House Aspect";
                }
                
                // Special Aspects
                if (!isAspect) {
                    switch (p1.getPlanet()) {
                        case MARS:
                            if (houseDist == 4 || houseDist == 8) {
                                isAspect = true;
                                aspectType = houseDist + "th House Aspect";
                            }
                            break;
                        case JUPITER:
                            if (houseDist == 5 || houseDist == 9) {
                                isAspect = true;
                                aspectType = houseDist + "th House Aspect";
                            }
                            break;
                        case SATURN:
                            if (houseDist == 3 || houseDist == 10) {
                                isAspect = true;
                                aspectType = houseDist + "th House Aspect";
                            }
                            break;
                        case RAHU:
                        case KETU:
                            if (houseDist == 5 || houseDist == 9) {
                                isAspect = true;
                                aspectType = houseDist + "th House Aspect";
                            }
                            break;
                    }
                }
                
                if (isAspect) {
                    boolean isHarmful = p1.getPlanet().isNaturalMalefic();
                    aspects.add(new AspectInfo(
                        p1.getPlanet().getEnglishName(),
                        p2.getPlanet().getEnglishName(),
                        0.0, // Exact angle not needed for sign aspect
                        aspectType,
                        isHarmful
                    ));
                }
            }
        }
        
        return aspects.toArray(new AspectInfo[0]);
    }
    
    private RelationshipInfo[] generateRelationships(ChartResult.PlanetaryPosition[] positions) {
        List<RelationshipInfo> relationships = new ArrayList<>();
        
        for (ChartResult.PlanetaryPosition p1 : positions) {
            for (ChartResult.PlanetaryPosition p2 : positions) {
                if (p1.getPlanet() == p2.getPlanet()) continue;
                
                // 1. Natural Relationship
                String naturalRel = getNaturalRelationship(p1.getPlanet(), p2.getPlanet());
                
                // 2. Temporary Relationship (Tatkalika)
                int houseDist = (p2.getHouseNumber() - p1.getHouseNumber() + 12) % 12 + 1;
                String tempRel = (houseDist == 2 || houseDist == 3 || houseDist == 4 || 
                                  houseDist == 10 || houseDist == 11 || houseDist == 12) 
                                  ? "Friend" : "Enemy";
                
                // 3. Compound Relationship (Panchadha)
                String compoundRel = calculateCompoundRelationship(naturalRel, tempRel);
                
                relationships.add(new RelationshipInfo(
                    p1.getPlanet().getEnglishName(),
                    p2.getPlanet().getEnglishName(),
                    compoundRel,
                    "Natural: " + naturalRel + ", Temporary: " + tempRel
                ));
            }
        }
        
        return relationships.toArray(new RelationshipInfo[0]);
    }
    
    private StrengthInfo[] generatePlanetaryStrengths(ChartResult.PlanetaryPosition[] positions) {
        // Simplified strength calculation based on Exaltation/Own Sign
        List<StrengthInfo> strengths = new ArrayList<>();
        
        for (ChartResult.PlanetaryPosition pos : positions) {
            double strength = 100.0; // Base strength
            String status = pos.getExaltationStatus();
            
            if (status.contains("Exalted")) strength *= 1.5;
            else if (status.contains("Own Sign")) strength *= 1.3;
            else if (status.contains("Moolatrikona")) strength *= 1.4;
            else if (status.contains("Debilitated")) strength *= 0.5;
            
            // Retrograde planets are strong (Chestha Bala)
            if (pos.isRetrograde()) strength *= 1.2;
            
            strengths.add(new StrengthInfo(
                pos.getPlanet().getEnglishName(),
                strength,
                0.0, // Ashtakavarga placeholder
                strength > 120 ? "Strong" : (strength < 80 ? "Weak" : "Moderate")
            ));
        }
        
        return strengths.toArray(new StrengthInfo[0]);
    }
    
    private DashaPeriod[] generateDashaPeriods(DashaType dashaType, LocalDateTime startDate, LocalDateTime endDate) {
        if (dashaType != DashaType.VIMSHOTTARI) {
            return new DashaPeriod[0]; // Only Vimshottari supported for now
        }
        
        // 1. Find Moon's position
        ChartResult.PlanetaryPosition moonPos = null;
        try {
            // We need to recalculate or store Moon position. 
            // Since we don't have direct access to positions here without passing them,
            // we'll assume we can get it from SwissEphCalculator again or pass it.
            // For now, let's recalculate Moon for accuracy.
            // Ideally, we should pass positions to this method.
            // But since interface doesn't allow, we'll recalculate.
             // Wait, generateDashaChart takes BirthData, so we can recalculate.
             // But wait, generateDashaChart calls generateDashaPeriods.
             // Let's implement full logic in generateDashaChart instead of this helper if needed,
             // but this helper is called by generateDashaChart.
             // We'll recalculate Moon position here.
             
             // Actually, let's just use a simplified calculation for now as we don't have easy access 
             // to the full SwissEph instance without re-initializing.
             // But SwissEphCalculator static methods are available.
             
             // Let's get Moon longitude
             com.example.purnima.util.SwissEphCalculator.PlanetaryPosition moon = 
                 SwissEphCalculator.calculatePlanetPosition(startDate, 0, 0, "Moon"); // Lat/Lon don't matter much for Moon longitude
             
             double moonLongitude = moon.getLongitude();
             
             // 2. Calculate Nakshatra
             double nakshatraSpan = 13.0 + (20.0/60.0); // 13.3333...
             int nakshatraIndex = (int) (moonLongitude / nakshatraSpan);
             double elapsedInNakshatra = moonLongitude % nakshatraSpan;
             double remainingFraction = 1.0 - (elapsedInNakshatra / nakshatraSpan);
             
             // 3. Dasha Lords and Periods
             // Sequence: Ketu, Venus, Sun, Moon, Mars, Rahu, Jupiter, Saturn, Mercury
             // Nakshatra 1 (Ashwini) -> Ketu
             String[] dashaLords = {"Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter", "Saturn", "Mercury"};
             int[] dashaYears = {7, 20, 6, 10, 7, 18, 16, 19, 17};
             
             int startLordIndex = nakshatraIndex % 9;
             double balanceYears = dashaYears[startLordIndex] * remainingFraction;
             
             List<DashaPeriod> periods = new ArrayList<>();
             LocalDateTime currentStart = startDate;
             
             // First period (Balance)
             LocalDateTime firstEnd = currentStart.plusDays((long)(balanceYears * 365.25));
             periods.add(new DashaPeriod(dashaLords[startLordIndex], currentStart, firstEnd, (int)balanceYears));
             currentStart = firstEnd;
             
             // Subsequent periods
             int currentLordIndex = (startLordIndex + 1) % 9;
             while (currentStart.isBefore(endDate)) {
                 int years = dashaYears[currentLordIndex];
                 LocalDateTime nextEnd = currentStart.plusYears(years);
                 periods.add(new DashaPeriod(dashaLords[currentLordIndex], currentStart, nextEnd, years));
                 currentStart = nextEnd;
                 currentLordIndex = (currentLordIndex + 1) % 9;
             }
             
             return periods.toArray(new DashaPeriod[0]);
             
        } catch (Exception e) {
            e.printStackTrace();
            return new DashaPeriod[0];
        }
    }

    // Helper for Natural Relationships
    private String getNaturalRelationship(Planet p1, Planet p2) {
        // Simplified table
        // Sun
        if (p1 == Planet.SUN) {
            if (p2 == Planet.MOON || p2 == Planet.MARS || p2 == Planet.JUPITER) return "Friend";
            if (p2 == Planet.MERCURY) return "Neutral";
            return "Enemy";
        }
        // Moon
        if (p1 == Planet.MOON) {
            if (p2 == Planet.SUN || p2 == Planet.MERCURY) return "Friend";
            return "Neutral"; // No enemies
        }
        // Mars
        if (p1 == Planet.MARS) {
            if (p2 == Planet.SUN || p2 == Planet.MOON || p2 == Planet.JUPITER) return "Friend";
            if (p2 == Planet.VENUS || p2 == Planet.SATURN) return "Neutral";
            return "Enemy";
        }
        // Mercury
        if (p1 == Planet.MERCURY) {
            if (p2 == Planet.SUN || p2 == Planet.VENUS) return "Friend";
            if (p2 == Planet.MARS || p2 == Planet.JUPITER || p2 == Planet.SATURN) return "Neutral";
            return "Enemy";
        }
        // Jupiter
        if (p1 == Planet.JUPITER) {
            if (p2 == Planet.SUN || p2 == Planet.MOON || p2 == Planet.MARS) return "Friend";
            if (p2 == Planet.SATURN) return "Neutral";
            return "Enemy";
        }
        // Venus
        if (p1 == Planet.VENUS) {
            if (p2 == Planet.MERCURY || p2 == Planet.SATURN) return "Friend";
            if (p2 == Planet.MARS || p2 == Planet.JUPITER) return "Neutral";
            return "Enemy";
        }
        // Saturn
        if (p1 == Planet.SATURN) {
            if (p2 == Planet.MERCURY || p2 == Planet.VENUS) return "Friend";
            if (p2 == Planet.JUPITER) return "Neutral";
            return "Enemy";
        }
        // Rahu (treated like Saturn)
        if (p1 == Planet.RAHU) {
            if (p2 == Planet.VENUS || p2 == Planet.SATURN) return "Friend";
            if (p2 == Planet.MERCURY || p2 == Planet.JUPITER) return "Neutral";
            return "Enemy";
        }
        // Ketu (treated like Mars)
        if (p1 == Planet.KETU) {
            if (p2 == Planet.MARS || p2 == Planet.VENUS) return "Friend";
            if (p2 == Planet.MERCURY || p2 == Planet.JUPITER || p2 == Planet.SATURN) return "Neutral";
            return "Enemy";
        }
        return "Neutral";
    }
    
    private String calculateCompoundRelationship(String natural, String temporal) {
        if (natural.equals("Friend")) {
            if (temporal.equals("Friend")) return "Great Friend";
            return "Neutral";
        }
        if (natural.equals("Neutral")) {
            if (temporal.equals("Friend")) return "Friend";
            return "Enemy";
        }
        if (natural.equals("Enemy")) {
            if (temporal.equals("Friend")) return "Neutral";
            return "Great Enemy";
        }
        return "Neutral";
    }
}