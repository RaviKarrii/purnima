package com.example.purnima.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Dasa period (Mahadasa, Antardasa, etc.).
 */
public class DasaResult {
    private String planet;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int level; // 1=Mahadasa, 2=Antardasa, 3=Pratyantardasa
    private List<DasaResult> subDasas;
    private String significance; // Significance of this Dasa period

    public DasaResult() {}

    public DasaResult(String planet, LocalDateTime startDate, LocalDateTime endDate, int level) {
        this.planet = planet;
        this.startDate = startDate;
        this.endDate = endDate;
        this.level = level;
    }

    public String getPlanet() { return planet; }
    public void setPlanet(String planet) { this.planet = planet; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public List<DasaResult> getSubDasas() { return subDasas; }
    public void setSubDasas(List<DasaResult> subDasas) { this.subDasas = subDasas; }
    
    public String getSignificance() { return significance; }
    public void setSignificance(String significance) { this.significance = significance; }
}
