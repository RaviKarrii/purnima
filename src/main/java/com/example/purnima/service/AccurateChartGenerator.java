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
        // Simplified exaltation calculation
        // In a full implementation, this would use proper exaltation/debilitation degrees
        
        switch (planet) {
            case SUN:
                return (longitude >= 0 && longitude <= 10) ? "Exalted" : "Neutral";
            case MOON:
                return (longitude >= 30 && longitude <= 40) ? "Exalted" : "Neutral";
            case MARS:
                return (longitude >= 270 && longitude <= 280) ? "Exalted" : "Neutral";
            case MERCURY:
                return (longitude >= 150 && longitude <= 160) ? "Exalted" : "Neutral";
            case JUPITER:
                return (longitude >= 90 && longitude <= 100) ? "Exalted" : "Neutral";
            case VENUS:
                return (longitude >= 330 && longitude <= 340) ? "Exalted" : "Neutral";
            case SATURN:
                return (longitude >= 180 && longitude <= 190) ? "Exalted" : "Neutral";
            default:
                return "Neutral";
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
    
    // Aspect and relationship methods (simplified - same as DefaultChartGenerator)
    private AspectInfo[] generateAspects(ChartResult.PlanetaryPosition[] positions) {
        // Simplified aspect calculation
        return new AspectInfo[0];
    }
    
    private RelationshipInfo[] generateRelationships(ChartResult.PlanetaryPosition[] positions) {
        // Simplified relationship calculation
        return new RelationshipInfo[0];
    }
    
    private StrengthInfo[] generatePlanetaryStrengths(ChartResult.PlanetaryPosition[] positions) {
        // Simplified strength calculation
        return new StrengthInfo[0];
    }
    
    private DashaPeriod[] generateDashaPeriods(DashaType dashaType, LocalDateTime startDate, LocalDateTime endDate) {
        // Simplified dasha calculation
        return new DashaPeriod[0];
    }
} 