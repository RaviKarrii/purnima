package com.example.purnima.service;

import com.example.purnima.api.PanchangCalculator;
import com.example.purnima.model.BirthData;
import com.example.purnima.model.PanchangResult;
import com.example.purnima.util.SwissEphCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Default implementation of PanchangCalculator using Swiss Ephemeris.
 */
@Service
public class DefaultPanchangCalculator implements PanchangCalculator {

    private final MessageSource messageSource;

    @Autowired
    public DefaultPanchangCalculator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public PanchangResult calculatePanchang(LocalDate date, double latitude, double longitude, String placeName) {
        return calculatePanchang(date.atStartOfDay(), latitude, longitude, placeName, java.time.ZoneId.systemDefault());
    }

    @Override
    public PanchangResult calculatePanchang(LocalDate date, double latitude, double longitude, String placeName, java.time.ZoneId zoneId) {
        return calculatePanchang(date.atStartOfDay(), latitude, longitude, placeName, zoneId);
    }

    @Override
    public PanchangResult calculatePanchang(LocalDateTime dateTime, double latitude, double longitude, String placeName) {
        return calculatePanchang(dateTime, latitude, longitude, placeName, java.time.ZoneId.systemDefault());
    }

    @Override
    public PanchangResult calculatePanchang(LocalDateTime dateTime, double latitude, double longitude, String placeName, java.time.ZoneId zoneId) {
        Locale locale = LocaleContextHolder.getLocale();
        java.time.LocalDate localDate = dateTime.toLocalDate();
        LocalDateTime dayStart = localDate.atStartOfDay();
        LocalDateTime dayEnd = localDate.plusDays(1).atStartOfDay();

        // 1. Tithi List
        java.util.List<PanchangResult.TithiInfo> tithiList = new java.util.ArrayList<>();
        LocalDateTime current = dayStart;
        while (current.isBefore(dayEnd)) {
            // Convert to UTC for calculation
            java.time.ZonedDateTime zdt = current.atZone(zoneId);
            java.time.ZonedDateTime utZdt = zdt.withZoneSameInstant(java.time.ZoneId.of("UTC"));
            LocalDateTime utcDateTime = utZdt.toLocalDateTime();

            SwissEphCalculator.LunarPhase lunarPhase = SwissEphCalculator.calculateLunarPhase(utcDateTime, latitude, longitude);
            int tithiNumber = lunarPhase.getTithi();
            String tithiName = messageSource.getMessage("tithi." + tithiNumber, null, "Tithi " + tithiNumber, locale);
            
            LocalDateTime tithiEndTimeUtc = findTithiEndTime(utcDateTime, latitude, longitude, tithiNumber);
            // Convert back to local for display/loop check
            java.time.ZonedDateTime endZdt = tithiEndTimeUtc.atZone(java.time.ZoneId.of("UTC")).withZoneSameInstant(zoneId);
            LocalDateTime tithiEndTimeLocal = endZdt.toLocalDateTime();

            double startDecimal = toDecimalTime(current); // current is already local
            // Handle element spanning beyond dayEnd
            String endTimeStr;
            if (tithiEndTimeLocal.isAfter(dayEnd)) {
                endTimeStr = "Next Day"; // Or some indicator
            } else {
                endTimeStr = com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(tithiEndTimeLocal));
            }

            tithiList.add(new PanchangResult.TithiInfo(
                tithiNumber, tithiName, tithiName, 
                com.example.purnima.util.TimeUtil.formatDecimalTime(startDecimal), 
                endTimeStr, 
                lunarPhase.isShuklaPaksha()
            ));
            
            current = tithiEndTimeLocal.plusSeconds(1); // Advance slightly to avoid edge case
        }

        // 2. Vara (Day of Week)
        int dayOfWeek = dateTime.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
        int vedicDay = (dayOfWeek == 7) ? 1 : dayOfWeek + 1;
        String[] varaKeys = {"vara.sunday", "vara.monday", "vara.tuesday", "vara.wednesday", "vara.thursday", "vara.friday", "vara.saturday"};
        String varaName = messageSource.getMessage(varaKeys[vedicDay - 1], null, locale);
        String rulingPlanet = getVaraRulingPlanet(vedicDay);
        PanchangResult.VaraInfo varaInfo = new PanchangResult.VaraInfo(vedicDay, varaName, varaName, rulingPlanet);

        // 3. Nakshatra List
        java.util.List<PanchangResult.NakshatraInfo> nakList = new java.util.ArrayList<>();
        current = dayStart;
        while (current.isBefore(dayEnd)) {
            java.time.ZonedDateTime zdt = current.atZone(zoneId);
            java.time.ZonedDateTime utZdt = zdt.withZoneSameInstant(java.time.ZoneId.of("UTC"));
            LocalDateTime utcDateTime = utZdt.toLocalDateTime();

            SwissEphCalculator.NakshatraInfo nakInfo = SwissEphCalculator.calculateNakshatra(utcDateTime, latitude, longitude);
            int nakshatraNumber = nakInfo.getNakshatraNumber();
            String nakshatraName = messageSource.getMessage("nakshatra." + nakshatraNumber, null, "Nakshatra " + nakshatraNumber, locale);
            String nakshatraRulingPlanet = getNakshatraRulingPlanet(nakshatraNumber);
            
            LocalDateTime nakEndTimeUtc = findNakshatraEndTime(utcDateTime, latitude, longitude, nakshatraNumber);
            java.time.ZonedDateTime endZdt = nakEndTimeUtc.atZone(java.time.ZoneId.of("UTC")).withZoneSameInstant(zoneId);
            LocalDateTime nakEndTimeLocal = endZdt.toLocalDateTime();

            double startDecimal = toDecimalTime(current);
            String endTimeStr = nakEndTimeLocal.isAfter(dayEnd) ? "Next Day" : 
                com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(nakEndTimeLocal));
            
            nakList.add(new PanchangResult.NakshatraInfo(
                nakshatraNumber, nakshatraName, nakshatraName, nakshatraRulingPlanet, 
                com.example.purnima.util.TimeUtil.formatDecimalTime(startDecimal), 
                endTimeStr
            ));
            current = nakEndTimeLocal.plusSeconds(1);
        }

        // 4. Yoga List
        java.util.List<PanchangResult.YogaInfo> yogaList = new java.util.ArrayList<>();
        current = dayStart;
        while (current.isBefore(dayEnd)) {
            java.time.ZonedDateTime zdt = current.atZone(zoneId);
            java.time.ZonedDateTime utZdt = zdt.withZoneSameInstant(java.time.ZoneId.of("UTC"));
            LocalDateTime utcDateTime = utZdt.toLocalDateTime();

            SwissEphCalculator.YogaInfo yogaInfoObj = SwissEphCalculator.calculateYoga(utcDateTime, latitude, longitude);
            int yogaNumber = yogaInfoObj.getYogaNumber();
            String yogaName = messageSource.getMessage("yoga." + yogaNumber, null, "Yoga " + yogaNumber, locale);
            
            LocalDateTime yogaEndTimeUtc = findYogaEndTime(utcDateTime, latitude, longitude, yogaNumber);
            java.time.ZonedDateTime endZdt = yogaEndTimeUtc.atZone(java.time.ZoneId.of("UTC")).withZoneSameInstant(zoneId);
            LocalDateTime yogaEndTimeLocal = endZdt.toLocalDateTime();

            double startDecimal = toDecimalTime(current);
            String endTimeStr = yogaEndTimeLocal.isAfter(dayEnd) ? "Next Day" : 
                com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(yogaEndTimeLocal));
            
            yogaList.add(new PanchangResult.YogaInfo(
                yogaNumber, yogaName, yogaName, 
                com.example.purnima.util.TimeUtil.formatDecimalTime(startDecimal), 
                endTimeStr
            ));
            current = yogaEndTimeLocal.plusSeconds(1);
        }

        // 5. Karana List
        java.util.List<PanchangResult.KaranaInfo> karanaList = new java.util.ArrayList<>();
        current = dayStart;
        while (current.isBefore(dayEnd)) {
            java.time.ZonedDateTime zdt = current.atZone(zoneId);
            java.time.ZonedDateTime utZdt = zdt.withZoneSameInstant(java.time.ZoneId.of("UTC"));
            LocalDateTime utcDateTime = utZdt.toLocalDateTime();

            SwissEphCalculator.KaranaInfo kInfo = SwissEphCalculator.calculateKarana(utcDateTime, latitude, longitude);
            int karanaNumber = kInfo.getKaranaNumber();
            
            String karanaKey = getKaranaKey(karanaNumber);
            String karanaName = messageSource.getMessage(karanaKey, null, "Karana " + karanaNumber, locale);
            
            LocalDateTime karanaEndTimeUtc = findKaranaEndTime(utcDateTime, latitude, longitude, karanaNumber);
            java.time.ZonedDateTime endZdt = karanaEndTimeUtc.atZone(java.time.ZoneId.of("UTC")).withZoneSameInstant(zoneId);
            LocalDateTime karanaEndTimeLocal = endZdt.toLocalDateTime();

            double startDecimal = toDecimalTime(current);
            String endTimeStr = karanaEndTimeLocal.isAfter(dayEnd) ? "Next Day" : 
                com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(karanaEndTimeLocal));
            
            karanaList.add(new PanchangResult.KaranaInfo(
                karanaNumber, karanaName, karanaName, 
                com.example.purnima.util.TimeUtil.formatDecimalTime(startDecimal), 
                endTimeStr
            ));
            current = karanaEndTimeLocal.plusSeconds(1);
        }


        // 6. Muhurta (Simplified placeholder)
        PanchangResult.MuhurtaInfo muhurtaInfo = new PanchangResult.MuhurtaInfo(
            "04:30 - 05:30", "11:45 - 12:30", "18:00 - 18:30",
            "16:30 - 18:00", "14:30 - 16:00", "12:00 - 13:30"
        );

        // 7. Rise/Set Times
        // Use noon logic to get meaningful rise/set for the day
        LocalDateTime noon = dayStart.plusHours(12);
        java.time.ZonedDateTime zdtNoon = noon.atZone(zoneId);
        java.time.ZonedDateTime utZdtNoon = zdtNoon.withZoneSameInstant(java.time.ZoneId.of("UTC"));
        LocalDateTime utcNoon = utZdtNoon.toLocalDateTime();
        
        LocalDateTime sunriseUt = SwissEphCalculator.calculateSunrise(utcNoon, latitude, longitude);
        LocalDateTime sunsetUt = SwissEphCalculator.calculateSunset(utcNoon, latitude, longitude);
        LocalDateTime moonriseUt = SwissEphCalculator.calculateMoonrise(utcNoon, latitude, longitude);
        LocalDateTime moonsetUt = SwissEphCalculator.calculateMoonset(utcNoon, latitude, longitude);

        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        java.time.ZoneId utcZone = java.time.ZoneId.of("UTC");
        
        String sunriseStr = (sunriseUt != null) ? sunriseUt.atZone(utcZone).withZoneSameInstant(zoneId).format(timeFormatter) : null;
        String sunsetStr = (sunsetUt != null) ? sunsetUt.atZone(utcZone).withZoneSameInstant(zoneId).format(timeFormatter) : null;
        String moonriseStr = (moonriseUt != null) ? moonriseUt.atZone(utcZone).withZoneSameInstant(zoneId).format(timeFormatter) : null;
        String moonsetStr = (moonsetUt != null) ? moonsetUt.atZone(utcZone).withZoneSameInstant(zoneId).format(timeFormatter) : null;

        return new PanchangResult(dateTime, latitude, longitude, placeName,
                                  sunriseStr, sunsetStr, moonriseStr, moonsetStr,
                                  tithiList, varaInfo, nakList, yogaList, karanaList, muhurtaInfo);
    }

    @Override
    public PanchangResult calculatePanchang(BirthData birthData) {
        return calculatePanchang(birthData.getBirthDateTime(), 
                               birthData.getLatitude(), 
                               birthData.getLongitude(),
                               birthData.getPlaceName());
    }

    @Override
    public PanchangResult.TithiInfo calculateTithi(LocalDateTime dateTime, double latitude, double longitude) {
        Locale locale = LocaleContextHolder.getLocale();
        SwissEphCalculator.LunarPhase lunarPhase = SwissEphCalculator.calculateLunarPhase(dateTime, latitude, longitude);
        int tithiNumber = lunarPhase.getTithi();
        String tithiName = messageSource.getMessage("tithi." + tithiNumber, null, "Tithi " + tithiNumber, locale);
        LocalDateTime tithiEndTime = findTithiEndTime(dateTime, latitude, longitude, tithiNumber);
        return new PanchangResult.TithiInfo(tithiNumber, tithiName, tithiName, 
            com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(dateTime)), 
            com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(tithiEndTime)), 
            lunarPhase.isShuklaPaksha());
    }

    @Override
    public PanchangResult.VaraInfo calculateVara(LocalDate date) {
        Locale locale = LocaleContextHolder.getLocale();
        int dayOfWeek = date.getDayOfWeek().getValue();
        int vedicDay = (dayOfWeek == 7) ? 1 : dayOfWeek + 1;
        String[] varaKeys = {"vara.sunday", "vara.monday", "vara.tuesday", "vara.wednesday", "vara.thursday", "vara.friday", "vara.saturday"};
        String varaName = messageSource.getMessage(varaKeys[vedicDay - 1], null, locale);
        String rulingPlanet = getVaraRulingPlanet(vedicDay);
        return new PanchangResult.VaraInfo(vedicDay, varaName, varaName, rulingPlanet);
    }

    @Override
    public PanchangResult.NakshatraInfo calculateNakshatra(LocalDateTime dateTime, double latitude, double longitude) {
        Locale locale = LocaleContextHolder.getLocale();
        SwissEphCalculator.NakshatraInfo nakInfo = SwissEphCalculator.calculateNakshatra(dateTime, latitude, longitude);
        int nakshatraNumber = nakInfo.getNakshatraNumber();
        String nakshatraName = messageSource.getMessage("nakshatra." + nakshatraNumber, null, "Nakshatra " + nakshatraNumber, locale);
        String nakshatraRulingPlanet = getNakshatraRulingPlanet(nakshatraNumber);
        LocalDateTime nakshatraEndTime = findNakshatraEndTime(dateTime, latitude, longitude, nakshatraNumber);
        return new PanchangResult.NakshatraInfo(nakshatraNumber, nakshatraName, nakshatraName, nakshatraRulingPlanet, 
            com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(dateTime)), 
            com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(nakshatraEndTime)));
    }

    @Override
    public PanchangResult.YogaInfo calculateYoga(LocalDateTime dateTime, double latitude, double longitude) {
        Locale locale = LocaleContextHolder.getLocale();
        SwissEphCalculator.YogaInfo yogaInfoObj = SwissEphCalculator.calculateYoga(dateTime, latitude, longitude);
        int yogaNumber = yogaInfoObj.getYogaNumber();
        String yogaName = messageSource.getMessage("yoga." + yogaNumber, null, "Yoga " + yogaNumber, locale);
        LocalDateTime yogaEndTime = findYogaEndTime(dateTime, latitude, longitude, yogaNumber);
        return new PanchangResult.YogaInfo(yogaNumber, yogaName, yogaName, 
            com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(dateTime)), 
            com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(yogaEndTime)));
    }

    @Override
    public PanchangResult.KaranaInfo calculateKarana(LocalDateTime dateTime, double latitude, double longitude) {
        Locale locale = LocaleContextHolder.getLocale();
        SwissEphCalculator.KaranaInfo kInfo = SwissEphCalculator.calculateKarana(dateTime, latitude, longitude);
        int karanaNumber = kInfo.getKaranaNumber();
        String karanaKey = getKaranaKey(karanaNumber);
        String karanaName = messageSource.getMessage(karanaKey, null, "Karana " + karanaNumber, locale);
        LocalDateTime karanaEndTime = findKaranaEndTime(dateTime, latitude, longitude, karanaNumber);
        return new PanchangResult.KaranaInfo(karanaNumber, karanaName, karanaName, 
            com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(dateTime)), 
            com.example.purnima.util.TimeUtil.formatDecimalTime(toDecimalTime(karanaEndTime)));
    }

    @Override
    public PanchangResult.MuhurtaInfo getAuspiciousTimings(LocalDate date, double latitude, double longitude) {
        return new PanchangResult.MuhurtaInfo(
            "04:30 - 05:30", "11:45 - 12:30", "18:00 - 18:30",
            "16:30 - 18:00", "14:30 - 16:00", "12:00 - 13:30"
        );
    }
    
    private String getKaranaKey(int karanaNumber) {
        if (karanaNumber == 1) return "karana.kimstughna";
        if (karanaNumber >= 2 && karanaNumber <= 57) {
            int cycleIndex = (karanaNumber - 2) % 7;
            switch (cycleIndex) {
                case 0: return "karana.bava";
                case 1: return "karana.balava";
                case 2: return "karana.kaulava";
                case 3: return "karana.taitila";
                case 4: return "karana.garija";
                case 5: return "karana.vanija";
                case 6: return "karana.vishti";
            }
        }
        if (karanaNumber == 58) return "karana.shakuni";
        if (karanaNumber == 59) return "karana.chatushpada";
        if (karanaNumber == 60) return "karana.naga";
        return "karana.bava";
    }
    
    private String getVaraRulingPlanet(int vedicDay) {
        String[] rulingPlanets = {"Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"};
        if (vedicDay >= 1 && vedicDay <= 7) {
            return rulingPlanets[vedicDay - 1];
        }
        return "Unknown";
    }
    
    private String getNakshatraRulingPlanet(int nakshatraNumber) {
        String[] rulingPlanets = {
            "Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter",
            "Saturn", "Mercury", "Ketu", "Venus", "Sun", "Moon",
            "Mars", "Rahu", "Jupiter", "Saturn", "Mercury", "Ketu",
            "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter",
            "Saturn", "Mercury"
        };
        if (nakshatraNumber >= 1 && nakshatraNumber <= 27) {
            return rulingPlanets[nakshatraNumber - 1];
        }
        return "Unknown";
    }

    private LocalDateTime findTithiEndTime(LocalDateTime start, double lat, double lon, int currentTithi) {
        return findEndTime(start, lat, lon, (dt) -> SwissEphCalculator.calculateLunarPhase(dt, lat, lon).getTithi(), currentTithi);
    }

    private LocalDateTime findNakshatraEndTime(LocalDateTime start, double lat, double lon, int currentNakshatra) {
        return findEndTime(start, lat, lon, (dt) -> SwissEphCalculator.calculateNakshatra(dt, lat, lon).getNakshatraNumber(), currentNakshatra);
    }

    private LocalDateTime findYogaEndTime(LocalDateTime start, double lat, double lon, int currentYoga) {
        return findEndTime(start, lat, lon, (dt) -> SwissEphCalculator.calculateYoga(dt, lat, lon).getYogaNumber(), currentYoga);
    }

    private LocalDateTime findKaranaEndTime(LocalDateTime start, double lat, double lon, int currentKarana) {
        return findEndTime(start, lat, lon, (dt) -> SwissEphCalculator.calculateKarana(dt, lat, lon).getKaranaNumber(), currentKarana);
    }

    private LocalDateTime findEndTime(LocalDateTime start, double lat, double lon, java.util.function.Function<LocalDateTime, Integer> valueProvider, int startValue) {
        LocalDateTime low = start;
        LocalDateTime high = start.plusHours(24);
        
        for (int i = 0; i < 12; i++) {
            long seconds = java.time.Duration.between(low, high).getSeconds();
            LocalDateTime mid = low.plusSeconds(seconds / 2);
            int midValue = valueProvider.apply(mid);
            
            if (midValue == startValue) {
                low = mid;
            } else {
                high = mid;
            }
        }
        return high;
    }

    private double toDecimalTime(LocalDateTime dt) {
        return dt.getHour() + (dt.getMinute() / 60.0) + (dt.getSecond() / 3600.0);
    }

    private double toDecimalTime(LocalDateTime dt, java.time.ZoneId zoneId) {
        // dt is in UT (from SwissEph), convert to target zoneId
        java.time.ZonedDateTime utZdt = java.time.ZonedDateTime.of(dt, java.time.ZoneId.of("UTC"));
        java.time.ZonedDateTime targetZdt = utZdt.withZoneSameInstant(zoneId);
        LocalDateTime targetDt = targetZdt.toLocalDateTime();
        return targetDt.getHour() + (targetDt.getMinute() / 60.0) + (targetDt.getSecond() / 3600.0);
    }
}