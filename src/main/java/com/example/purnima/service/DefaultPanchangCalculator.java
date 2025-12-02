package com.example.purnima.service;

import com.example.purnima.api.PanchangCalculator;
import com.example.purnima.model.BirthData;
import com.example.purnima.model.PanchangResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import com.example.purnima.model.PanchangResult.*;


/**
 * Default implementation of PanchangCalculator.
 * Provides basic Panchang calculations based on Vedic astrology principles.
 */
public class DefaultPanchangCalculator implements PanchangCalculator {
    
    @Override
    public PanchangResult calculatePanchang(LocalDate date, double latitude, double longitude, String placeName) {
        return calculatePanchang(date.atStartOfDay(), latitude, longitude, placeName);
    }

    @Override
    public PanchangResult calculatePanchang(LocalDateTime dateTime, double latitude, double longitude, String placeName) {
        TithiInfo tithi = calculateTithi(dateTime, latitude, longitude);
        VaraInfo vara = calculateVara(dateTime.toLocalDate());
        NakshatraInfo nakshatra = calculateNakshatra(dateTime, latitude, longitude);
        YogaInfo yoga = calculateYoga(dateTime, latitude, longitude);
        KaranaInfo karana = calculateKarana(dateTime, latitude, longitude);
        MuhurtaInfo muhurta = getAuspiciousTimings(dateTime.toLocalDate(), latitude, longitude);
        
        return new PanchangResult(dateTime, latitude, longitude, placeName,
                                tithi, vara, nakshatra, yoga, karana, muhurta);
    }

    @Override
    public PanchangResult calculatePanchang(BirthData birthData) {
        return calculatePanchang(birthData.getBirthDateTime(), 
                               birthData.getLatitude(), 
                               birthData.getLongitude(),
                               birthData.getPlaceName());
    }

    public PanchangResult calculatePanchang(LocalDate date, double latitude, double longitude) {
        return calculatePanchang(date, latitude, longitude, "Unknown Place");
    }

    public PanchangResult calculatePanchang(LocalDateTime dateTime, double latitude, double longitude) {
        return calculatePanchang(dateTime, latitude, longitude, "Unknown Place");
    }

    @Override
    public TithiInfo calculateTithi(LocalDateTime dateTime, double latitude, double longitude) {
        // Use SwissEph for accurate Tithi
        com.example.purnima.util.SwissEphCalculator.LunarPhase currentTithi = com.example.purnima.util.SwissEphCalculator.calculateLunarPhase(dateTime, latitude, longitude);
        
        // Calculate end time
        LocalDateTime endTime = findTithiEndTime(dateTime, latitude, longitude, currentTithi.getTithi());
        double endTimeDecimal = toDecimalTime(endTime);
        
        // Map Tithi number to name
        int tithiNumber = currentTithi.getTithi();
        String[] tithiNames = {
            "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
            "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
            "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima",
            "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
            "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
            "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Amavasya"
        };
        
        String[] sanskritNames = {
            "प्रतिपदा", "द्वितीया", "तृतीया", "चतुर्थी", "पञ्चमी",
            "षष्ठी", "सप्तमी", "अष्टमी", "नवमी", "दशमी",
            "एकादशी", "द्वादशी", "त्रयोदशी", "चतुर्दशी", "पूर्णिमा",
            "प्रतिपदा", "द्वितीया", "तृतीया", "चतुर्थी", "पञ्चमी",
            "षष्ठी", "सप्तमी", "अष्टमी", "नवमी", "दशमी",
            "एकादशी", "द्वादशी", "त्रयोदशी", "चतुर्दशी", "अमावस्या"
        };
        
        String tithiName = tithiNames[tithiNumber - 1];
        String sanskritName = sanskritNames[tithiNumber - 1];
        
        return new TithiInfo(tithiNumber, tithiName, sanskritName,
                           0.0, endTimeDecimal, currentTithi.isShuklaPaksha());
    }
    
    @Override
    public VaraInfo calculateVara(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        
        String[] varaNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] sanskritNames = {"रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार"};
        String[] rulingPlanets = {"Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"};
        
        int dayIndex = dayOfWeek.getValue() % 7;
        
        return new PanchangResult.VaraInfo(dayIndex + 1, varaNames[dayIndex], sanskritNames[dayIndex], rulingPlanets[dayIndex]);
    }
    
    @Override
    public NakshatraInfo calculateNakshatra(LocalDateTime dateTime, double latitude, double longitude) {
        // Use SwissEph for accurate Nakshatra
        com.example.purnima.util.SwissEphCalculator.NakshatraInfo currentNak = com.example.purnima.util.SwissEphCalculator.calculateNakshatra(dateTime, latitude, longitude);
        
        // Calculate end time
        LocalDateTime endTime = findNakshatraEndTime(dateTime, latitude, longitude, currentNak.getNakshatraNumber());
        double endTimeDecimal = toDecimalTime(endTime);
        
        int nakshatraNumber = currentNak.getNakshatraNumber();
        
        String[] nakshatraNames = {
            "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra", "Punarvasu",
            "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta",
            "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha", "Mula",
            "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha",
            "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
        };
        
        String[] sanskritNames = {
            "अश्विनी", "भरणी", "कृत्तिका", "रोहिणी", "मृगशिरा", "आर्द्रा", "पुनर्वसु",
            "पुष्य", "आश्लेषा", "मघा", "पूर्व फाल्गुनी", "उत्तर फाल्गुनी", "हस्त",
            "चित्रा", "स्वाति", "विशाखा", "अनुराधा", "ज्येष्ठा", "मूल",
            "पूर्वाषाढा", "उत्तराषाढा", "श्रवण", "धनिष्ठा", "शतभिषा",
            "पूर्व भाद्रपद", "उत्तर भाद्रपद", "रेवती"
        };
        
        String[] rulingPlanets = {
            "Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter",
            "Saturn", "Mercury", "Ketu", "Venus", "Sun", "Moon",
            "Mars", "Rahu", "Jupiter", "Saturn", "Mercury", "Ketu",
            "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter",
            "Saturn", "Mercury"
        };
        
        return new NakshatraInfo(nakshatraNumber, nakshatraNames[nakshatraNumber - 1], sanskritNames[nakshatraNumber - 1],
                               rulingPlanets[nakshatraNumber - 1], 0.0, endTimeDecimal);
    }
    
    @Override
    public YogaInfo calculateYoga(LocalDateTime dateTime, double latitude, double longitude) {
        // Use SwissEph for accurate Yoga
        com.example.purnima.util.SwissEphCalculator.YogaInfo currentYoga = com.example.purnima.util.SwissEphCalculator.calculateYoga(dateTime, latitude, longitude);
            
        // Calculate end time
        LocalDateTime endTime = findYogaEndTime(dateTime, latitude, longitude, currentYoga.getYogaNumber());
        double endTimeDecimal = toDecimalTime(endTime);
        
        int yogaNumber = currentYoga.getYogaNumber();
        
        String[] yogaNames = {
            "Vishkumbha", "Priti", "Ayushman", "Saubhagya", "Shobhana", "Atiganda", "Sukarman",
            "Dhriti", "Shula", "Ganda", "Vriddhi", "Dhruva", "Vyaghata",
            "Harshana", "Vajra", "Siddhi", "Vyatipata", "Variyan", "Parigha",
            "Shiva", "Siddha", "Sadhya", "Shubha", "Shukla", "Brahma",
            "Indra", "Vaidhriti"
        };
        
        String[] sanskritNames = {
            "विष्कुम्भ", "प्रीति", "आयुष्मान्", "सौभाग्य", "शोभन", "अतिगण्ड", "सुकर्मन्",
            "धृति", "शूल", "गण्ड", "वृद्धि", "ध्रुव", "व्याघात",
            "हर्षण", "वज्र", "सिद्धि", "व्यतिपात", "वरीयान्", "परिघ",
            "शिव", "सिद्ध", "साध्य", "शुभ", "शुक्ल", "ब्रह्म",
            "इन्द्र", "वैधृति"
        };
        
        return new YogaInfo(yogaNumber, yogaNames[yogaNumber - 1], sanskritNames[yogaNumber - 1],
                          0.0, endTimeDecimal);
    }
    
    @Override
    public KaranaInfo calculateKarana(LocalDateTime dateTime, double latitude, double longitude) {
        // Use SwissEph for accurate Karana
        com.example.purnima.util.SwissEphCalculator.KaranaInfo currentKarana = 
            com.example.purnima.util.SwissEphCalculator.calculateKarana(dateTime, latitude, longitude);
            
        // Calculate end time
        LocalDateTime endTime = findKaranaEndTime(dateTime, latitude, longitude, currentKarana.getKaranaNumber());
        double endTimeDecimal = toDecimalTime(endTime);
        
        int karanaNum = currentKarana.getKaranaNumber();
        String karanaName = getKaranaName(karanaNum);
        String sanskritName = getKaranaSanskritName(karanaNum);
        
        return new KaranaInfo(karanaNum, karanaName, sanskritName, 0.0, endTimeDecimal);
    }
    
    @Override
    public MuhurtaInfo getAuspiciousTimings(LocalDate date, double latitude, double longitude) {
        // Simplified Muhurta calculation
        // In actual implementation, this would use astronomical calculations
        
        String brahmaMuhurta = "04:30 - 05:30";
        String abhijitMuhurta = "11:45 - 12:30";
        String godhuliMuhurta = "18:00 - 18:30";
        String rahuKaal = "16:30 - 18:00";
        String gulikaKaal = "14:30 - 16:00";
        String yamagandaKaal = "12:00 - 13:30";
        
        return new PanchangResult.MuhurtaInfo(brahmaMuhurta, abhijitMuhurta, godhuliMuhurta,
                              rahuKaal, gulikaKaal, yamagandaKaal);
    }

    // Helper methods for Karana names
    private String getKaranaName(int karanaNumber) {
        String[] movableKaranas = {"Bava", "Balava", "Kaulava", "Taitila", "Garija", "Vanija", "Vishti"};
        String[] fixedKaranas = {"Shakuni", "Chatushpada", "Naga", "Kimstughna"};
        
        if (karanaNumber == 1) return fixedKaranas[3]; // Kimstughna
        if (karanaNumber >= 58) {
            if (karanaNumber == 58) return fixedKaranas[0]; // Shakuni
            if (karanaNumber == 59) return fixedKaranas[1]; // Chatushpada
            if (karanaNumber == 60) return fixedKaranas[2]; // Naga
        }
        
        if (karanaNumber >= 2 && karanaNumber <= 57) {
            return movableKaranas[(karanaNumber - 2) % 7];
        }
        
        return "Unknown";
    }
    
    private String getKaranaSanskritName(int karanaNumber) {
        String[] movableKaranas = {"बव", "बालव", "कौलव", "तैतिल", "गरिज", "वणिज", "विष्टि"};
        String[] fixedKaranas = {"शकुनि", "चतुष्पाद", "नाग", "किंस्तुघ्न"};
        
        if (karanaNumber == 1) return fixedKaranas[3];
        if (karanaNumber >= 58) {
            if (karanaNumber == 58) return fixedKaranas[0];
            if (karanaNumber == 59) return fixedKaranas[1];
            if (karanaNumber == 60) return fixedKaranas[2];
        }
        
        if (karanaNumber >= 2 && karanaNumber <= 57) {
            return movableKaranas[(karanaNumber - 2) % 7];
        }
        
        return "अज्ञात";
    }
    
    // Helper methods for end time calculation
    
    private LocalDateTime findTithiEndTime(LocalDateTime start, double lat, double lon, int currentTithi) {
        return findEndTime(start, lat, lon, time -> com.example.purnima.util.SwissEphCalculator.calculateLunarPhase(time, lat, lon).getTithi(), currentTithi);
    }
    
    private LocalDateTime findNakshatraEndTime(LocalDateTime start, double lat, double lon, int currentNak) {
        return findEndTime(start, lat, lon, time -> com.example.purnima.util.SwissEphCalculator.calculateNakshatra(time, lat, lon).getNakshatraNumber(), currentNak);
    }
    
    private LocalDateTime findYogaEndTime(LocalDateTime start, double lat, double lon, int currentYoga) {
        return findEndTime(start, lat, lon, time -> com.example.purnima.util.SwissEphCalculator.calculateYoga(time, lat, lon).getYogaNumber(), currentYoga);
    }
    
    private LocalDateTime findKaranaEndTime(LocalDateTime start, double lat, double lon, int currentKarana) {
        return findEndTime(start, lat, lon, time -> com.example.purnima.util.SwissEphCalculator.calculateKarana(time, lat, lon).getKaranaNumber(), currentKarana);
    }

    private LocalDateTime findEndTime(LocalDateTime start, double lat, double lon, java.util.function.Function<LocalDateTime, Integer> valueProvider, int startValue) {
        LocalDateTime current = start;
        // Check every hour for 30 hours
        for (int i = 1; i <= 30; i++) {
            LocalDateTime next = start.plusHours(i);
            int nextValue = valueProvider.apply(next);
            if (nextValue != startValue) {
                // Change happened between (next-1h) and next.
                // Binary search for minute precision
                LocalDateTime low = next.minusHours(1);
                LocalDateTime high = next;
                for (int j = 0; j < 10; j++) {
                    long seconds = java.time.Duration.between(low, high).getSeconds();
                    if (seconds < 60) break;
                    LocalDateTime mid = low.plusSeconds(seconds / 2);
                    if (valueProvider.apply(mid) == startValue) {
                        low = mid;
                    } else {
                        high = mid;
                    }
                }
                return high;
            }
        }
        return start.plusHours(24); // Fallback
    }

    private double toDecimalTime(LocalDateTime time) {
        return time.getHour() + (time.getMinute() / 60.0);
    }
} 