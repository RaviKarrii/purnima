package com.example.purnima.model;

import java.time.LocalDateTime;
import java.util.List;
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
    private final String sunrise;
    private final String sunset;
    private final String moonrise;
    private final String moonset;
    private final List<TithiInfo> tithi;
    private final VaraInfo vara;
    private final List<NakshatraInfo> nakshatra;
    private final List<YogaInfo> yoga;
    private final List<KaranaInfo> karana;
    private final MuhurtaInfo muhurta;

    public PanchangResult(LocalDateTime dateTime, double latitude, double longitude, String placeName,
                         String sunrise, String sunset, String moonrise, String moonset,
                         List<TithiInfo> tithi, VaraInfo vara, List<NakshatraInfo> nakshatra, 
                         List<YogaInfo> yoga, List<KaranaInfo> karana, MuhurtaInfo muhurta) {
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.moonrise = moonrise;
        this.moonset = moonset;
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

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getMoonrise() {
        return moonrise;
    }

    public String getMoonset() {
        return moonset;
    }

    public List<TithiInfo> getTithi() {
        return tithi;
    }

    public VaraInfo getVara() {
        return vara;
    }

    public List<NakshatraInfo> getNakshatra() {
        return nakshatra;
    }

    public List<YogaInfo> getYoga() {
        return yoga;
    }

    public List<KaranaInfo> getKarana() {
        return karana;
    }

    public MuhurtaInfo getMuhurta() {
        return muhurta;
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
                Objects.equals(sunrise, that.sunrise) &&
                Objects.equals(sunset, that.sunset) &&
                Objects.equals(moonrise, that.moonrise) &&
                Objects.equals(moonset, that.moonset) &&
                Objects.equals(tithi, that.tithi) &&
                Objects.equals(vara, that.vara) &&
                Objects.equals(nakshatra, that.nakshatra) &&
                Objects.equals(yoga, that.yoga) &&
                Objects.equals(karana, that.karana) &&
                Objects.equals(muhurta, that.muhurta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, latitude, longitude, placeName, sunrise, sunset, moonrise, moonset, tithi, vara, nakshatra, yoga, karana, muhurta);
    }

    @Override
    public String toString() {
        return "PanchangResult{" +
                "dateTime=" + dateTime +
                ", placeName='" + placeName + '\'' +
                ", sunrise='" + sunrise + '\'' +
                ", sunset='" + sunset + '\'' +
                ", tithi=" + tithi +
                ", vara=" + vara.getVaraName() +
                ", nakshatra=" + nakshatra +
                '}';
    }

    /**
     * Inner classes for Panchang elements (moved from interface for better organization)
     */
    public static class TithiInfo {
        private final int tithiNumber;
        private final String tithiName;
        private final String sanskritName;
        private final String startTime;
        private final String endTime;
        private final boolean isShuklaPaksha;

        public TithiInfo(int tithiNumber, String tithiName, String sanskritName, 
                        String startTime, String endTime, boolean isShuklaPaksha) {
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
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
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
        private final String startTime;
        private final String endTime;

        public NakshatraInfo(int nakshatraNumber, String nakshatraName, String sanskritName,
                           String rulingPlanet, String startTime, String endTime) {
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
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
    }

    public static class YogaInfo {
        private final int yogaNumber;
        private final String yogaName;
        private final String sanskritName;
        private final String startTime;
        private final String endTime;

        public YogaInfo(int yogaNumber, String yogaName, String sanskritName,
                       String startTime, String endTime) {
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
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
    }

    public static class KaranaInfo {
        private final int karanaNumber;
        private final String karanaName;
        private final String sanskritName;
        private final String startTime;
        private final String endTime;

        public KaranaInfo(int karanaNumber, String karanaName, String sanskritName,
                         String startTime, String endTime) {
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
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
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