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
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import com.example.purnima.model.MuhurtaSlot;

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
    
    // Rashi Keys
    private static final String[] RASHI_KEYS = {
        "rashi.aries", "rashi.taurus", "rashi.gemini", "rashi.cancer", 
        "rashi.leo", "rashi.virgo", "rashi.libra", "rashi.scorpio", 
        "rashi.sagittarius", "rashi.capricorn", "rashi.aquarius", "rashi.pisces"
    };

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
    @Override
    public List<MuhurtaSlot> findVehiclePurchaseMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId) {
        List<MuhurtaSlot> slots = new ArrayList<>();
        LocalDateTime current = start;
        
        // Vehicle Purchase Criteria:
        // Nakshatras: Ashwini(1), Rohini(4), Mrigashira(5), Punarvasu(7), Pushya(8), Hasta(13), Chitra(14), Swati(15), Anuradha(17), Shravana(22), Dhanishta(23), Shatabhisha(24), Revati(27)
        List<Integer> goodNakshatras = Arrays.asList(1, 4, 5, 7, 8, 13, 14, 15, 17, 22, 23, 24, 27);
        // Tithis: 3, 5, 7, 10, 11, 13, 15 (Purnima)
        List<Integer> goodTithis = Arrays.asList(3, 5, 7, 10, 11, 13, 15);
        // Days: Mon(1), Wed(3), Thu(4), Fri(5)
        List<DayOfWeek> goodDays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        
        while (current.isBefore(end)) {
            if (goodDays.contains(current.getDayOfWeek())) {
                // Check every 30 mins
                LocalDateTime slotStart = current;
                LocalDateTime slotEnd = current.plusMinutes(30);
                LocalDateTime checkTime = slotStart.plusMinutes(15);
                
                MuhurtaResult mr = calculateMuhurta(checkTime.toLocalDate(), latitude, longitude, zoneId);
                // Check Rahu Kalam (approximate check using the calculated daily Rahu Kalam)
                // We need to check if checkTime falls within the Rahu Kalam of that day
                boolean isRahu = isTimeInPeriod(checkTime, mr.getInauspiciousTimes().getRahuKalam());
                
                if (!isRahu) {
                    SwissEphCalculator.NakshatraInfo nakInfo = SwissEphCalculator.calculateNakshatra(checkTime, latitude, longitude);
                    SwissEphCalculator.LunarPhase lunarPhase = SwissEphCalculator.calculateLunarPhase(checkTime, latitude, longitude);
                    
                    if (goodNakshatras.contains(nakInfo.getNakshatraNumber()) && goodTithis.contains(lunarPhase.getTithi())) {
                        addOrMergeSlot(slots, slotStart, slotEnd, getLocalizedMessage("muhurta.quality.good", "Good"), 
                            Arrays.asList(
                                getLocalizedMessage("panchang.nakshatra", "Nakshatra") + ": " + getNakshatraName(nakInfo.getNakshatraNumber()), 
                                getLocalizedMessage("panchang.tithi", "Tithi") + ": " + getTithiName(lunarPhase.getTithi())
                            ));
                    }
                }
            }
            current = current.plusMinutes(30);
        }
        return slots;
    }

    @Override
    public List<MuhurtaSlot> findMarriageMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId) {
        List<MuhurtaSlot> slots = new ArrayList<>();
        LocalDateTime current = start;
        
        // Marriage Criteria:
        // Nakshatras: Rohini(4), Mrigashira(5), Magha(10), Uttara Phalguni(12), Hasta(13), Swati(15), Anuradha(17), Moola(19), Uttara Ashadha(21), Uttara Bhadrapada(26), Revati(27)
        List<Integer> goodNakshatras = Arrays.asList(4, 5, 10, 12, 13, 15, 17, 19, 21, 26, 27);
        // Tithis: 2, 3, 5, 7, 10, 11, 13
        List<Integer> goodTithis = Arrays.asList(2, 3, 5, 7, 10, 11, 13);
        // Days: Mon, Wed, Thu, Fri
        List<DayOfWeek> goodDays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        
        while (current.isBefore(end)) {
            if (goodDays.contains(current.getDayOfWeek())) {
                LocalDateTime slotStart = current;
                LocalDateTime slotEnd = current.plusMinutes(30);
                LocalDateTime checkTime = slotStart.plusMinutes(15);
                
                // Check Combustion (Moodha) - Jupiter and Venus
                // We check once per day ideally, but here per slot is fine
                if (!isCombust(checkTime, latitude, longitude)) {
                    MuhurtaResult mr = calculateMuhurta(checkTime.toLocalDate(), latitude, longitude, zoneId);
                    boolean isRahu = isTimeInPeriod(checkTime, mr.getInauspiciousTimes().getRahuKalam());
                    
                    if (!isRahu) {
                        SwissEphCalculator.NakshatraInfo nakInfo = SwissEphCalculator.calculateNakshatra(checkTime, latitude, longitude);
                        SwissEphCalculator.LunarPhase lunarPhase = SwissEphCalculator.calculateLunarPhase(checkTime, latitude, longitude);
                        
                        if (goodNakshatras.contains(nakInfo.getNakshatraNumber()) && goodTithis.contains(lunarPhase.getTithi())) {
                             addOrMergeSlot(slots, slotStart, slotEnd, getLocalizedMessage("muhurta.quality.good", "Good"), 
                                Arrays.asList(
                                    getLocalizedMessage("panchang.nakshatra", "Nakshatra") + ": " + getNakshatraName(nakInfo.getNakshatraNumber()), 
                                    getLocalizedMessage("panchang.tithi", "Tithi") + ": " + getTithiName(lunarPhase.getTithi())
                                ));
                        }
                    }
                }
            }
            current = current.plusMinutes(30);
        }
        return slots;
    }
    
    private boolean isTimeInPeriod(LocalDateTime time, MuhurtaResult.Period period) {
        // Period dates might be just LocalDate, we need to compare times
        // The Period object in MuhurtaResult has LocalDateTime start/end.
        return !time.isBefore(period.getStartTime()) && !time.isAfter(period.getEndTime());
    }
    
    private void addOrMergeSlot(List<MuhurtaSlot> slots, LocalDateTime start, LocalDateTime end, String quality, List<String> factors) {
        if (!slots.isEmpty()) {
            MuhurtaSlot last = slots.get(slots.size() - 1);
            if (last.getEndTime().equals(start) && last.getQuality().equals(quality)) {
                last.setEndTime(end);
                return;
            }
        }
        MuhurtaSlot slot = new MuhurtaSlot(start, end, quality);
        slot.setPositiveFactors(new ArrayList<>(factors));
        slots.add(slot);
    }
    
    private boolean isCombust(LocalDateTime time, double lat, double lon) {
        SwissEphCalculator.PlanetaryPosition sun = SwissEphCalculator.calculatePlanetPosition(time, lat, lon, "Sun");
        SwissEphCalculator.PlanetaryPosition jupiter = SwissEphCalculator.calculatePlanetPosition(time, lat, lon, "Jupiter");
        SwissEphCalculator.PlanetaryPosition venus = SwissEphCalculator.calculatePlanetPosition(time, lat, lon, "Venus");
        
        double sunLong = sun.getLongitude();
        
        // Check Jupiter (within 11 degrees)
        double jupDiff = Math.abs(sunLong - jupiter.getLongitude());
        if (jupDiff > 180) jupDiff = 360 - jupDiff;
        if (jupDiff < 11) return true;
        
        // Check Venus (within 10 degrees)
        double venDiff = Math.abs(sunLong - venus.getLongitude());
        if (venDiff > 180) venDiff = 360 - venDiff;
        if (venDiff < 10) return true;
        
        return false;
    }
    @Override
    public List<MuhurtaSlot> findGrihaPraveshMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId) {
        List<MuhurtaSlot> slots = new ArrayList<>();
        LocalDateTime current = start;
        
        // Griha Pravesh Criteria:
        // Nakshatras: Rohini(4), Mrigashira(5), Uttara Phalguni(12), Chitra(14), Anuradha(17), Uttara Ashadha(21), Uttara Bhadrapada(26), Revati(27)
        List<Integer> goodNakshatras = Arrays.asList(4, 5, 12, 14, 17, 21, 26, 27);
        // Tithis: 2, 3, 5, 7, 10, 11, 13
        List<Integer> goodTithis = Arrays.asList(2, 3, 5, 7, 10, 11, 13);
        // Days: Mon, Wed, Thu, Fri
        List<DayOfWeek> goodDays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        // Fixed Lagna preferred: Taurus(2), Leo(5), Scorpio(8), Aquarius(11)
        List<Integer> fixedLagnas = Arrays.asList(2, 5, 8, 11);
        
        while (current.isBefore(end)) {
            if (goodDays.contains(current.getDayOfWeek())) {
                LocalDateTime slotStart = current;
                LocalDateTime slotEnd = current.plusMinutes(30);
                LocalDateTime checkTime = slotStart.plusMinutes(15);
                
                // Check Solar Month (Sun Sign)
                // Avoid Chaitra (Sun in Pisces/Aries transition), Pousha (Sun in Sagittarius/Capricorn) etc. depending on tradition.
                // Simplified: Avoid Sun in Aries(1), Cancer(4), Libra(7), Capricorn(10) (Movable signs often avoided for stability, though some texts differ)
                // Let's stick to the plan: Sun in Taurus, Gemini, Leo, Virgo, Scorpio, Sagittarius, Aquarius, Pisces.
                // Avoid: Aries(1), Cancer(4), Libra(7), Capricorn(10).
                int sunSign = getPlanetSign(checkTime, latitude, longitude, "Sun");
                if (sunSign != 1 && sunSign != 4 && sunSign != 7 && sunSign != 10) {
                    
                    MuhurtaResult mr = calculateMuhurta(checkTime.toLocalDate(), latitude, longitude, zoneId);
                    if (!isTimeInPeriod(checkTime, mr.getInauspiciousTimes().getRahuKalam())) {
                        SwissEphCalculator.NakshatraInfo nakInfo = SwissEphCalculator.calculateNakshatra(checkTime, latitude, longitude);
                        SwissEphCalculator.LunarPhase lunarPhase = SwissEphCalculator.calculateLunarPhase(checkTime, latitude, longitude);
                        
                        if (goodNakshatras.contains(nakInfo.getNakshatraNumber()) && goodTithis.contains(lunarPhase.getTithi())) {
                            // Check Lagna
                            int lagna = getAscendantSign(checkTime, latitude, longitude);
                            if (fixedLagnas.contains(lagna)) {
                                addOrMergeSlot(slots, slotStart, slotEnd, getLocalizedMessage("muhurta.quality.best", "Best"), 
                                    Arrays.asList(
                                        getLocalizedMessage("panchang.nakshatra", "Nakshatra") + ": " + getNakshatraName(nakInfo.getNakshatraNumber()), 
                                        getLocalizedMessage("panchang.tithi", "Tithi") + ": " + getTithiName(lunarPhase.getTithi()), 
                                        getLocalizedMessage("chart.ascendant", "Lagna") + ": " + getRashiName(lagna)
                                    ));
                            } else {
                                addOrMergeSlot(slots, slotStart, slotEnd, getLocalizedMessage("muhurta.quality.good", "Good"), 
                                    Arrays.asList(
                                        getLocalizedMessage("panchang.nakshatra", "Nakshatra") + ": " + getNakshatraName(nakInfo.getNakshatraNumber()), 
                                        getLocalizedMessage("panchang.tithi", "Tithi") + ": " + getTithiName(lunarPhase.getTithi()), 
                                        getLocalizedMessage("chart.ascendant", "Lagna") + ": " + getRashiName(lagna)
                                    ));
                            }
                        }
                    }
                }
            }
            current = current.plusMinutes(30);
        }
        return slots;
    }

    @Override
    public List<MuhurtaSlot> findNewBusinessMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId) {
        List<MuhurtaSlot> slots = new ArrayList<>();
        LocalDateTime current = start;
        
        // Business Criteria:
        // Nakshatras: Ashwini(1), Pushya(8), Hasta(13), Chitra(14), Anuradha(17), Revati(27)
        List<Integer> goodNakshatras = Arrays.asList(1, 8, 13, 14, 17, 27);
        // Days: Wed, Thu, Fri
        List<DayOfWeek> goodDays = Arrays.asList(DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        
        while (current.isBefore(end)) {
            if (goodDays.contains(current.getDayOfWeek())) {
                LocalDateTime slotStart = current;
                LocalDateTime slotEnd = current.plusMinutes(30);
                LocalDateTime checkTime = slotStart.plusMinutes(15);
                
                MuhurtaResult mr = calculateMuhurta(checkTime.toLocalDate(), latitude, longitude, zoneId);
                if (!isTimeInPeriod(checkTime, mr.getInauspiciousTimes().getRahuKalam())) {
                    SwissEphCalculator.NakshatraInfo nakInfo = SwissEphCalculator.calculateNakshatra(checkTime, latitude, longitude);
                    
                    if (goodNakshatras.contains(nakInfo.getNakshatraNumber())) {
                        addOrMergeSlot(slots, slotStart, slotEnd, getLocalizedMessage("muhurta.quality.good", "Good"), 
                            Arrays.asList(getLocalizedMessage("panchang.nakshatra", "Nakshatra") + ": " + getNakshatraName(nakInfo.getNakshatraNumber())));
                    }
                }
            }
            current = current.plusMinutes(30);
        }
        return slots;
    }

    @Override
    public List<MuhurtaSlot> findNamakaranaMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId) {
        List<MuhurtaSlot> slots = new ArrayList<>();
        LocalDateTime current = start;
        
        // Namakarana Criteria:
        // Nakshatras: Ashwini(1), Rohini(4), Mrigashira(5), Punarvasu(7), Pushya(8), Hasta(13), Swati(15), Anuradha(17), Shravana(22), Revati(27)
        List<Integer> goodNakshatras = Arrays.asList(1, 4, 5, 7, 8, 13, 15, 17, 22, 27);
        // Tithis: 1, 2, 3, 5, 7, 10, 11, 12, 13
        List<Integer> goodTithis = Arrays.asList(1, 2, 3, 5, 7, 10, 11, 12, 13);
        // Days: Mon, Wed, Thu, Fri
        List<DayOfWeek> goodDays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        
        while (current.isBefore(end)) {
            if (goodDays.contains(current.getDayOfWeek())) {
                LocalDateTime slotStart = current;
                LocalDateTime slotEnd = current.plusMinutes(30);
                LocalDateTime checkTime = slotStart.plusMinutes(15);
                
                MuhurtaResult mr = calculateMuhurta(checkTime.toLocalDate(), latitude, longitude, zoneId);
                if (!isTimeInPeriod(checkTime, mr.getInauspiciousTimes().getRahuKalam())) {
                    SwissEphCalculator.NakshatraInfo nakInfo = SwissEphCalculator.calculateNakshatra(checkTime, latitude, longitude);
                    SwissEphCalculator.LunarPhase lunarPhase = SwissEphCalculator.calculateLunarPhase(checkTime, latitude, longitude);
                    
                    if (goodNakshatras.contains(nakInfo.getNakshatraNumber()) && goodTithis.contains(lunarPhase.getTithi())) {
                        addOrMergeSlot(slots, slotStart, slotEnd, getLocalizedMessage("muhurta.quality.good", "Good"), 
                            Arrays.asList(
                                getLocalizedMessage("panchang.nakshatra", "Nakshatra") + ": " + getNakshatraName(nakInfo.getNakshatraNumber()), 
                                getLocalizedMessage("panchang.tithi", "Tithi") + ": " + getTithiName(lunarPhase.getTithi())
                            ));
                    }
                }
            }
            current = current.plusMinutes(30);
        }
        return slots;
    }

    @Override
    public List<MuhurtaSlot> findPropertyPurchaseMuhurta(LocalDateTime start, LocalDateTime end, double latitude, double longitude, ZoneId zoneId) {
        List<MuhurtaSlot> slots = new ArrayList<>();
        LocalDateTime current = start;
        
        // Property Purchase Criteria:
        // Nakshatras: Mrigashira(5), Punarvasu(7), Ashlesha(9), Magha(10), Purva Phalguni(11), Vishakha(16), Moola(19), Revati(27)
        List<Integer> goodNakshatras = Arrays.asList(5, 7, 9, 10, 11, 16, 19, 27);
        // Days: Thu, Fri
        List<DayOfWeek> goodDays = Arrays.asList(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        
        while (current.isBefore(end)) {
            if (goodDays.contains(current.getDayOfWeek())) {
                LocalDateTime slotStart = current;
                LocalDateTime slotEnd = current.plusMinutes(30);
                LocalDateTime checkTime = slotStart.plusMinutes(15);
                
                MuhurtaResult mr = calculateMuhurta(checkTime.toLocalDate(), latitude, longitude, zoneId);
                if (!isTimeInPeriod(checkTime, mr.getInauspiciousTimes().getRahuKalam())) {
                    SwissEphCalculator.NakshatraInfo nakInfo = SwissEphCalculator.calculateNakshatra(checkTime, latitude, longitude);
                    
                    if (goodNakshatras.contains(nakInfo.getNakshatraNumber())) {
                        addOrMergeSlot(slots, slotStart, slotEnd, getLocalizedMessage("muhurta.quality.good", "Good"), 
                            Arrays.asList(getLocalizedMessage("panchang.nakshatra", "Nakshatra") + ": " + getNakshatraName(nakInfo.getNakshatraNumber())));
                    }
                }
            }
            current = current.plusMinutes(30);
        }
        return slots;
    }
    
    private int getPlanetSign(LocalDateTime time, double lat, double lon, String planet) {
        SwissEphCalculator.PlanetaryPosition pos = SwissEphCalculator.calculatePlanetPosition(time, lat, lon, planet);
        return (int) Math.floor(pos.getLongitude() / 30) + 1;
    }
    
    private int getAscendantSign(LocalDateTime time, double lat, double lon) {
        double asc = SwissEphCalculator.calculateAscendant(time, lat, lon);
        return (int) Math.floor(asc / 30) + 1;
    }
    
    private String getNakshatraName(int number) {
        return getLocalizedMessage("nakshatra." + number, "Nakshatra " + number);
    }
    
    private String getTithiName(int number) {
        return getLocalizedMessage("tithi." + number, "Tithi " + number);
    }
    
    private String getRashiName(int number) {
        if (number >= 1 && number <= 12) {
            return getLocalizedMessage(RASHI_KEYS[number - 1], "Rashi " + number);
        }
        return "Rashi " + number;
    }
}
