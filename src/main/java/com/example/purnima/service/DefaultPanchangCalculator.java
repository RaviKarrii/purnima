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
    public PanchangResult calculatePanchang(LocalDate date, double latitude, double longitude) {
        LocalDateTime dateTime = date.atStartOfDay();
        return calculatePanchang(dateTime, latitude, longitude);
    }
    
    @Override
    public PanchangResult calculatePanchang(LocalDateTime dateTime, double latitude, double longitude) {
        TithiInfo tithi = calculateTithi(dateTime, latitude, longitude);
        VaraInfo vara = calculateVara(dateTime.toLocalDate());
        NakshatraInfo nakshatra = calculateNakshatra(dateTime, latitude, longitude);
        YogaInfo yoga = calculateYoga(dateTime, latitude, longitude);
        KaranaInfo karana = calculateKarana(dateTime, latitude, longitude);
        MuhurtaInfo muhurta = getAuspiciousTimings(dateTime.toLocalDate(), latitude, longitude);
        
        return new PanchangResult(dateTime, latitude, longitude, "Unknown Place", 
                                tithi, vara, nakshatra, yoga, karana, muhurta);
    }
    
    @Override
    public PanchangResult calculatePanchang(BirthData birthData) {
        return calculatePanchang(birthData.getBirthDateTime(), 
                               birthData.getLatitude(), 
                               birthData.getLongitude());
    }
    
    @Override
    public TithiInfo calculateTithi(LocalDateTime dateTime, double latitude, double longitude) {
        // Simplified Tithi calculation
        // In actual implementation, this would use astronomical calculations
        
        int dayOfYear = dateTime.getDayOfYear();
        int tithiNumber = ((dayOfYear - 1) % 30) + 1;
        
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
        
        boolean isShuklaPaksha = tithiNumber <= 15;
        double startTime = 6.0; // 6:00 AM
        double endTime = 6.0; // 6:00 AM next day
        
        return new PanchangResult.TithiInfo(tithiNumber, tithiNames[tithiNumber - 1], 
                           sanskritNames[tithiNumber - 1], startTime, endTime, isShuklaPaksha);
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
        // Simplified Nakshatra calculation
        // In actual implementation, this would use astronomical calculations
        
        int dayOfYear = dateTime.getDayOfYear();
        int nakshatraNumber = ((dayOfYear - 1) % 27) + 1;
        
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
        
        double startTime = 6.0; // 6:00 AM
        double endTime = 6.0; // 6:00 AM next day
        
        return new PanchangResult.NakshatraInfo(nakshatraNumber, nakshatraNames[nakshatraNumber - 1],
                               sanskritNames[nakshatraNumber - 1], rulingPlanets[nakshatraNumber - 1],
                               startTime, endTime);
    }
    
    @Override
    public YogaInfo calculateYoga(LocalDateTime dateTime, double latitude, double longitude) {
        // Simplified Yoga calculation
        // In actual implementation, this would use astronomical calculations
        
        int dayOfYear = dateTime.getDayOfYear();
        int yogaNumber = ((dayOfYear - 1) % 27) + 1;
        
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
        
        double startTime = 6.0; // 6:00 AM
        double endTime = 6.0; // 6:00 AM next day
        
        return new PanchangResult.YogaInfo(yogaNumber, yogaNames[yogaNumber - 1], sanskritNames[yogaNumber - 1],
                          startTime, endTime);
    }
    
    @Override
    public KaranaInfo calculateKarana(LocalDateTime dateTime, double latitude, double longitude) {
        // Simplified Karana calculation
        // In actual implementation, this would use astronomical calculations
        
        int dayOfYear = dateTime.getDayOfYear();
        int karanaNumber = ((dayOfYear - 1) % 11) + 1;
        
        String[] karanaNames = {
            "Bava", "Balava", "Kaulava", "Taitila", "Garija",
            "Vanija", "Vishti", "Shakuni", "Chatushpada", "Naga",
            "Kimstughna"
        };
        
        String[] sanskritNames = {
            "बव", "बालव", "कौलव", "तैतिल", "गरिज",
            "वणिज", "विष्टि", "शकुनि", "चतुष्पाद", "नाग",
            "किंस्तुघ्न"
        };
        
        double startTime = 6.0; // 6:00 AM
        double endTime = 6.0; // 6:00 AM next day
        
        return new PanchangResult.KaranaInfo(karanaNumber, karanaNames[karanaNumber - 1], sanskritNames[karanaNumber - 1],
                            startTime, endTime);
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
} 