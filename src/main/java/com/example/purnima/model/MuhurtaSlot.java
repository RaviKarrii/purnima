package com.example.purnima.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a suitable time slot for a specific Muhurta.
 */
public class MuhurtaSlot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String quality; // e.g., "Best", "Good", "Average"
    private String color;
    private List<String> positiveFactors; // e.g., "Auspicious Nakshatra", "Good Tithi"
    private List<String> negativeFactors; // e.g., "Rahu Kalam overlap" (if allowed with caution)

    public MuhurtaSlot() {}

    public MuhurtaSlot(LocalDateTime startTime, LocalDateTime endTime, String quality) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
    }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public List<String> getPositiveFactors() { return positiveFactors; }
    public void setPositiveFactors(List<String> positiveFactors) { this.positiveFactors = positiveFactors; }

    public List<String> getNegativeFactors() { return negativeFactors; }
    public void setNegativeFactors(List<String> negativeFactors) { this.negativeFactors = negativeFactors; }
}
