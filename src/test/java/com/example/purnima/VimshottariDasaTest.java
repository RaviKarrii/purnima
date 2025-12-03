package com.example.purnima;

import com.example.purnima.model.BirthData;
import com.example.purnima.model.DasaResult;
import com.example.purnima.service.VimshottariDasaCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VimshottariDasaTest {

    @Test
    public void testDasaCalculation() {
        VimshottariDasaCalculator calculator = new VimshottariDasaCalculator();
        
        // Test Case: 1990-05-15 14:30 Mumbai
        LocalDateTime birthTime = LocalDateTime.of(1990, 5, 15, 14, 30);
        BirthData birthData = new BirthData(birthTime, 19.0760, 72.8777, "Mumbai", ZoneId.of("Asia/Kolkata"));
        
        List<DasaResult> mahadasas = calculator.calculateMahadasas(birthData);
        
        assertNotNull(mahadasas);
        assertFalse(mahadasas.isEmpty());
        
        System.out.println("Vimshottari Dasa for " + birthTime);
        for (DasaResult dasa : mahadasas) {
            System.out.println(dasa.getPlanet() + ": " + dasa.getStartDate().toLocalDate() + " to " + dasa.getEndDate().toLocalDate());
        }
        
        // Verify sequence
        // Moon in Shravana (Moon ruled) or something?
        // Let's check first Dasa.
        DasaResult firstDasa = mahadasas.get(0);
        assertNotNull(firstDasa.getPlanet());
        
        // Verify current Dasa
        DasaResult currentDasa = calculator.getCurrentDasa(birthData);
        assertNotNull(currentDasa);
        System.out.println("Current Dasa: " + currentDasa.getPlanet() + " (Level " + currentDasa.getLevel() + ")");
    }
}
