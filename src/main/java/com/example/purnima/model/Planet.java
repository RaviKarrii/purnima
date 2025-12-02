package com.example.purnima.model;

/**
 * Represents the nine planets (grahas) in Vedic astrology.
 */
public enum Planet {
    SUN("Sun", "Surya", "planet.sun", "☉", 0, 0),
    MOON("Moon", "Chandra", "planet.moon", "☽", 1, 1),
    MARS("Mars", "Mangala", "planet.mars", "♂", 2, 4),
    MERCURY("Mercury", "Budha", "planet.mercury", "☿", 3, 2),
    JUPITER("Jupiter", "Guru", "planet.jupiter", "♃", 4, 5),
    VENUS("Venus", "Shukra", "planet.venus", "♀", 5, 3),
    SATURN("Saturn", "Shani", "planet.saturn", "♄", 6, 6),
    RAHU("Rahu", "Rahu", "planet.rahu", "☊", 7, 10),
    KETU("Ketu", "Ketu", "planet.ketu", "☋", 8, 12);

    private final String englishName;
    private final String sanskritName;
    private final String messageKey;
    private final String symbol;
    private final int index;
    private final int swissEphId;

    Planet(String englishName, String sanskritName, String messageKey, String symbol, int index, int swissEphId) {
        this.englishName = englishName;
        this.sanskritName = sanskritName;
        this.messageKey = messageKey;
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

    public String getMessageKey() {
        return messageKey;
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
} 