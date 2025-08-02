package com.example.purnima.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Represents birth data required for Vedic astrology calculations.
 * Contains date, time, and location information.
 */
public class BirthData {
    private LocalDateTime birthDateTime;
    private double latitude;
    private double longitude;
    private String placeName;
    private ZoneId timeZone;

    public BirthData() {
        // Default constructor
    }

    public BirthData(LocalDateTime birthDateTime, double latitude, double longitude, String placeName) {
        this.birthDateTime = birthDateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
        this.timeZone = ZoneId.systemDefault();
    }

    public BirthData(LocalDateTime birthDateTime, double latitude, double longitude, String placeName, ZoneId timeZone) {
        this.birthDateTime = birthDateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
        this.timeZone = timeZone;
    }

    // Getters and Setters
    public LocalDateTime getBirthDateTime() {
        return birthDateTime;
    }

    public void setBirthDateTime(LocalDateTime birthDateTime) {
        this.birthDateTime = birthDateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BirthData birthData = (BirthData) o;
        return Double.compare(birthData.latitude, latitude) == 0 &&
                Double.compare(birthData.longitude, longitude) == 0 &&
                Objects.equals(birthDateTime, birthData.birthDateTime) &&
                Objects.equals(placeName, birthData.placeName) &&
                Objects.equals(timeZone, birthData.timeZone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthDateTime, latitude, longitude, placeName, timeZone);
    }

    @Override
    public String toString() {
        return "BirthData{" +
                "birthDateTime=" + birthDateTime +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", placeName='" + placeName + '\'' +
                ", timeZone=" + timeZone +
                '}';
    }
} 