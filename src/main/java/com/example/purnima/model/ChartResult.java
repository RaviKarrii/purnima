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
        private final String planetName;
        private final Rashi rashi;
        private final String rashiName;
        private final double degreeInRashi;
        private final int houseNumber;
        private final boolean isRetrograde;
        private final String exaltationStatus;

        public PlanetaryPosition(Planet planet, String planetName, Rashi rashi, String rashiName, double degreeInRashi, 
                               int houseNumber, boolean isRetrograde, String exaltationStatus) {
            this.planet = planet;
            this.planetName = planetName;
            this.rashi = rashi;
            this.rashiName = rashiName;
            this.degreeInRashi = degreeInRashi;
            this.houseNumber = houseNumber;
            this.isRetrograde = isRetrograde;
            this.exaltationStatus = exaltationStatus;
        }

        // Getters
        public Planet getPlanet() { return planet; }
        public String getPlanetName() { return planetName; }
        public Rashi getRashi() { return rashi; }
        public String getRashiName() { return rashiName; }
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
        private final String rashiName;
        private final double startDegree;
        private final double endDegree;
        private final PlanetaryPosition[] planets;

        public House(int houseNumber, Rashi rashi, String rashiName, double startDegree, double endDegree, PlanetaryPosition[] planets) {
            this.houseNumber = houseNumber;
            this.rashi = rashi;
            this.rashiName = rashiName;
            this.startDegree = startDegree;
            this.endDegree = endDegree;
            this.planets = planets;
        }

        // Getters
        public int getHouseNumber() { return houseNumber; }
        public Rashi getRashi() { return rashi; }
        public String getRashiName() { return rashiName; }
        public double getStartDegree() { return startDegree; }
        public double getEndDegree() { return endDegree; }
        public PlanetaryPosition[] getPlanets() { return planets; }
    }
} 