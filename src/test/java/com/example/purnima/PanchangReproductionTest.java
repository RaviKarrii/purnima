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
        if (!result.getTithi().isEmpty()) {
            System.out.println("Tithi: " + result.getTithi().get(0).getTithiName() + " ends at " + result.getTithi().get(0).getEndTime());
        }
        if (!result.getNakshatra().isEmpty()) {
            System.out.println("Nakshatra: " + result.getNakshatra().get(0).getNakshatraName() + " ends at " + result.getNakshatra().get(0).getEndTime());
        }
        if (!result.getYoga().isEmpty()) {
            System.out.println("Yoga: " + result.getYoga().get(0).getYogaName() + " ends at " + result.getYoga().get(0).getEndTime());
        }
        if (!result.getKarana().isEmpty()) {
            System.out.println("Karana: " + result.getKarana().get(0).getKaranaName() + " ends at " + result.getKarana().get(0).getEndTime());
        }
    }
}
