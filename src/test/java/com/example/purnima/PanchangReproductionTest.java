package com.example.purnima;

import com.example.purnima.model.PanchangResult;
import com.example.purnima.service.DefaultPanchangCalculator;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class PanchangReproductionTest {

    @Test
    public void testReproduction() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");

        DefaultPanchangCalculator calculator = new DefaultPanchangCalculator(messageSource);

        LocalDate date = LocalDate.of(2025, 12, 3);
        
        // User provided coordinates (looks like Leh/Ladakh, not Hyderabad)
        double userLat = 34.4065;
        double userLon = 78.4772;
        String userPlace = "UserCoords";

        // Standard Hyderabad coordinates
        double hydLat = 17.3850;
        double hydLon = 78.4867;
        String hydPlace = "Hyderabad";

        // Check Ayanamsa
        double ayanamsa = com.example.purnima.util.SwissEphCalculator.getAyanamsa(date.atStartOfDay());
        System.out.println("Ayanamsa for " + date + ": " + ayanamsa);

        System.out.println("--- Calculation for User Coords (" + userLat + ", " + userLon + ") ---");
        printPanchang(calculator, date, userLat, userLon, userPlace);

        System.out.println("\n--- Calculation for Hyderabad Coords (" + hydLat + ", " + hydLon + ") ---");
        printPanchang(calculator, date, hydLat, hydLon, hydPlace);
    }

    private void printPanchang(DefaultPanchangCalculator calculator, LocalDate date, double lat, double lon, String place) {
        PanchangResult result = calculator.calculatePanchang(date, lat, lon, place, java.time.ZoneId.of("Asia/Kolkata"));
        System.out.println("Tithi: " + result.getTithi().getTithiName() + " ends at " + result.getTithi().getEndTime());
        System.out.println("Nakshatra: " + result.getNakshatra().getNakshatraName() + " ends at " + result.getNakshatra().getEndTime());
        System.out.println("Yoga: " + result.getYoga().getYogaName() + " ends at " + result.getYoga().getEndTime());
        System.out.println("Karana: " + result.getKarana().getKaranaName() + " ends at " + result.getKarana().getEndTime());
    }
}
