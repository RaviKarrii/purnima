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
    private int level; // 1=Mahadasa, 2=Antardasa, 3=Pratyantardasa, etc.
    private List<DasaResult> subPeriods;

    public DasaResult(String planet, LocalDateTime startDate, LocalDateTime endDate, int level) {
        this.planet = planet;
        this.startDate = startDate;
        this.endDate = endDate;
        this.level = level;
        this.subPeriods = new ArrayList<>();
    }

    public String getPlanet() {
        return planet;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public int getLevel() {
        return level;
    }

    public List<DasaResult> getSubPeriods() {
        return subPeriods;
    }

    public void setSubPeriods(List<DasaResult> subPeriods) {
        this.subPeriods = subPeriods;
    }
    
    public void addSubPeriod(DasaResult subPeriod) {
        this.subPeriods.add(subPeriod);
    }
}
