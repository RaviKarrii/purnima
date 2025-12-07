package com.example.purnima;

import com.example.purnima.model.PanchangResult;
import com.example.purnima.service.DefaultPanchangCalculator;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class NakshatraReproductionTest {

    @Test
    public void testNakshatraForDec7_2025() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");

        DefaultPanchangCalculator calculator = new DefaultPanchangCalculator(messageSource);

        LocalDate date = LocalDate.of(2025, 12, 7);
        double lat = 28.6139; // New Delhi
        double lon = 77.2090;
        String place = "New Delhi";
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");

        // Calculate Panchang
        PanchangResult result = calculator.calculatePanchang(date, lat, lon, place, zoneId);

        System.out.println("Date: " + date);
        System.out.println("Place: " + place);
        
        List<PanchangResult.NakshatraInfo> nakList = result.getNakshatra();
        System.out.println("Nakshatras for the day (" + nakList.size() + "):");
        for (PanchangResult.NakshatraInfo nak : nakList) {
            System.out.println("  " + nak.getNakshatraName() + " (" + nak.getNakshatraNumber() + ") " + nak.getStartTime() + " - " + nak.getEndTime());
        }

        List<PanchangResult.TithiInfo> tithiList = result.getTithi();
        System.out.println("Tithis for the day (" + tithiList.size() + "):");
        for (PanchangResult.TithiInfo t : tithiList) {
            System.out.println("  " + t.getTithiName() + " (" + t.getTithiNumber() + ") " + t.getStartTime() + " - " + t.getEndTime());
        }
        
        // Assertions
        assert(nakList.size() >= 1);
        assert(tithiList.size() >= 1);
    }
}
