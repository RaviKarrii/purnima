package com.example.purnima.model;

import java.util.Objects;

/**
 * Result class containing astrological chart information.
 * Contains planetary positions and house information.
 */
public class ChartResult {
    private final BirthData birthData;
    private final PlanetaryPosition[] planetaryPositions;
    private final House[] houses;

    public ChartResult(BirthData birthData, PlanetaryPosition[] planetaryPositions, House[] houses) {
        this.birthData = birthData;
        this.planetaryPositions = planetaryPositions;
        this.houses = houses;
    }

    // Getters
    public BirthData getBirthData() {
        return birthData;
    }

    public PlanetaryPosition[] getPlanetaryPositions() {
        return planetaryPositions;
    }

    public House[] getHouses() {
        return houses;
    }

    /**
     * Get planetary position by planet name.
     */
    public PlanetaryPosition getPlanetaryPosition(Planet planet) {
        for (PlanetaryPosition position : planetaryPositions) {
            if (position.getPlanet() == planet) {
                return position;
            }
        }
        return null;
    }

    /**
     * Get house by house number (1-12).
     */
    public House getHouse(int houseNumber) {
        if (houseNumber >= 1 && houseNumber <= 12) {
            return houses[houseNumber - 1];
        }
        return null;
    }

    /**
     * Get the ascendant (Lagna) house.
     */
    public House getAscendant() {
        return houses[0]; // First house is always the ascendant
    }

    /**
     * Get planets in a specific house.
     */
    public PlanetaryPosition[] getPlanetsInHouse(int houseNumber) {
        if (houseNumber < 1 || houseNumber > 12) {
            return new PlanetaryPosition[0];
        }
        
        House house = houses[houseNumber - 1];
        return house.getPlanets();
    }

    /**
     * Get a formatted summary of the chart.
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Birth Chart Summary\n");
        sb.append("===================\n\n");
        sb.append("Name: ").append(birthData.getPlaceName()).append("\n");
        sb.append("Date & Time: ").append(birthData.getBirthDateTime()).append("\n");
        sb.append("Location: ").append(birthData.getLatitude()).append(", ").append(birthData.getLongitude()).append("\n\n");
        
        sb.append("ASCENDANT (LAGNA)\n");
        sb.append("Rashi: ").append(getAscendant().getRashi().getEnglishName()).append(" (").append(getAscendant().getRashi().getSanskritName()).append(")\n");
        sb.append("Degree: ").append(String.format("%.2f", getAscendant().getStartDegree())).append("°\n\n");
        
        sb.append("PLANETARY POSITIONS\n");
        sb.append("==================\n");
        for (PlanetaryPosition position : planetaryPositions) {
            sb.append(position.getPlanet().getEnglishName()).append(": ");
            sb.append(position.getRashi().getEnglishName()).append(" ");
            sb.append(String.format("%.2f", position.getDegreeInRashi())).append("°\n");
        }
        
        return sb.toString();
    }

    /**
     * Get detailed chart information.
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Detailed Birth Chart\n");
        sb.append("====================\n\n");
        sb.append("BIRTH INFORMATION\n");
        sb.append("Date & Time: ").append(birthData.getBirthDateTime()).append("\n");
        sb.append("Location: ").append(birthData.getPlaceName()).append("\n");
        sb.append("Coordinates: ").append(birthData.getLatitude()).append(", ").append(birthData.getLongitude()).append("\n\n");
        
        sb.append("HOUSES (BHAVAS)\n");
        sb.append("===============\n");
        for (int i = 0; i < houses.length; i++) {
            House house = houses[i];
            sb.append("House ").append(i + 1).append(": ");
            sb.append(house.getRashi().getEnglishName()).append(" (").append(house.getRashi().getSanskritName()).append(")\n");
            sb.append("  Start: ").append(String.format("%.2f", house.getStartDegree())).append("°\n");
            sb.append("  End: ").append(String.format("%.2f", house.getEndDegree())).append("°\n");
            
            PlanetaryPosition[] planets = house.getPlanets();
            if (planets.length > 0) {
                sb.append("  Planets: ");
                for (int j = 0; j < planets.length; j++) {
                    if (j > 0) sb.append(", ");
                    sb.append(planets[j].getPlanet().getEnglishName());
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        
        sb.append("PLANETARY POSITIONS\n");
        sb.append("==================\n");
        for (PlanetaryPosition position : planetaryPositions) {
            sb.append(position.getPlanet().getEnglishName()).append(" (").append(position.getPlanet().getSanskritName()).append(")\n");
            sb.append("  Rashi: ").append(position.getRashi().getEnglishName()).append(" (").append(position.getRashi().getSanskritName()).append(")\n");
            sb.append("  Degree: ").append(String.format("%.2f", position.getDegreeInRashi())).append("° in ").append(position.getRashi().getEnglishName()).append("\n");
            sb.append("  House: ").append(position.getHouseNumber()).append("\n");
            sb.append("  Retrograde: ").append(position.isRetrograde() ? "Yes" : "No").append("\n");
            sb.append("  Exaltation: ").append(position.getExaltationStatus()).append("\n\n");
        }
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChartResult that = (ChartResult) o;
        return Objects.equals(birthData, that.birthData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthData);
    }

    @Override
    public String toString() {
        return "ChartResult{" +
                "birthData=" + birthData +
                ", ascendant=" + getAscendant().getRashi().getEnglishName() +
                '}';
    }

    /**
     * Represents planetary position in the chart.
     */
    public static class PlanetaryPosition {
        private final Planet planet;
        private final Rashi rashi;
        private final double degreeInRashi;
        private final int houseNumber;
        private final boolean isRetrograde;
        private final String exaltationStatus;

        public PlanetaryPosition(Planet planet, Rashi rashi, double degreeInRashi, 
                               int houseNumber, boolean isRetrograde, String exaltationStatus) {
            this.planet = planet;
            this.rashi = rashi;
            this.degreeInRashi = degreeInRashi;
            this.houseNumber = houseNumber;
            this.isRetrograde = isRetrograde;
            this.exaltationStatus = exaltationStatus;
        }

        // Getters
        public Planet getPlanet() { return planet; }
        public Rashi getRashi() { return rashi; }
        public double getDegreeInRashi() { return degreeInRashi; }
        public int getHouseNumber() { return houseNumber; }
        public boolean isRetrograde() { return isRetrograde; }
        public String getExaltationStatus() { return exaltationStatus; }
    }

    /**
     * Represents a house (bhava) in the chart.
     */
    public static class House {
        private final int houseNumber;
        private final Rashi rashi;
        private final double startDegree;
        private final double endDegree;
        private final PlanetaryPosition[] planets;

        public House(int houseNumber, Rashi rashi, double startDegree, double endDegree, PlanetaryPosition[] planets) {
            this.houseNumber = houseNumber;
            this.rashi = rashi;
            this.startDegree = startDegree;
            this.endDegree = endDegree;
            this.planets = planets;
        }

        // Getters
        public int getHouseNumber() { return houseNumber; }
        public Rashi getRashi() { return rashi; }
        public double getStartDegree() { return startDegree; }
        public double getEndDegree() { return endDegree; }
        public PlanetaryPosition[] getPlanets() { return planets; }
    }
} 