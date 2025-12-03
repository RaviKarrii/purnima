package com.example.purnima;

import com.example.purnima.model.MuhurtaResult;
import com.example.purnima.service.DefaultMuhurtaCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class MuhurtaTest {

    @Test
    public void testMuhurtaCalculation() {
        DefaultMuhurtaCalculator calculator = new DefaultMuhurtaCalculator();
        
        LocalDate date = LocalDate.of(2024, 1, 15);
        double lat = 19.0760; // Mumbai
        double lon = 72.8777;
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        
        MuhurtaResult result = calculator.calculateMuhurta(date, lat, lon, zoneId);
        
        assertNotNull(result);
        assertNotNull(result.getDayChoghadiya());
        assertEquals(8, result.getDayChoghadiya().size());
        
        System.out.println("Day Choghadiya for " + date);
        for (MuhurtaResult.Choghadiya c : result.getDayChoghadiya()) {
            System.out.println(c.getName() + " (" + c.getNature() + "): " + c.getStartTime().toLocalTime() + " - " + c.getEndTime().toLocalTime());
        }
        
        assertNotNull(result.getHoras());
        assertEquals(24, result.getHoras().size());
        
        assertNotNull(result.getInauspiciousTimes());
        assertNotNull(result.getInauspiciousTimes().getRahuKalam());
        System.out.println("Rahu Kalam: " + result.getInauspiciousTimes().getRahuKalam().getStartTime().toLocalTime() + " - " + result.getInauspiciousTimes().getRahuKalam().getEndTime().toLocalTime());
    }
}
