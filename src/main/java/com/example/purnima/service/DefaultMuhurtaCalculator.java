package com.example.purnima.service;

import com.example.purnima.api.MuhurtaCalculator;
import com.example.purnima.model.MuhurtaResult;
import com.example.purnima.util.SwissEphCalculator;
import de.thmac.swisseph.SweConst;
import de.thmac.swisseph.SweDate;
import de.thmac.swisseph.SwissEph;
import de.thmac.swisseph.DblObj;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of MuhurtaCalculator.
 */
public class DefaultMuhurtaCalculator implements MuhurtaCalculator {

    private final SwissEph sw = new SwissEph();

    // Choghadiya Rulers (Day): Sun, Ven, Mer, Mon, Sat, Jup, Mar (Udveg, Chal, Labh, Amrit, Kaal, Shubh, Rog)
    // Actually, the sequence depends on the weekday.
    // Standard sequence: Udveg, Chal, Labh, Amrit, Kaal, Shubh, Rog
    // Sunday starts with Udveg. Monday with Amrit. Tuesday with Rog. Wednesday with Labh. Thursday with Shubh. Friday with Chal. Saturday with Kaal.
    
    private static final String[] CHOGHADIYA_NAMES = {
        "Udveg", "Chal", "Labh", "Amrit", "Kaal", "Shubh", "Rog"
    };
    
    private static final String[] CHOGHADIYA_NATURE = {
        "Bad", "Neutral", "Good", "Good", "Bad", "Good", "Bad"
    };
    
    // Starting index for each weekday (Sunday=0, Monday=1, ...)
    // Sunday (0): Udveg (0)
    // Monday (1): Amrit (3)
    // Tuesday (2): Rog (6)
    // Wednesday (3): Labh (2)
    // Thursday (4): Shubh (5)
    // Friday (5): Chal (1)
    // Saturday (6): Kaal (4)
    private static final int[] DAY_START_INDEX = {0, 3, 6, 2, 5, 1, 4};
    
    // Night Choghadiya Rulers
    // Sunday: Shubh (5)
    // Monday: Chal (1)
    // Tuesday: Kaal (4)
    // Wednesday: Udveg (0)
    // Thursday: Amrit (3)
    // Friday: Rog (6)
    // Saturday: Labh (2)
    private static final int[] NIGHT_START_INDEX = {5, 1, 4, 0, 3, 6, 2};
    
    // Hora Rulers (1st hour of day ruled by weekday lord)
    // Sun, Ven, Mer, Mon, Sat, Jup, Mar -> Speed order? No.
    // Hora order: Saturn, Jupiter, Mars, Sun, Venus, Mercury, Moon (Slowest to Fastest)
    // But the sequence for the day starts with the weekday lord.
    // Then it follows the order: Sun -> Venus -> Mercury -> Moon -> Saturn -> Jupiter -> Mars -> Sun...
    // Wait, standard Hora order is: Sun, Venus, Mercury, Moon, Saturn, Jupiter, Mars.
    // Let's verify.
    // 1st Hora of Sunday is Sun. 2nd is Venus. 3rd Mercury. 4th Moon. 5th Saturn. 6th Jupiter. 7th Mars. 8th Sun.
    // Yes, this is the sequence.
    private static final String[] HORA_PLANETS = {
        "Sun", "Venus", "Mercury", "Moon", "Saturn", "Jupiter", "Mars"
    };
    
    // Rahu Kalam, Yamagandam, Gulika Kalam (Start/End as fraction of day duration / 8 parts)
    // Weekday indices: Sun=0, Mon=1, ... Sat=6
    // Rahu: Sun(8), Mon(2), Tue(7), Wed(5), Thu(6), Fri(4), Sat(3) - 8th part is 4:30-6:00 usually.
    // Let's use standard 8-part segments.
    // Segments: 1(6-7.30), 2(7.30-9), 3(9-10.30), 4(10.30-12), 5(12-1.30), 6(1.30-3), 7(3-4.30), 8(4.30-6)
    // Rahu: Sun(8), Mon(2), Tue(7), Wed(5), Thu(6), Fri(4), Sat(3)
    private static final int[] RAHU_SEGMENTS = {8, 2, 7, 5, 6, 4, 3};
    
    // Yamagandam: Sun(5), Mon(4), Tue(3), Wed(2), Thu(1), Fri(7), Sat(6)
    private static final int[] YAMA_SEGMENTS = {5, 4, 3, 2, 1, 7, 6};
    
    // Gulika: Sun(7), Mon(6), Tue(5), Wed(4), Thu(3), Fri(2), Sat(1)
    private static final int[] GULIKA_SEGMENTS = {7, 6, 5, 4, 3, 2, 1};

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
        // Start search from midnight local time converted to UT, or just use noon - 12 hours?
        // Better: Start from 6 AM local time minus 12 hours to be safe? 
        // Or just start from 00:00 local time.
        // Let's use 00:00 local time of the date.
        
        LocalDateTime startOfDay = date.atStartOfDay();
        // Convert to UT for SwissEph start time
        // Actually, SweDate takes UT.
        // If we want to find sunrise for a local date, we should start searching from a bit before expected sunrise.
        // 00:00 local time is safe.
        
        // Convert local 00:00 to UT
        LocalDateTime utStart = startOfDay.atZone(zoneId).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        SweDate sd = new SweDate(utStart.getYear(), utStart.getMonthValue(), utStart.getDayOfMonth(), 
                                utStart.getHour() + utStart.getMinute()/60.0 + utStart.getSecond()/3600.0);
        double julianDay = sd.getJulDay();
        
        DblObj rise = new DblObj();
        DblObj set = new DblObj();
        StringBuffer serr = new StringBuffer();
        
        double[] geopos = {longitude, latitude, 0};
        
        // Calculate Sunrise
        // Note: swe_rise_trans finds the *next* rise/set after the given julianDay.
        // If we start at 00:00 local, we should find today's sunrise (usually after 00:00).
        sw.swe_rise_trans(julianDay, SweConst.SE_SUN, null, SweConst.SEFLG_SWIEPH, SweConst.SE_CALC_RISE, geopos, 0, 0, rise, serr);
        
        // Calculate Sunset
        sw.swe_rise_trans(julianDay, SweConst.SE_SUN, null, SweConst.SEFLG_SWIEPH, SweConst.SE_CALC_SET, geopos, 0, 0, set, serr);
        
        LocalDateTime sunriseTime = convertUtJdToLocal(rise.val, zoneId);
        LocalDateTime sunsetTime = convertUtJdToLocal(set.val, zoneId);
        
        // Verify that we got the sunrise/sunset for the correct day.
        // It's possible that 00:00 local is after sunrise (e.g. in high latitudes in summer? or if we are close to timezone edge).
        // Or if we start at 00:00 and sunrise was at 23:59 previous day? Unlikely.
        // But if we start at 00:00 and sunrise is at 06:00, we get it.
        // If we start at 00:00 and sunset is at 18:00, we get it.
        
        // However, if we are looking for sunrise on date D.
        // If we start at D 00:00.
        // If sunrise is D 06:00, we get D 06:00.
        // If sunset is D 18:00, we get D 18:00.
        // This seems correct.
        
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
        double durationPerChoghadiya = totalSeconds / 8.0; // 8 parts? No, 8 parts for Rahu/Yama, but Choghadiya is 8 parts?
        // Choghadiya divides day/night into 8 parts.
        
        int weekday = date.getDayOfWeek().getValue() % 7; // 1=Mon, ..., 7=Sun -> 0=Sun, 1=Mon, ... 6=Sat
        // Java DayOfWeek: Mon=1, Sun=7.
        // My arrays assume Sun=0.
        if (weekday == 0) weekday = 0; // Sun=7%7=0. Correct.
        
        int startIndex = isDay ? DAY_START_INDEX[weekday] : NIGHT_START_INDEX[weekday];
        
        LocalDateTime current = start;
        for (int i = 0; i < 8; i++) {
            int index = (startIndex + i) % 7;
            LocalDateTime next = start.plusSeconds((long) ((i + 1) * durationPerChoghadiya));
            
            list.add(new MuhurtaResult.Choghadiya(
                CHOGHADIYA_NAMES[index],
                current,
                next,
                CHOGHADIYA_NATURE[index]
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
        
        // 1st Hora ruler is the weekday lord
        // Sun=0 -> Sun (Index 0 in HORA_PLANETS? No. HORA_PLANETS={"Sun", "Venus", ...})
        // Weekday Lords: Sun, Mon, Tue, Wed, Thu, Fri, Sat
        // HORA_PLANETS indices: Sun=0, Mon=3, Tue=6, Wed=2, Thu=5, Fri=1, Sat=4
        
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
            
            list.add(new MuhurtaResult.Hora(
                HORA_PLANETS[index],
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
