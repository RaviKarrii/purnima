package com.example.purnima.service;

import com.example.purnima.api.ChartGenerator;
import com.example.purnima.model.BirthData;
import com.example.purnima.model.ChartResult;
import com.example.purnima.model.Planet;
import com.example.purnima.model.Rashi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of ChartGenerator.
 * Provides basic chart generation functionality based on Vedic astrology principles.
 */
public class DefaultChartGenerator implements ChartGenerator {
    
    @Override
    public ChartResult generateBirthChart(BirthData birthData) {
        // Generate planetary positions (simplified calculation)
        ChartResult.PlanetaryPosition[] planetaryPositions = generatePlanetaryPositions(birthData);
        
        // Generate houses (simplified calculation)
        ChartResult.House[] houses = generateHouses(birthData, planetaryPositions);
        
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
    
    private ChartResult.PlanetaryPosition[] generatePlanetaryPositions(BirthData birthData) {
        List<ChartResult.PlanetaryPosition> positions = new ArrayList<>();
        
        // Generate positions for all planets (simplified calculation)
        Planet[] planets = Planet.values();
        for (int i = 0; i < planets.length; i++) {
            Planet planet = planets[i];
            
            // Simplified calculation based on birth time
            int dayOfYear = birthData.getBirthDateTime().getDayOfYear();
            int hour = birthData.getBirthDateTime().getHour();
            
            // Calculate rashi based on day and hour
            int rashiIndex = ((dayOfYear + hour + i) % 12);
            Rashi rashi = Rashi.values()[rashiIndex];
            
            // Calculate degree within rashi
            double degreeInRashi = (hour + i) % 30.0;
            
            // Calculate house number
            int houseNumber = ((rashiIndex + 1) % 12) + 1;
            
            // Determine if retrograde (simplified)
            boolean isRetrograde = (i % 3 == 0); // Every third planet is retrograde for demo
            
            // Determine exaltation status (simplified)
            String exaltationStatus = (i % 4 == 0) ? "Exalted" : 
                                    (i % 4 == 1) ? "Debilitated" : "Neutral";
            
            positions.add(new ChartResult.PlanetaryPosition(
                planet, rashi, degreeInRashi, houseNumber, isRetrograde, exaltationStatus
            ));
        }
        
        return positions.toArray(new ChartResult.PlanetaryPosition[0]);
    }
    
    private ChartResult.House[] generateHouses(BirthData birthData, ChartResult.PlanetaryPosition[] planetaryPositions) {
        ChartResult.House[] houses = new ChartResult.House[12];
        
        // Calculate ascendant (simplified)
        int dayOfYear = birthData.getBirthDateTime().getDayOfYear();
        int hour = birthData.getBirthDateTime().getHour();
        int ascendantRashiIndex = ((dayOfYear + hour) % 12);
        
        for (int i = 0; i < 12; i++) {
            int rashiIndex = (ascendantRashiIndex + i) % 12;
            Rashi rashi = Rashi.values()[rashiIndex];
            
            double startDegree = i * 30.0;
            double endDegree = (i + 1) * 30.0;
            
            // Find planets in this house
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
    
    private AspectInfo[] generateAspects(ChartResult.PlanetaryPosition[] positions) {
        List<AspectInfo> aspects = new ArrayList<>();
        
        // Generate aspects between planets (simplified)
        for (int i = 0; i < positions.length; i++) {
            for (int j = i + 1; j < positions.length; j++) {
                ChartResult.PlanetaryPosition pos1 = positions[i];
                ChartResult.PlanetaryPosition pos2 = positions[j];
                
                // Calculate aspect angle (simplified)
                double angle = Math.abs(pos1.getDegreeInRashi() - pos2.getDegreeInRashi());
                
                // Determine aspect type
                String aspectType = "Conjunction";
                boolean isHarmful = false;
                
                if (angle >= 60 && angle <= 120) {
                    aspectType = "Trine";
                    isHarmful = false;
                } else if (angle >= 90 && angle <= 150) {
                    aspectType = "Square";
                    isHarmful = true;
                } else if (angle >= 150 && angle <= 210) {
                    aspectType = "Opposition";
                    isHarmful = true;
                }
                
                aspects.add(new AspectInfo(
                    pos1.getPlanet().getEnglishName(),
                    pos2.getPlanet().getEnglishName(),
                    angle,
                    aspectType,
                    isHarmful
                ));
            }
        }
        
        return aspects.toArray(new AspectInfo[0]);
    }
    
    private RelationshipInfo[] generateRelationships(ChartResult.PlanetaryPosition[] positions) {
        List<RelationshipInfo> relationships = new ArrayList<>();
        
        // Generate relationships between planets (simplified)
        for (int i = 0; i < positions.length; i++) {
            for (int j = i + 1; j < positions.length; j++) {
                ChartResult.PlanetaryPosition pos1 = positions[i];
                ChartResult.PlanetaryPosition pos2 = positions[j];
                
                String relationshipType = "Neutral";
                String description = "No special relationship";
                
                // Determine relationship based on planets
                if (pos1.getPlanet().isNaturalBenefic() && pos2.getPlanet().isNaturalBenefic()) {
                    relationshipType = "Friendly";
                    description = "Both planets are natural benefics";
                } else if (pos1.getPlanet().isNaturalMalefic() && pos2.getPlanet().isNaturalMalefic()) {
                    relationshipType = "Enemy";
                    description = "Both planets are natural malefics";
                }
                
                relationships.add(new RelationshipInfo(
                    pos1.getPlanet().getEnglishName(),
                    pos2.getPlanet().getEnglishName(),
                    relationshipType,
                    description
                ));
            }
        }
        
        return relationships.toArray(new RelationshipInfo[0]);
    }
    
    private StrengthInfo[] generatePlanetaryStrengths(ChartResult.PlanetaryPosition[] positions) {
        List<StrengthInfo> strengths = new ArrayList<>();
        
        for (ChartResult.PlanetaryPosition position : positions) {
            // Simplified strength calculation
            double shadbala = 50.0 + (Math.random() * 50.0); // Random strength between 50-100
            double ashtakavarga = 30.0 + (Math.random() * 40.0); // Random strength between 30-70
            
            String strengthLevel = "Medium";
            if (shadbala > 80) strengthLevel = "Strong";
            else if (shadbala < 30) strengthLevel = "Weak";
            
            strengths.add(new StrengthInfo(
                position.getPlanet().getEnglishName(),
                shadbala,
                ashtakavarga,
                strengthLevel
            ));
        }
        
        return strengths.toArray(new StrengthInfo[0]);
    }
    
    private DashaPeriod[] generateDashaPeriods(DashaType dashaType, LocalDateTime startDate, LocalDateTime endDate) {
        List<DashaPeriod> periods = new ArrayList<>();
        
        // Simplified dasha periods
        String[] planets = {"Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"};
        int[] durations = {6, 10, 7, 17, 16, 20, 19}; // Vimshottari dasha durations
        
        LocalDateTime currentDate = startDate;
        for (int i = 0; i < planets.length; i++) {
            LocalDateTime periodEnd = currentDate.plusYears(durations[i]);
            if (periodEnd.isAfter(endDate)) {
                periodEnd = endDate;
            }
            
            periods.add(new DashaPeriod(
                planets[i],
                currentDate,
                periodEnd,
                durations[i]
            ));
            
            currentDate = periodEnd;
            if (currentDate.isAfter(endDate)) break;
        }
        
        return periods.toArray(new DashaPeriod[0]);
    }
    
    private String generateJsonFormat(ChartResult chartResult) {
        // Simplified JSON generation
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
            json.append("      \"house\": ").append(pos.getHouseNumber()).append("\n");
            json.append("    }");
            if (i < chartResult.getPlanetaryPositions().length - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ]\n");
        json.append("}");
        
        return json.toString();
    }
    
    private String generateXmlFormat(ChartResult chartResult) {
        // Simplified XML generation
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
            xml.append("    </planet>\n");
        }
        
        xml.append("  </planets>\n");
        xml.append("</birthChart>");
        
        return xml.toString();
    }
    
    private String generateHtmlFormat(ChartResult chartResult) {
        // Simplified HTML generation
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<title>Birth Chart - ").append(chartResult.getBirthData().getPlaceName()).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("table { border-collapse: collapse; width: 100%; }\n");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("th { background-color: #f2f2f2; }\n");
        html.append("</style>\n</head>\n<body>\n");
        html.append("<h1>Birth Chart</h1>\n");
        html.append("<p><strong>Name:</strong> ").append(chartResult.getBirthData().getPlaceName()).append("</p>\n");
        html.append("<p><strong>Date & Time:</strong> ").append(chartResult.getBirthData().getBirthDateTime()).append("</p>\n");
        html.append("<p><strong>Ascendant:</strong> ").append(chartResult.getAscendant().getRashi().getEnglishName()).append("</p>\n");
        html.append("<h2>Planetary Positions</h2>\n");
        html.append("<table>\n");
        html.append("<tr><th>Planet</th><th>Rashi</th><th>Degree</th><th>House</th></tr>\n");
        
        for (ChartResult.PlanetaryPosition pos : chartResult.getPlanetaryPositions()) {
            html.append("<tr>\n");
            html.append("<td>").append(pos.getPlanet().getEnglishName()).append("</td>\n");
            html.append("<td>").append(pos.getRashi().getEnglishName()).append("</td>\n");
            html.append("<td>").append(String.format("%.2f", pos.getDegreeInRashi())).append("°</td>\n");
            html.append("<td>").append(pos.getHouseNumber()).append("</td>\n");
            html.append("</tr>\n");
        }
        
        html.append("</table>\n</body>\n</html>");
        
        return html.toString();
    }
    
    private String generateCsvFormat(ChartResult chartResult) {
        // Simplified CSV generation
        StringBuilder csv = new StringBuilder();
        csv.append("Planet,Rashi,Degree,House\n");
        
        for (ChartResult.PlanetaryPosition pos : chartResult.getPlanetaryPositions()) {
            csv.append(pos.getPlanet().getEnglishName()).append(",");
            csv.append(pos.getRashi().getEnglishName()).append(",");
            csv.append(String.format("%.2f", pos.getDegreeInRashi())).append(",");
            csv.append(pos.getHouseNumber()).append("\n");
        }
        
        return csv.toString();
    }
} 