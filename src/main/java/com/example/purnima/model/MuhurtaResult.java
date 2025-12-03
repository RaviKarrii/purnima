package com.example.purnima.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents Muhurta information (Choghadiya, Hora, etc.).
 */
public class MuhurtaResult {
    
    private List<Choghadiya> dayChoghadiya;
    private List<Choghadiya> nightChoghadiya;
    private List<Hora> horas;
    private InauspiciousTimes inauspiciousTimes;
    
    public MuhurtaResult() {}

    public List<Choghadiya> getDayChoghadiya() {
        return dayChoghadiya;
    }

    public void setDayChoghadiya(List<Choghadiya> dayChoghadiya) {
        this.dayChoghadiya = dayChoghadiya;
    }

    public List<Choghadiya> getNightChoghadiya() {
        return nightChoghadiya;
    }

    public void setNightChoghadiya(List<Choghadiya> nightChoghadiya) {
        this.nightChoghadiya = nightChoghadiya;
    }

    public List<Hora> getHoras() {
        return horas;
    }

    public void setHoras(List<Hora> horas) {
        this.horas = horas;
    }

    public InauspiciousTimes getInauspiciousTimes() {
        return inauspiciousTimes;
    }

    public void setInauspiciousTimes(InauspiciousTimes inauspiciousTimes) {
        this.inauspiciousTimes = inauspiciousTimes;
    }

    public static class Choghadiya {
        private String name;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String nature; // Good, Bad, Neutral

        public Choghadiya(String name, LocalDateTime startTime, LocalDateTime endTime, String nature) {
            this.name = name;
            this.startTime = startTime;
            this.endTime = endTime;
            this.nature = nature;
        }
        
        public String getName() { return name; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public String getNature() { return nature; }
    }
    
    public static class Hora {
        private String planet;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        
        public Hora(String planet, LocalDateTime startTime, LocalDateTime endTime) {
            this.planet = planet;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public String getPlanet() { return planet; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
    }
    
    public static class InauspiciousTimes {
        private Period rahuKalam;
        private Period yamagandam;
        private Period gulikaKalam;
        
        public InauspiciousTimes(Period rahuKalam, Period yamagandam, Period gulikaKalam) {
            this.rahuKalam = rahuKalam;
            this.yamagandam = yamagandam;
            this.gulikaKalam = gulikaKalam;
        }
        
        public Period getRahuKalam() { return rahuKalam; }
        public Period getYamagandam() { return yamagandam; }
        public Period getGulikaKalam() { return gulikaKalam; }
    }
    
    public static class Period {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        
        public Period(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
    }
}
