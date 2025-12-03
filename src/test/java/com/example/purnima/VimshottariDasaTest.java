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
    @Test
    public void testCalculateMahadasasWithSignificance() {
        // Setup MessageSource
        org.springframework.context.support.ReloadableResourceBundleMessageSource messageSource = new org.springframework.context.support.ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        
        VimshottariDasaCalculator calculator = new VimshottariDasaCalculator(messageSource);
        LocalDateTime birthTime = LocalDateTime.of(1990, 1, 1, 10, 0);
        BirthData birthData = new BirthData(birthTime, 17.385, 78.4867, "Hyderabad", ZoneId.of("Asia/Kolkata"));
        
        List<DasaResult> dasas = calculator.calculateMahadasas(birthData, true);
        
        assertNotNull(dasas);
        assertFalse(dasas.isEmpty());
        
        // Check first Dasa
        DasaResult firstDasa = dasas.get(0);
        System.out.println("First Dasa Planet: " + firstDasa.getPlanet());
        System.out.println("First Dasa Significance: " + firstDasa.getSignificance());
        
        assertNotNull(firstDasa.getSignificance(), "Significance should not be null");
        assertNotEquals("Significance not available", firstDasa.getSignificance(), "Significance should be localized");
        
        // Check sub-dasas
        if (firstDasa.getSubDasas() != null && !firstDasa.getSubDasas().isEmpty()) {
            DasaResult subDasa = firstDasa.getSubDasas().get(0);
            assertNotNull(subDasa.getSignificance(), "Significance should not be null for sub-dasa");
        }
    }
}
