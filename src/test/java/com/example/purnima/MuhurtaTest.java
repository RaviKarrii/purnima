package com.example.purnima;

import com.example.purnima.model.MuhurtaResult;
import com.example.purnima.service.DefaultMuhurtaCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import com.example.purnima.model.MuhurtaSlot;
import java.time.LocalDateTime;
import java.util.List;
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
    private org.springframework.context.MessageSource createMessageSource() {
        org.springframework.context.support.ReloadableResourceBundleMessageSource messageSource = new org.springframework.context.support.ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Test
    public void testVehicleMuhurta() {
        DefaultMuhurtaCalculator calculator = new DefaultMuhurtaCalculator(createMessageSource());
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = start.plusDays(30); // Check for a month
        double lat = 17.3850; // Hyderabad
        double lon = 78.4867;
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        
        List<MuhurtaSlot> slots = calculator.findVehiclePurchaseMuhurta(start, end, lat, lon, zoneId);
        
        assertNotNull(slots);
        System.out.println("Vehicle Muhurta Slots found: " + slots.size());
        for (MuhurtaSlot slot : slots) {
            System.out.println("Slot: " + slot.getStartTime() + " - " + slot.getEndTime() + " (" + slot.getQuality() + ")");
            System.out.println("Factors: " + slot.getPositiveFactors());
        }
    }

    @Test
    public void testMarriageMuhurta() {
        DefaultMuhurtaCalculator calculator = new DefaultMuhurtaCalculator(createMessageSource());
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = start.plusDays(30);
        double lat = 17.3850;
        double lon = 78.4867;
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        
        List<MuhurtaSlot> slots = calculator.findMarriageMuhurta(start, end, lat, lon, zoneId);
        
        assertNotNull(slots);
        System.out.println("Marriage Muhurta Slots found: " + slots.size());
        for (MuhurtaSlot slot : slots) {
            System.out.println("Slot: " + slot.getStartTime() + " - " + slot.getEndTime() + " (" + slot.getQuality() + ")");
            System.out.println("Factors: " + slot.getPositiveFactors());
        }
    }
    @Test
    public void testGrihaPraveshMuhurta() {
        DefaultMuhurtaCalculator calculator = new DefaultMuhurtaCalculator(createMessageSource());
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = start.plusDays(60); // Check for 2 months
        double lat = 17.3850;
        double lon = 78.4867;
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        
        List<MuhurtaSlot> slots = calculator.findGrihaPraveshMuhurta(start, end, lat, lon, zoneId);
        
        assertNotNull(slots);
        System.out.println("Griha Pravesh Muhurta Slots found: " + slots.size());
        for (MuhurtaSlot slot : slots) {
            System.out.println("Slot: " + slot.getStartTime() + " - " + slot.getEndTime() + " (" + slot.getQuality() + ")");
            System.out.println("Factors: " + slot.getPositiveFactors());
        }
    }

    @Test
    public void testNewBusinessMuhurta() {
        DefaultMuhurtaCalculator calculator = new DefaultMuhurtaCalculator(createMessageSource());
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = start.plusDays(30);
        double lat = 17.3850;
        double lon = 78.4867;
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        
        List<MuhurtaSlot> slots = calculator.findNewBusinessMuhurta(start, end, lat, lon, zoneId);
        
        assertNotNull(slots);
        System.out.println("New Business Muhurta Slots found: " + slots.size());
        for (MuhurtaSlot slot : slots) {
            System.out.println("Slot: " + slot.getStartTime() + " - " + slot.getEndTime() + " (" + slot.getQuality() + ")");
            System.out.println("Factors: " + slot.getPositiveFactors());
        }
    }

    @Test
    public void testNamakaranaMuhurta() {
        DefaultMuhurtaCalculator calculator = new DefaultMuhurtaCalculator(createMessageSource());
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = start.plusDays(30);
        double lat = 17.3850;
        double lon = 78.4867;
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        
        List<MuhurtaSlot> slots = calculator.findNamakaranaMuhurta(start, end, lat, lon, zoneId);
        
        assertNotNull(slots);
        System.out.println("Namakarana Muhurta Slots found: " + slots.size());
        for (MuhurtaSlot slot : slots) {
            System.out.println("Slot: " + slot.getStartTime() + " - " + slot.getEndTime() + " (" + slot.getQuality() + ")");
            System.out.println("Factors: " + slot.getPositiveFactors());
        }
    }

    @Test
    public void testPropertyPurchaseMuhurta() {
        DefaultMuhurtaCalculator calculator = new DefaultMuhurtaCalculator(createMessageSource());
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = start.plusDays(30);
        double lat = 17.3850;
        double lon = 78.4867;
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        
        List<MuhurtaSlot> slots = calculator.findPropertyPurchaseMuhurta(start, end, lat, lon, zoneId);
        
        assertNotNull(slots);
        System.out.println("Property Purchase Muhurta Slots found: " + slots.size());
        for (MuhurtaSlot slot : slots) {
            System.out.println("Slot: " + slot.getStartTime() + " - " + slot.getEndTime() + " (" + slot.getQuality() + ")");
            System.out.println("Factors: " + slot.getPositiveFactors());
        }
    }
}
