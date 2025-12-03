package com.example.purnima.service;

import com.example.purnima.api.MuhurtaCalculator;
import com.example.purnima.model.MuhurtaResult;
import com.example.purnima.util.SwissEphCalculator;
import de.thmac.swisseph.SweConst;
import de.thmac.swisseph.SweDate;
import de.thmac.swisseph.SwissEph;
import de.thmac.swisseph.DblObj;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Default implementation of MuhurtaCalculator.
 */
public class DefaultMuhurtaCalculator implements MuhurtaCalculator {

    private final SwissEph sw = new SwissEph();
    private final MessageSource messageSource;

    // Choghadiya Rulers (Day): Sun, Ven, Mer, Mon, Sat, Jup, Mar (Udveg, Chal, Labh, Amrit, Kaal, Shubh, Rog)
    // Keys for localization
    private static final String[] CHOGHADIYA_NAME_KEYS = {
        "choghadiya.udveg", "choghadiya.chal", "choghadiya.labh", "choghadiya.amrit", "choghadiya.kaal", "choghadiya.shubh", "choghadiya.rog"
    };
    
    // Nature keys
    private static final String[] CHOGHADIYA_NATURE_KEYS = {
        "choghadiya.nature.bad", "choghadiya.nature.neutral", "choghadiya.nature.good", "choghadiya.nature.good", "choghadiya.nature.bad", "choghadiya.nature.good", "choghadiya.nature.bad"
    };
    
    // Starting index for each weekday (Sunday=0, Monday=1, ...)
    private static final int[] DAY_START_INDEX = {0, 3, 6, 2, 5, 1, 4};
    
    // Night Choghadiya Rulers
    private static final int[] NIGHT_START_INDEX = {5, 1, 4, 0, 3, 6, 2};
    
    // Hora Rulers (1st hour of day ruled by weekday lord)
    // Keys for localization
    private static final String[] HORA_PLANET_KEYS = {
        "planet.sun", "planet.venus", "planet.mercury", "planet.moon", "planet.saturn", "planet.jupiter", "planet.mars"
    };
    
    // Rahu Kalam, Yamagandam, Gulika Kalam (Start/End as fraction of day duration / 8 parts)
    private static final int[] RAHU_SEGMENTS = {8, 2, 7, 5, 6, 4, 3};
    private static final int[] YAMA_SEGMENTS = {5, 4, 3, 2, 1, 7, 6};
    private static final int[] GULIKA_SEGMENTS = {7, 6, 5, 4, 3, 2, 1};

    public DefaultMuhurtaCalculator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Default constructor for backward compatibility
    public DefaultMuhurtaCalculator() {
        this.messageSource = null;
    }
    
    private String getLocalizedMessage(String key, String defaultMsg) {
        if (messageSource == null) return defaultMsg;
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, defaultMsg, locale);
    }

    @Override
    public MuhurtaResult calculateMuhurta(LocalDate date, double latitude, double longitude, ZoneId zoneId) {
        MuhurtaResult result = new MuhurtaResult();
        
        // 1. Calculate Sunrise and Sunset
        LocalDateTime[] sunRiseSet = calculateSunriseSunset(date, latitude, longitude, zoneId);
        LocalDateTime sunrise = sunRiseSet[0];
        LocalDateTime sunset = sunRiseSet[1];
        LocalDateTime nextSunrise = calculateSunriseSunset(date.plusDays(1), latitude, longitude, zoneId)[0];
        
        // 2. Calculate Day Choghadiya
        result.setDayChoghadiya(calculateChoghadiya(date, sunrise, sunset, true));
        
        // 3. Calculate Night Choghadiya
        result.setNightChoghadiya(calculateChoghadiya(date, sunset, nextSunrise, false));
        
        // 4. Calculate Hora
        result.setHoras(calculateHoras(date, sunrise, nextSunrise));
        
        // 5. Calculate Inauspicious Times
        result.setInauspiciousTimes(calculateInauspiciousTimes(date, sunrise, sunset));
        
        return result;
    }
    
    private LocalDateTime[] calculateSunriseSunset(LocalDate date, double latitude, double longitude, ZoneId zoneId) {
        // Use SwissEph to calculate sunrise/sunset
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime utStart = startOfDay.atZone(zoneId).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        SweDate sd = new SweDate(utStart.getYear(), utStart.getMonthValue(), utStart.getDayOfMonth(), 
                                utStart.getHour() + utStart.getMinute()/60.0 + utStart.getSecond()/3600.0);
        double julianDay = sd.getJulDay();
        
        DblObj rise = new DblObj();
        DblObj set = new DblObj();
        StringBuffer serr = new StringBuffer();
        
        double[] geopos = {longitude, latitude, 0};
        
        // Calculate Sunrise
        sw.swe_rise_trans(julianDay, SweConst.SE_SUN, null, SweConst.SEFLG_SWIEPH, SweConst.SE_CALC_RISE, geopos, 0, 0, rise, serr);
        
        // Calculate Sunset
        sw.swe_rise_trans(julianDay, SweConst.SE_SUN, null, SweConst.SEFLG_SWIEPH, SweConst.SE_CALC_SET, geopos, 0, 0, set, serr);
        
        LocalDateTime sunriseTime = convertUtJdToLocal(rise.val, zoneId);
        LocalDateTime sunsetTime = convertUtJdToLocal(set.val, zoneId);
        
        return new LocalDateTime[]{sunriseTime, sunsetTime};
    }
    
    private LocalDateTime convertUtJdToLocal(double utJd, ZoneId zoneId) {
        SweDate sd = new SweDate(utJd);
        int year = sd.getYear();
        int month = sd.getMonth();
        int day = sd.getDay();
        double hourVal = sd.getHour();
        
        int hour = (int) hourVal;
        int minute = (int) ((hourVal - hour) * 60);
        int second = (int) ((((hourVal - hour) * 60) - minute) * 60);
        
        // This is UT
        LocalDateTime utTime = LocalDateTime.of(year, month, day, hour, minute, second);
        
        // Convert to ZoneId
        return utTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(zoneId).toLocalDateTime();
    }
    
    private List<MuhurtaResult.Choghadiya> calculateChoghadiya(LocalDate date, LocalDateTime start, LocalDateTime end, boolean isDay) {
        List<MuhurtaResult.Choghadiya> list = new ArrayList<>();
        long totalSeconds = java.time.Duration.between(start, end).getSeconds();
        double durationPerChoghadiya = totalSeconds / 8.0; 
        
        int weekday = date.getDayOfWeek().getValue() % 7; // 1=Mon, ..., 7=Sun -> 0=Sun, 1=Mon, ... 6=Sat
        if (weekday == 0) weekday = 0; 
        
        int startIndex = isDay ? DAY_START_INDEX[weekday] : NIGHT_START_INDEX[weekday];
        
        LocalDateTime current = start;
        for (int i = 0; i < 8; i++) {
            int index = (startIndex + i) % 7;
            LocalDateTime next = start.plusSeconds((long) ((i + 1) * durationPerChoghadiya));
            
            String name = getLocalizedMessage(CHOGHADIYA_NAME_KEYS[index], "Unknown");
            String nature = getLocalizedMessage(CHOGHADIYA_NATURE_KEYS[index], "Unknown");
            
            list.add(new MuhurtaResult.Choghadiya(
                name,
                current,
                next,
                nature
            ));
            current = next;
        }
        return list;
    }
    
    private List<MuhurtaResult.Hora> calculateHoras(LocalDate date, LocalDateTime sunrise, LocalDateTime nextSunrise) {
        List<MuhurtaResult.Hora> list = new ArrayList<>();
        long totalSeconds = java.time.Duration.between(sunrise, nextSunrise).getSeconds();
        double durationPerHour = totalSeconds / 24.0;
        
        int weekday = date.getDayOfWeek().getValue() % 7; // Sun=0
        
        int startPlanetIndex = 0;
        switch (weekday) {
            case 0: startPlanetIndex = 0; break; // Sun
            case 1: startPlanetIndex = 3; break; // Mon
            case 2: startPlanetIndex = 6; break; // Tue
            case 3: startPlanetIndex = 2; break; // Wed
            case 4: startPlanetIndex = 5; break; // Thu
            case 5: startPlanetIndex = 1; break; // Fri
            case 6: startPlanetIndex = 4; break; // Sat
        }
        
        LocalDateTime current = sunrise;
        for (int i = 0; i < 24; i++) {
            int index = (startPlanetIndex + i) % 7;
            LocalDateTime next = sunrise.plusSeconds((long) ((i + 1) * durationPerHour));
            
            String planetName = getLocalizedMessage(HORA_PLANET_KEYS[index], "Unknown");
            
            list.add(new MuhurtaResult.Hora(
                planetName,
                current,
                next
            ));
            current = next;
        }
        return list;
    }
    
    private MuhurtaResult.InauspiciousTimes calculateInauspiciousTimes(LocalDate date, LocalDateTime sunrise, LocalDateTime sunset) {
        long daySeconds = java.time.Duration.between(sunrise, sunset).getSeconds();
        double partDuration = daySeconds / 8.0;
        
        int weekday = date.getDayOfWeek().getValue() % 7; // Sun=0
        
        // Rahu
        int rahuSegment = RAHU_SEGMENTS[weekday]; // 1-8
        LocalDateTime rahuStart = sunrise.plusSeconds((long) ((rahuSegment - 1) * partDuration));
        LocalDateTime rahuEnd = sunrise.plusSeconds((long) (rahuSegment * partDuration));
        
        // Yama
        int yamaSegment = YAMA_SEGMENTS[weekday];
        LocalDateTime yamaStart = sunrise.plusSeconds((long) ((yamaSegment - 1) * partDuration));
        LocalDateTime yamaEnd = sunrise.plusSeconds((long) (yamaSegment * partDuration));
        
        // Gulika
        int gulikaSegment = GULIKA_SEGMENTS[weekday];
        LocalDateTime gulikaStart = sunrise.plusSeconds((long) ((gulikaSegment - 1) * partDuration));
        LocalDateTime gulikaEnd = sunrise.plusSeconds((long) (gulikaSegment * partDuration));
        
        return new MuhurtaResult.InauspiciousTimes(
            new MuhurtaResult.Period(rahuStart, rahuEnd),
            new MuhurtaResult.Period(yamaStart, yamaEnd),
            new MuhurtaResult.Period(gulikaStart, gulikaEnd)
        );
    }
}
