package com.example.purnima.model;

/**
 * Represents the nine planets (grahas) in Vedic astrology.
 */
public enum Planet {
    SUN("Surya", "सूर्य", "☉", 0, 0),
    MOON("Chandra", "चंद्र", "☽", 1, 1),
    MARS("Mangal", "मंगल", "♂", 2, 4),
    MERCURY("Budh", "बुध", "☿", 3, 2),
    JUPITER("Guru", "गुरु", "♃", 4, 5),
    VENUS("Shukra", "शुक्र", "♀", 5, 3),
    SATURN("Shani", "शनि", "♄", 6, 6),
    RAHU("Rahu", "राहु", "☊", 7, 10),
    KETU("Ketu", "केतु", "☋", 8, 12);

    private final String englishName;
    private final String sanskritName;
    private final String symbol;
    private final int index;
    private final int swissEphId;

    Planet(String englishName, String sanskritName, String symbol, int index, int swissEphId) {
        this.englishName = englishName;
        this.sanskritName = sanskritName;
        this.symbol = symbol;
        this.index = index;
        this.swissEphId = swissEphId;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getSanskritName() {
        return sanskritName;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getIndex() {
        return index;
    }

    /**
     * Get the Swiss Ephemeris ID for this planet.
     */
    public int getSwissEphId() {
        return swissEphId;
    }

    /**
     * Check if the planet is a benefic planet.
     * In Vedic astrology, Jupiter and Venus are natural benefics.
     */
    public boolean isNaturalBenefic() {
        return this == JUPITER || this == VENUS;
    }

    /**
     * Check if the planet is a malefic planet.
     * In Vedic astrology, Mars, Saturn, Rahu, and Ketu are natural malefics.
     */
    public boolean isNaturalMalefic() {
        return this == MARS || this == SATURN || this == RAHU || this == KETU;
    }

    /**
     * Check if the planet is a shadow planet (Rahu or Ketu).
     */
    public boolean isShadowPlanet() {
        return this == RAHU || this == KETU;
    }

    /**
     * Get planet by Swiss Ephemeris ID.
     */
    public static Planet getBySwissEphId(int swissEphId) {
        for (Planet planet : values()) {
            if (planet.swissEphId == swissEphId) {
                return planet;
            }
        }
        throw new IllegalArgumentException("Unknown Swiss Ephemeris ID: " + swissEphId);
    }

    @Override
    public String toString() {
        return englishName + " (" + sanskritName + ")";
    }
} 