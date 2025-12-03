package com.example.purnima.service;

import com.example.purnima.api.DasaCalculator;
import com.example.purnima.model.BirthData;
import com.example.purnima.model.DasaResult;
import com.example.purnima.util.SwissEphCalculator;
import com.example.purnima.util.SwissEphCalculator.NakshatraInfo;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of Vimshottari Dasa system (120 years cycle).
 */
public class VimshottariDasaCalculator implements DasaCalculator {

    private final MessageSource messageSource;

    // Planet order in Vimshottari Dasa: Ketu, Venus, Sun, Moon, Mars, Rahu, Jupiter, Saturn, Mercury
    // Keys for localization
    private static final String[] DASA_PLANET_KEYS = {
        "planet.ketu", "planet.venus", "planet.sun", "planet.moon", "planet.mars", "planet.rahu", "planet.jupiter", "planet.saturn", "planet.mercury"
    };

    // Duration of each Mahadasa in years
    private static final int[] DASA_YEARS = {
        7, 20, 6, 10, 7, 18, 16, 19, 17
    };
    
    // Total cycle duration
    private static final int TOTAL_DASA_YEARS = 120;

    public VimshottariDasaCalculator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Default constructor for backward compatibility or testing without i18n
    public VimshottariDasaCalculator() {
        this.messageSource = null;
    }

    private String getLocalizedPlanetName(int index) {
        if (messageSource == null) {
            // Fallback to English names if no MessageSource
            String[] englishNames = {"Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter", "Saturn", "Mercury"};
            return englishNames[index];
        }
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(DASA_PLANET_KEYS[index], null, locale);
    }
    
    // Helper to get index from localized or English name (reverse lookup is tricky with i18n)
    // Actually, we should store the internal key or index in DasaResult, but DasaResult takes a String planet name.
    // For calculation logic, we need to know the planet index.
    // Issue: If we store localized name in DasaResult, we can't easily get the index back for sub-period generation if we rely on name.
    // Solution: We should pass the index or key to generation methods, not rely on parsing the name from DasaResult.
    // Or, we can store the key in DasaResult? DasaResult is a simple model.
    // Let's modify generation methods to take planet index.

    @Override
    public List<DasaResult> calculateMahadasas(BirthData birthData) {
        return calculateMahadasas(birthData, false);
    }
    
    public List<DasaResult> calculateMahadasas(BirthData birthData, boolean includeSignificance) {
        // 1. Calculate Moon's Nakshatra and position
        // Using SwissEphCalculator directly for moon longitude and nakshatra info
        NakshatraInfo nakshatraInfo = SwissEphCalculator.calculateNakshatra(
            birthData.getBirthDateTime(), 
            birthData.getLatitude(), 
            birthData.getLongitude()
        );
        
        // 2. Determine ruling planet and balance of Dasa
        int nakshatraNumber = nakshatraInfo.getNakshatraNumber(); // 1 to 27
        // Nakshatra index 0 to 26
        int nakshatraIndex = nakshatraNumber - 1; 
        
        // Determine the lord of the nakshatra based on the standard Vimshottari sequence
        // Nakshatra lords in order: Ketu, Venus, Sun, Moon, Mars, Rahu, Jupiter, Saturn, Mercury
        // This sequence repeats every 9 nakshatras.
        // So, nakshatra 1 (Aswini) is ruled by Ketu, 2 (Bharani) by Venus, 3 (Krittika) by Sun, etc.
        // The DASA_PLANET_KEYS array is already in this order.
        int planetIndexInDasaOrder = nakshatraIndex % 9; // This directly gives the index for DASA_PLANET_KEYS
        
        // 3. Calculate balance of Dasa
        double degreeInNakshatra = nakshatraInfo.getDegreeInNakshatra(); // Degrees traversed in current nakshatra
        double totalNakshatraDuration = 13.333333333; // 13 degrees 20 minutes
        
        double fractionElapsed = degreeInNakshatra / totalNakshatraDuration;
        double fractionRemaining = 1.0 - fractionElapsed;
        
        // Years remaining in current Mahadasa
        double totalDasaYearsForPlanet = DASA_YEARS[planetIndexInDasaOrder];
        double yearsRemaining = totalDasaYearsForPlanet * fractionRemaining;
        
        // 4. Generate Mahadasas
        List<DasaResult> mahadasas = new ArrayList<>();
        LocalDateTime currentStartDate = birthData.getBirthDateTime();
        
        // First Mahadasa (partial)
        LocalDateTime firstEndDate = addYears(currentStartDate, yearsRemaining);
        DasaResult firstDasa = new DasaResult(getLocalizedPlanetName(planetIndexInDasaOrder), currentStartDate, firstEndDate, 1);
        if (includeSignificance) {
            firstDasa.setSignificance(getDasaSignificance(planetIndexInDasaOrder));
        }
        
        // For the first Mahadasa, its theoretical start was `totalDasaYearsForPlanet` years before its end.
        LocalDateTime theoreticalFirstDasaStart = addYears(firstEndDate, -totalDasaYearsForPlanet);
        List<DasaResult> firstSubDasas = generateAntardasas(planetIndexInDasaOrder, theoreticalFirstDasaStart, firstEndDate, includeSignificance);
        
        // Filter sub-dasas to only show those active after birth
        List<DasaResult> activeSubDasas = new ArrayList<>();
        for (DasaResult sub : firstSubDasas) {
            if (sub.getEndDate().isAfter(birthData.getBirthDateTime())) {
                // Adjust start if it's before birth
                if (sub.getStartDate().isBefore(birthData.getBirthDateTime())) {
                    sub.setStartDate(birthData.getBirthDateTime());
                }
                activeSubDasas.add(sub);
            }
        }
        firstDasa.setSubDasas(activeSubDasas);
        mahadasas.add(firstDasa);
        
        currentStartDate = firstEndDate;
        
        // Subsequent Mahadasas
        int currentPlanetIndex = (planetIndexInDasaOrder + 1) % 9;
        
        // Generate subsequent Mahadasas until 120 years from birth or a reasonable future date
        // The original code used `while (ChronoUnit.YEARS.between(birthDate, currentStartDate) < 120)`
        // Let's ensure we generate at least a full 120-year cycle from birth.
        LocalDateTime cycleEndDate = addYears(birthData.getBirthDateTime(), TOTAL_DASA_YEARS);

        while (currentStartDate.isBefore(cycleEndDate) || mahadasas.size() < 9) { // Ensure at least 9 mahadasas for a full cycle
            int duration = DASA_YEARS[currentPlanetIndex];
            LocalDateTime endDate = addYears(currentStartDate, (double) duration);
            
            // Cap the end date to the 120-year cycle end if it goes beyond
            if (endDate.isAfter(cycleEndDate)) {
                endDate = cycleEndDate;
            }

            DasaResult dasa = new DasaResult(getLocalizedPlanetName(currentPlanetIndex), currentStartDate, endDate, 1);
            if (includeSignificance) {
                dasa.setSignificance(getDasaSignificance(currentPlanetIndex));
            }
            dasa.setSubDasas(generateAntardasas(currentPlanetIndex, currentStartDate, endDate, includeSignificance));
            mahadasas.add(dasa);
            
            currentStartDate = endDate;
            currentPlanetIndex = (currentPlanetIndex + 1) % 9;

            if (currentStartDate.isEqual(cycleEndDate)) break; // Stop if we hit the 120-year mark
        }
        
        return mahadasas;
    }

    @Override
    public DasaResult getCurrentDasa(BirthData birthData) {
        return getCurrentDasa(birthData, LocalDateTime.now(), false);
    }
    
    public DasaResult getCurrentDasa(BirthData birthData, LocalDateTime targetDate, boolean includeSignificance) {
        List<DasaResult> mahadasas = calculateMahadasas(birthData, includeSignificance);
        
        for (DasaResult md : mahadasas) {
            if (!targetDate.isBefore(md.getStartDate()) && !targetDate.isAfter(md.getEndDate())) {
                // Found Mahadasa
                // Now find Antardasa
                if (md.getSubDasas() != null) {
                    for (DasaResult ad : md.getSubDasas()) {
                        if (!targetDate.isBefore(ad.getStartDate()) && !targetDate.isAfter(ad.getEndDate())) {
                            // Found Antardasa
                            
                            // Let's generate PD for this AD
                            int mdPlanetIndex = getPlanetIndexFromLocalizedName(md.getPlanet());
                            int adPlanetIndex = getPlanetIndexFromLocalizedName(ad.getPlanet());
                            
                            List<DasaResult> pratyantardasas = generatePratyantardasas(mdPlanetIndex, adPlanetIndex, ad.getStartDate(), ad.getEndDate(), includeSignificance);
                            ad.setSubDasas(pratyantardasas);
                            
                            // Find current PD
                            for (DasaResult pd : pratyantardasas) {
                                if (!targetDate.isBefore(pd.getStartDate()) && !targetDate.isAfter(pd.getEndDate())) {
                                    // Found PD. Return MD -> AD -> PD hierarchy
                                    DasaResult result = new DasaResult(md.getPlanet(), md.getStartDate(), md.getEndDate(), 1);
                                    result.setSignificance(md.getSignificance());
                                    
                                    DasaResult subResult = new DasaResult(ad.getPlanet(), ad.getStartDate(), ad.getEndDate(), 2);
                                    subResult.setSignificance(ad.getSignificance());
                                    
                                    subResult.setSubDasas(new ArrayList<>(List.of(pd)));
                                    result.setSubDasas(new ArrayList<>(List.of(subResult)));
                                    return result;
                                }
                            }
                            // If no PD found (shouldn't happen if logic is correct, but as fallback)
                            return ad; 
                        }
                    }
                }
                return md;
            }
        }
        return null;
    }
    
    private int getPlanetIndexFromLocalizedName(String name) {
        for (int i = 0; i < DASA_PLANET_KEYS.length; i++) {
            if (getLocalizedPlanetName(i).equals(name)) {
                return i;
            }
        }
        return -1; // Should not happen if names are consistent
    }
    
    // Helper to add fractional years
    private LocalDateTime addYears(LocalDateTime date, double years) {
        long fullYears = (long) years;
        double fraction = years - fullYears;
        // Using 365.2425 for average year length (Gregorian calendar)
        long extraDays = (long) (fraction * 365.2425);
        
        return date.plusYears(fullYears).plusDays(extraDays);
    }
    
    private boolean isDateWithin(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return (target.isEqual(start) || target.isAfter(start)) && target.isBefore(end);
    }
    
    private List<DasaResult> generateAntardasas(int mahadasaLordIndex, LocalDateTime start, LocalDateTime end, boolean includeSignificance) {
        List<DasaResult> antardasas = new ArrayList<>();
        double totalMahadasaYears = DASA_YEARS[mahadasaLordIndex];
        
        LocalDateTime current = start;
        
        // Antardasa sequence starts from the Mahadasa lord itself
        for (int i = 0; i < 9; i++) {
            int subLordIndex = (mahadasaLordIndex + i) % 9;
            double subYears = DASA_YEARS[subLordIndex];
            
            // Formula: (Mahadasa Years * Antardasa Planet Years) / 120 = Antardasa Duration in Years
            double durationYears = (totalMahadasaYears * subYears) / TOTAL_DASA_YEARS;
            
            LocalDateTime subEnd = addYears(current, durationYears);
            
            // Ensure the last antardasa ends exactly at the mahadasa end
            if (i == 8) {
                subEnd = end;
            }
            
            DasaResult ad = new DasaResult(
                getLocalizedPlanetName(subLordIndex),
                current,
                subEnd,
                2
            );
            if (includeSignificance) {
                ad.setSignificance(getDasaSignificance(subLordIndex));
            }
            antardasas.add(ad);
            current = subEnd;
        }
        return antardasas;
    }
    
    private List<DasaResult> generatePratyantardasas(int mdLordIndex, int adLordIndex, LocalDateTime start, LocalDateTime end, boolean includeSignificance) {
        List<DasaResult> pds = new ArrayList<>();
        double mdYears = DASA_YEARS[mdLordIndex];
        double adYears = DASA_YEARS[adLordIndex];
        
        LocalDateTime current = start;
        
        // Pratyantardasa sequence starts from the Antardasa lord itself
        for (int i = 0; i < 9; i++) {
            int pdLordIndex = (adLordIndex + i) % 9;
            double pdYears = DASA_YEARS[pdLordIndex];
            
            // Formula: (MD Years * AD Years * PD Years) / (120 * 120) = Pratyantardasa Duration in Years
            double pdDurationYears = (mdYears * adYears * pdYears) / (TOTAL_DASA_YEARS * TOTAL_DASA_YEARS);
            
            LocalDateTime subEnd = addYears(current, pdDurationYears);
            
            // Ensure the last pratyantardasa ends exactly at the antardasa end
            if (i == 8) {
                subEnd = end;
            }
            
            DasaResult pd = new DasaResult(
                getLocalizedPlanetName(pdLordIndex),
                current,
                subEnd,
                3
            );
            if (includeSignificance) {
                pd.setSignificance(getDasaSignificance(pdLordIndex));
            }
            pds.add(pd);
            current = subEnd;
        }
        return pds;
    }
    
    private String getDasaSignificance(int planetIndex) {
        String key = "dasa.significance." + DASA_PLANET_KEYS[planetIndex].replace("planet.", "");
        return getLocalizedMessage(key, "Significance not available");
    }

    private String getLocalizedMessage(String key, String defaultMessage) {
        if (messageSource == null) {
            return defaultMessage;
        }
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, defaultMessage, locale);
    }
}
