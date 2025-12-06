package com.example.purnima;

import com.example.purnima.model.PanchangResult;
import com.example.purnima.service.DefaultPanchangCalculator;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class PanchangRiseSetTest {

    @Test
    public void testRiseSetTimes() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");

        DefaultPanchangCalculator calculator = new DefaultPanchangCalculator(messageSource);

        LocalDate date = LocalDate.of(2025, 12, 6);
        double lat = 17.3850; // Hyderabad
        double lon = 78.4867;
        String place = "Hyderabad";

        PanchangResult result = calculator.calculatePanchang(date, lat, lon, place, java.time.ZoneId.of("Asia/Kolkata"));

        System.out.println("Sunrise: " + result.getSunrise());
        System.out.println("Sunset: " + result.getSunset());
        System.out.println("Moonrise: " + result.getMoonrise());
        System.out.println("Moonset: " + result.getMoonset());

        assertNotNull(result.getSunrise(), "Sunrise should not be null");
        assertNotNull(result.getSunset(), "Sunset should not be null");
        // Moonrise/Moonset can be null on some days, but likely not today. 
        // Let's just check they are formatted correctly if present.
        
        if (result.getSunrise() != null) {
            assertTrue(result.getSunrise().matches("\\d{2}:\\d{2}"), "Sunrise format should be HH:mm");
        }
        if (result.getSunset() != null) {
            assertTrue(result.getSunset().matches("\\d{2}:\\d{2}"), "Sunset format should be HH:mm");
        }
        if (result.getMoonrise() != null) {
            assertTrue(result.getMoonrise().matches("\\d{2}:\\d{2}"), "Moonrise format should be HH:mm");
        }
        if (result.getMoonset() != null) {
            assertTrue(result.getMoonset().matches("\\d{2}:\\d{2}"), "Moonset format should be HH:mm");
        }
    }
}
