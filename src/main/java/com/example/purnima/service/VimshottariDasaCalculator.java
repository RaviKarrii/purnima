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
        List<DasaResult> mahadasas = new ArrayList<>();
        
        // 1. Calculate Moon's Nakshatra
        NakshatraInfo nakshatraInfo = SwissEphCalculator.calculateNakshatra(
            birthData.getBirthDateTime(), 
            birthData.getLatitude(), 
            birthData.getLongitude()
        );
        
        // 2. Determine ruling planet and starting Dasa
        int nakshatraIndex = nakshatraInfo.getNakshatraNumber() - 1;
        int planetIndex = nakshatraIndex % 9;
        
        // 3. Calculate balance of Dasa
        double degreeInNakshatra = nakshatraInfo.getDegreeInNakshatra();
        double totalNakshatraDuration = 13.333333333;
        double fractionElapsed = degreeInNakshatra / totalNakshatraDuration;
        double fractionRemaining = 1.0 - fractionElapsed;
        
        // Years remaining in current Mahadasa
        double yearsRemaining = DASA_YEARS[planetIndex] * fractionRemaining;
        
        // 4. Generate Mahadasas
        LocalDateTime currentStartDate = birthData.getBirthDateTime();
        
        // First Mahadasa (partial)
        LocalDateTime firstEndDate = addYears(currentStartDate, yearsRemaining);
        DasaResult firstDasa = new DasaResult(getLocalizedPlanetName(planetIndex), currentStartDate, firstEndDate, 1);
        generateAntardasas(firstDasa, planetIndex, DASA_YEARS[planetIndex]);
        mahadasas.add(firstDasa);
        
        currentStartDate = firstEndDate;
        
        // Subsequent Mahadasas
        int currentPlanetIndex = (planetIndex + 1) % 9;
        LocalDateTime birthDate = birthData.getBirthDateTime();
        
        while (ChronoUnit.YEARS.between(birthDate, currentStartDate) < 120) {
            int duration = DASA_YEARS[currentPlanetIndex];
            LocalDateTime endDate = addYears(currentStartDate, (double) duration);
            
            DasaResult dasa = new DasaResult(getLocalizedPlanetName(currentPlanetIndex), currentStartDate, endDate, 1);
            generateAntardasas(dasa, currentPlanetIndex, duration);
            mahadasas.add(dasa);
            
            currentStartDate = endDate;
            currentPlanetIndex = (currentPlanetIndex + 1) % 9;
        }
        
        return mahadasas;
    }

    @Override
    public DasaResult getCurrentDasa(BirthData birthData) {
        List<DasaResult> mahadasas = calculateMahadasas(birthData);
        LocalDateTime now = LocalDateTime.now(); 
        
        for (DasaResult md : mahadasas) {
            if (isDateWithin(now, md.getStartDate(), md.getEndDate())) {
                for (DasaResult ad : md.getSubPeriods()) {
                    if (isDateWithin(now, ad.getStartDate(), ad.getEndDate())) {
                        // Check if sub-periods exist (they should be generated by calculateMahadasas -> generateAntardasas)
                        // But Pratyantardasas are NOT generated by default in calculateMahadasas to save space?
                        // Wait, my previous implementation of generateAntardasas did NOT generate PDs.
                        // So ad.getSubPeriods() is empty.
                        
                        // We need to find the planet indices to generate PDs.
                        // Since DasaResult only has the localized name, we have a problem if we don't know the index.
                        // BUT, we can iterate to find the index that matches the localized name?
                        // Or better, since we are inside the loop, we can't easily know the index of 'md' and 'ad' without looking them up.
                        
                        // Let's implement a lookup helper.
                        int mdPlanetIndex = getPlanetIndexFromLocalizedName(md.getPlanet());
                        int adPlanetIndex = getPlanetIndexFromLocalizedName(ad.getPlanet());
                        
                        if (mdPlanetIndex != -1 && adPlanetIndex != -1) {
                             generatePratyantardasas(ad, mdPlanetIndex, adPlanetIndex);
                             
                             for (DasaResult pd : ad.getSubPeriods()) {
                                if (isDateWithin(now, pd.getStartDate(), pd.getEndDate())) {
                                    return pd;
                                }
                            }
                        }
                        return ad;
                    }
                }
                return md;
            }
        }
        return null;
    }
    
    private int getPlanetIndexFromLocalizedName(String name) {
        for (int i = 0; i < 9; i++) {
            if (getLocalizedPlanetName(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }
    
    // Helper to add fractional years
    private LocalDateTime addYears(LocalDateTime date, double years) {
        long fullYears = (long) years;
        double fraction = years - fullYears;
        long extraDays = (long) (fraction * 365.2425);
        
        return date.plusYears(fullYears).plusDays(extraDays);
    }
    
    private boolean isDateWithin(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return (target.isEqual(start) || target.isAfter(start)) && target.isBefore(end);
    }
    
    private void generateAntardasas(DasaResult mahadasa, int mahadasaPlanetIndex, int mahadasaYears) {
        // Antardasa sequence starts from the Mahadasa lord itself
        int startPlanetIndex = mahadasaPlanetIndex;
        
        // Theoretical start of this Mahadasa = EndDate - MahadasaYears
        LocalDateTime theoreticalStart = addYears(mahadasa.getEndDate(), -mahadasaYears);
        LocalDateTime adStart = theoreticalStart;
        
        for (int i = 0; i < 9; i++) {
            int planetIdx = (startPlanetIndex + i) % 9;
            int adYears = DASA_YEARS[planetIdx];
            
            double months = (double) mahadasaYears * adYears / 10.0;
            double adDurationYears = months / 12.0;
            
            LocalDateTime adEnd = addYears(adStart, adDurationYears);
            
            if (adEnd.isAfter(mahadasa.getStartDate())) {
                LocalDateTime effectiveStart = adStart.isBefore(mahadasa.getStartDate()) ? mahadasa.getStartDate() : adStart;
                DasaResult ad = new DasaResult(getLocalizedPlanetName(planetIdx), effectiveStart, adEnd, 2);
                mahadasa.addSubPeriod(ad);
            }
            
            adStart = adEnd;
        }
    }

    private void generatePratyantardasas(DasaResult antardasa, int mahadasaPlanetIndex, int antardasaPlanetIndex) {
        int startPlanetIndex = antardasaPlanetIndex;
        int mahadasaYears = DASA_YEARS[mahadasaPlanetIndex];
        int antardasaYears = DASA_YEARS[antardasaPlanetIndex];
        
        double adDurationYears = (double) mahadasaYears * antardasaYears / 120.0;
        LocalDateTime theoreticalStart = addYears(antardasa.getEndDate(), -adDurationYears);
        LocalDateTime pdStart = theoreticalStart;
        
        for (int i = 0; i < 9; i++) {
            int planetIdx = (startPlanetIndex + i) % 9;
            int pdYears = DASA_YEARS[planetIdx];
            
            double pdDurationYears = adDurationYears * (pdYears / 120.0);
            
            LocalDateTime pdEnd = addYears(pdStart, pdDurationYears);
            
             if (pdEnd.isAfter(antardasa.getStartDate())) {
                LocalDateTime effectiveStart = pdStart.isBefore(antardasa.getStartDate()) ? antardasa.getStartDate() : pdStart;
                DasaResult pd = new DasaResult(getLocalizedPlanetName(planetIdx), effectiveStart, pdEnd, 3);
                antardasa.addSubPeriod(pd);
            }
            
            pdStart = pdEnd;
        }
    }
}
