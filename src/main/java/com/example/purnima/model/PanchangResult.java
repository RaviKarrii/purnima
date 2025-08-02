package com.example.purnima.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Result class containing Panchang (five elements) calculations.
 * Contains Tithi, Vara, Nakshatra, Yoga, and Karana information.
 */
public class PanchangResult {
    private final LocalDateTime dateTime;
    private final double latitude;
    private final double longitude;
    private final String placeName;
    private final TithiInfo tithi;
    private final VaraInfo vara;
    private final NakshatraInfo nakshatra;
    private final YogaInfo yoga;
    private final KaranaInfo karana;
    private final MuhurtaInfo muhurta;

    public PanchangResult(LocalDateTime dateTime, double latitude, double longitude, String placeName,
                         TithiInfo tithi, VaraInfo vara, NakshatraInfo nakshatra, 
                         YogaInfo yoga, KaranaInfo karana, MuhurtaInfo muhurta) {
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
        this.tithi = tithi;
        this.vara = vara;
        this.nakshatra = nakshatra;
        this.yoga = yoga;
        this.karana = karana;
        this.muhurta = muhurta;
    }

    // Getters
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public TithiInfo getTithi() {
        return tithi;
    }

    public VaraInfo getVara() {
        return vara;
    }

    public NakshatraInfo getNakshatra() {
        return nakshatra;
    }

    public YogaInfo getYoga() {
        return yoga;
    }

    public KaranaInfo getKarana() {
        return karana;
    }

    public MuhurtaInfo getMuhurta() {
        return muhurta;
    }

    /**
     * Get a formatted summary of the Panchang.
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Panchang for ").append(dateTime.toLocalDate()).append(" at ").append(placeName).append("\n");
        sb.append("==========================================\n\n");
        sb.append("Tithi: ").append(tithi.getTithiName()).append(" (").append(tithi.getSanskritName()).append(")\n");
        sb.append("Vara: ").append(vara.getVaraName()).append(" (").append(vara.getSanskritName()).append(")\n");
        sb.append("Nakshatra: ").append(nakshatra.getNakshatraName()).append(" (").append(nakshatra.getSanskritName()).append(")\n");
        sb.append("Yoga: ").append(yoga.getYogaName()).append(" (").append(yoga.getSanskritName()).append(")\n");
        sb.append("Karana: ").append(karana.getKaranaName()).append(" (").append(karana.getSanskritName()).append(")");
        return sb.toString();
    }

    /**
     * Get detailed Panchang information.
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Detailed Panchang Information\n");
        sb.append("=============================\n\n");
        sb.append("Date & Time: ").append(dateTime).append("\n");
        sb.append("Location: ").append(placeName).append(" (").append(latitude).append(", ").append(longitude).append(")\n\n");
        
        sb.append("1. TITHI (Lunar Day)\n");
        sb.append("   Name: ").append(tithi.getTithiName()).append(" (").append(tithi.getSanskritName()).append(")\n");
        sb.append("   Number: ").append(tithi.getTithiNumber()).append("\n");
        sb.append("   Paksha: ").append(tithi.isShuklaPaksha() ? "Shukla (Waxing)" : "Krishna (Waning)").append("\n");
        sb.append("   Start: ").append(formatTime(tithi.getStartTime())).append("\n");
        sb.append("   End: ").append(formatTime(tithi.getEndTime())).append("\n\n");
        
        sb.append("2. VARA (Weekday)\n");
        sb.append("   Name: ").append(vara.getVaraName()).append(" (").append(vara.getSanskritName()).append(")\n");
        sb.append("   Ruling Planet: ").append(vara.getRulingPlanet()).append("\n\n");
        
        sb.append("3. NAKSHATRA (Lunar Mansion)\n");
        sb.append("   Name: ").append(nakshatra.getNakshatraName()).append(" (").append(nakshatra.getSanskritName()).append(")\n");
        sb.append("   Number: ").append(nakshatra.getNakshatraNumber()).append("\n");
        sb.append("   Ruling Planet: ").append(nakshatra.getRulingPlanet()).append("\n");
        sb.append("   Start: ").append(formatTime(nakshatra.getStartTime())).append("\n");
        sb.append("   End: ").append(formatTime(nakshatra.getEndTime())).append("\n\n");
        
        sb.append("4. YOGA (Solar-Lunar Combination)\n");
        sb.append("   Name: ").append(yoga.getYogaName()).append(" (").append(yoga.getSanskritName()).append(")\n");
        sb.append("   Number: ").append(yoga.getYogaNumber()).append("\n");
        sb.append("   Start: ").append(formatTime(yoga.getStartTime())).append("\n");
        sb.append("   End: ").append(formatTime(yoga.getEndTime())).append("\n\n");
        
        sb.append("5. KARANA (Half of Tithi)\n");
        sb.append("   Name: ").append(karana.getKaranaName()).append(" (").append(karana.getSanskritName()).append(")\n");
        sb.append("   Number: ").append(karana.getKaranaNumber()).append("\n");
        sb.append("   Start: ").append(formatTime(karana.getStartTime())).append("\n");
        sb.append("   End: ").append(formatTime(karana.getEndTime())).append("\n\n");
        
        if (muhurta != null) {
            sb.append("AUSPICIOUS TIMINGS (MUHURTA)\n");
            sb.append("============================\n");
            sb.append("Brahma Muhurta: ").append(muhurta.getBrahmaMuhurta()).append("\n");
            sb.append("Abhijit Muhurta: ").append(muhurta.getAbhijitMuhurta()).append("\n");
            sb.append("Godhuli Muhurta: ").append(muhurta.getGodhuliMuhurta()).append("\n");
            sb.append("Rahu Kaal: ").append(muhurta.getRahuKaal()).append("\n");
            sb.append("Gulika Kaal: ").append(muhurta.getGulikaKaal()).append("\n");
            sb.append("Yamaganda Kaal: ").append(muhurta.getYamagandaKaal()).append("\n");
        }
        
        return sb.toString();
    }

    private String formatTime(double time) {
        int hours = (int) time;
        int minutes = (int) ((time - hours) * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PanchangResult that = (PanchangResult) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(placeName, that.placeName) &&
                Objects.equals(tithi, that.tithi) &&
                Objects.equals(vara, that.vara) &&
                Objects.equals(nakshatra, that.nakshatra) &&
                Objects.equals(yoga, that.yoga) &&
                Objects.equals(karana, that.karana) &&
                Objects.equals(muhurta, that.muhurta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, latitude, longitude, placeName, tithi, vara, nakshatra, yoga, karana, muhurta);
    }

    @Override
    public String toString() {
        return "PanchangResult{" +
                "dateTime=" + dateTime +
                ", placeName='" + placeName + '\'' +
                ", tithi=" + tithi.getTithiName() +
                ", vara=" + vara.getVaraName() +
                ", nakshatra=" + nakshatra.getNakshatraName() +
                '}';
    }

    /**
     * Inner classes for Panchang elements (moved from interface for better organization)
     */
    public static class TithiInfo {
        private final int tithiNumber;
        private final String tithiName;
        private final String sanskritName;
        private final double startTime;
        private final double endTime;
        private final boolean isShuklaPaksha;

        public TithiInfo(int tithiNumber, String tithiName, String sanskritName, 
                        double startTime, double endTime, boolean isShuklaPaksha) {
            this.tithiNumber = tithiNumber;
            this.tithiName = tithiName;
            this.sanskritName = sanskritName;
            this.startTime = startTime;
            this.endTime = endTime;
            this.isShuklaPaksha = isShuklaPaksha;
        }

        // Getters
        public int getTithiNumber() { return tithiNumber; }
        public String getTithiName() { return tithiName; }
        public String getSanskritName() { return sanskritName; }
        public double getStartTime() { return startTime; }
        public double getEndTime() { return endTime; }
        public boolean isShuklaPaksha() { return isShuklaPaksha; }
    }

    public static class VaraInfo {
        private final int varaNumber;
        private final String varaName;
        private final String sanskritName;
        private final String rulingPlanet;

        public VaraInfo(int varaNumber, String varaName, String sanskritName, String rulingPlanet) {
            this.varaNumber = varaNumber;
            this.varaName = varaName;
            this.sanskritName = sanskritName;
            this.rulingPlanet = rulingPlanet;
        }

        // Getters
        public int getVaraNumber() { return varaNumber; }
        public String getVaraName() { return varaName; }
        public String getSanskritName() { return sanskritName; }
        public String getRulingPlanet() { return rulingPlanet; }
    }

    public static class NakshatraInfo {
        private final int nakshatraNumber;
        private final String nakshatraName;
        private final String sanskritName;
        private final String rulingPlanet;
        private final double startTime;
        private final double endTime;

        public NakshatraInfo(int nakshatraNumber, String nakshatraName, String sanskritName,
                           String rulingPlanet, double startTime, double endTime) {
            this.nakshatraNumber = nakshatraNumber;
            this.nakshatraName = nakshatraName;
            this.sanskritName = sanskritName;
            this.rulingPlanet = rulingPlanet;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        // Getters
        public int getNakshatraNumber() { return nakshatraNumber; }
        public String getNakshatraName() { return nakshatraName; }
        public String getSanskritName() { return sanskritName; }
        public String getRulingPlanet() { return rulingPlanet; }
        public double getStartTime() { return startTime; }
        public double getEndTime() { return endTime; }
    }

    public static class YogaInfo {
        private final int yogaNumber;
        private final String yogaName;
        private final String sanskritName;
        private final double startTime;
        private final double endTime;

        public YogaInfo(int yogaNumber, String yogaName, String sanskritName,
                       double startTime, double endTime) {
            this.yogaNumber = yogaNumber;
            this.yogaName = yogaName;
            this.sanskritName = sanskritName;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        // Getters
        public int getYogaNumber() { return yogaNumber; }
        public String getYogaName() { return yogaName; }
        public String getSanskritName() { return sanskritName; }
        public double getStartTime() { return startTime; }
        public double getEndTime() { return endTime; }
    }

    public static class KaranaInfo {
        private final int karanaNumber;
        private final String karanaName;
        private final String sanskritName;
        private final double startTime;
        private final double endTime;

        public KaranaInfo(int karanaNumber, String karanaName, String sanskritName,
                         double startTime, double endTime) {
            this.karanaNumber = karanaNumber;
            this.karanaName = karanaName;
            this.sanskritName = sanskritName;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        // Getters
        public int getKaranaNumber() { return karanaNumber; }
        public String getKaranaName() { return karanaName; }
        public String getSanskritName() { return sanskritName; }
        public double getStartTime() { return startTime; }
        public double getEndTime() { return endTime; }
    }

    public static class MuhurtaInfo {
        private final String brahmaMuhurta;
        private final String abhijitMuhurta;
        private final String godhuliMuhurta;
        private final String rahuKaal;
        private final String gulikaKaal;
        private final String yamagandaKaal;

        public MuhurtaInfo(String brahmaMuhurta, String abhijitMuhurta, String godhuliMuhurta,
                          String rahuKaal, String gulikaKaal, String yamagandaKaal) {
            this.brahmaMuhurta = brahmaMuhurta;
            this.abhijitMuhurta = abhijitMuhurta;
            this.godhuliMuhurta = godhuliMuhurta;
            this.rahuKaal = rahuKaal;
            this.gulikaKaal = gulikaKaal;
            this.yamagandaKaal = yamagandaKaal;
        }

        // Getters
        public String getBrahmaMuhurta() { return brahmaMuhurta; }
        public String getAbhijitMuhurta() { return abhijitMuhurta; }
        public String getGodhuliMuhurta() { return godhuliMuhurta; }
        public String getRahuKaal() { return rahuKaal; }
        public String getGulikaKaal() { return gulikaKaal; }
        public String getYamagandaKaal() { return yamagandaKaal; }
    }
} 