package com.example.purnima;

import com.example.purnima.model.BirthData;
import com.example.purnima.model.PanchangResult;
import com.example.purnima.api.ChartGenerator;
import com.example.purnima.service.AccurateChartGenerator;
import com.example.purnima.service.DefaultPanchangCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class I18nTest {

    @Test
    public void testI18n() {
        // Setup MessageSource
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");

        // Setup Services
        DefaultPanchangCalculator panchangCalculator = new DefaultPanchangCalculator(messageSource);
        AccurateChartGenerator chartGenerator = new AccurateChartGenerator(messageSource);

        // Test Data
        LocalDateTime dateTime = LocalDateTime.now();
        double lat = 28.6139; // New Delhi
        double lon = 77.2090;
        String place = "New Delhi";
        BirthData birthData = new BirthData(dateTime, lat, lon, place);

        // Test Locales
        Locale[] locales = {
            Locale.ENGLISH, 
            new Locale("hi"), 
            new Locale("te"),
            new Locale("sa"),
            new Locale("ta"),
            new Locale("kn")
        };

        for (Locale locale : locales) {
            System.out.println("\nTesting Locale: " + locale.getDisplayName());
            System.out.println("==================================");
            
            LocaleContextHolder.setLocale(locale);

            // Test Panchang
            PanchangResult panchang = panchangCalculator.calculatePanchang(dateTime, lat, lon, place);
            assertNotNull(panchang);
            
            System.out.println("Panchang Summary:");
            System.out.println("Tithi: " + panchang.getTithi().get(0).getTithiName());
            System.out.println("Vara: " + panchang.getVara().getVaraName());
            System.out.println("Nakshatra: " + panchang.getNakshatra().get(0).getNakshatraName());
            System.out.println("Yoga: " + panchang.getYoga().get(0).getYogaName());
            System.out.println("Karana: " + panchang.getKarana().get(0).getKaranaName());

            // Basic assertions to ensure we are getting some output
            assertNotNull(panchang.getTithi().get(0).getTithiName());
            assertNotNull(panchang.getVara().getVaraName());

            // Test Chart
            String chartSummary = chartGenerator.generateChartInFormat(chartGenerator.generateBirthChart(birthData), ChartGenerator.ChartFormat.TEXT);
            assertNotNull(chartSummary);
            
            System.out.println("\nChart Summary Snippet:");
            String[] lines = chartSummary.split("\n");
            for (int i = 0; i < Math.min(15, lines.length); i++) {
                System.out.println(lines[i]);
            }
        }
    }
}
