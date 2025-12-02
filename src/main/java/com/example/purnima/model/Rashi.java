package com.example.purnima.model;

/**
 * Represents the 12 zodiac signs (rashis) in Vedic astrology.
 */
public enum Rashi {
    MESH("Aries", "Mesha", "rashi.aries", "♈", Element.FIRE, Quality.CARDINAL, 0, 30),
    VRISHABH("Taurus", "Vrishabha", "rashi.taurus", "♉", Element.EARTH, Quality.FIXED, 30, 60),
    MITHUN("Gemini", "Mithuna", "rashi.gemini", "♊", Element.AIR, Quality.MUTABLE, 60, 90),
    KARK("Cancer", "Karka", "rashi.cancer", "♋", Element.WATER, Quality.CARDINAL, 90, 120),
    SINH("Leo", "Simha", "rashi.leo", "♌", Element.FIRE, Quality.FIXED, 120, 150),
    KANYA("Virgo", "Kanya", "rashi.virgo", "♍", Element.EARTH, Quality.MUTABLE, 150, 180),
    TULA("Libra", "Tula", "rashi.libra", "♎", Element.AIR, Quality.CARDINAL, 180, 210),
    VRISHCHIK("Scorpio", "Vrishchika", "rashi.scorpio", "♏", Element.WATER, Quality.FIXED, 210, 240),
    DHANU("Sagittarius", "Dhanu", "rashi.sagittarius", "♐", Element.FIRE, Quality.MUTABLE, 240, 270),
    MAKAR("Capricorn", "Makara", "rashi.capricorn", "♑", Element.EARTH, Quality.CARDINAL, 270, 300),
    KUMBH("Aquarius", "Kumbha", "rashi.aquarius", "♒", Element.AIR, Quality.FIXED, 300, 330),
    MEEN("Pisces", "Meena", "rashi.pisces", "♓", Element.WATER, Quality.MUTABLE, 330, 360);

    private final String englishName;
    private final String sanskritName;
    private final String messageKey;
    private final String symbol;
    private final Element element;
    private final Quality quality;
    private final int startDegree;
    private final int endDegree;

    Rashi(String englishName, String sanskritName, String messageKey, String symbol, Element element, Quality quality, int startDegree, int endDegree) {
        this.englishName = englishName;
        this.sanskritName = sanskritName;
        this.messageKey = messageKey;
        this.symbol = symbol;
        this.element = element;
        this.quality = quality;
        this.startDegree = startDegree;
        this.endDegree = endDegree;
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

    public Element getElement() {
        return element;
    }

    public Quality getQuality() {
        return quality;
    }

    public int getStartDegree() {
        return startDegree;
    }

    public int getEndDegree() {
        return endDegree;
    }

    /**
     * Get the rashi for a given degree (0-360).
     */
    public static Rashi getRashiForDegree(double degree) {
        int normalizedDegree = (int) (degree % 360);
        for (Rashi rashi : values()) {
            if (normalizedDegree >= rashi.startDegree && normalizedDegree < rashi.endDegree) {
                return rashi;
            }
        }
        return MESH; // Default fallback
    }

    /**
     * Get the degree within the rashi (0-30).
     */
    public double getDegreeInRashi(double absoluteDegree) {
        return absoluteDegree - startDegree;
    }

    @Override
    public String toString() {
        return englishName;
    }

    /**
     * Represents the four elements in Vedic astrology.
     */
    public enum Element {
        FIRE("Agni", "अग्नि"),
        EARTH("Prithvi", "पृथ्वी"),
        AIR("Vayu", "वायु"),
        WATER("Jal", "जल");

        private final String sanskritName;
        private final String devanagari;

        Element(String sanskritName, String devanagari) {
            this.sanskritName = sanskritName;
            this.devanagari = devanagari;
        }

        public String getSanskritName() {
            return sanskritName;
        }

        public String getDevanagari() {
            return devanagari;
        }
    }

    /**
     * Represents the three qualities in Vedic astrology.
     */
    public enum Quality {
        CARDINAL("Chara", "चर"),
        FIXED("Sthira", "स्थिर"),
        MUTABLE("Dwiswabhava", "द्विस्वभाव");

        private final String sanskritName;
        private final String devanagari;

        Quality(String sanskritName, String devanagari) {
            this.sanskritName = sanskritName;
            this.devanagari = devanagari;
        }

        public String getSanskritName() {
            return sanskritName;
        }

        public String getDevanagari() {
            return devanagari;
        }
    }
} 