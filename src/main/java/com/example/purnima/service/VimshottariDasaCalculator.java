package com.example.purnima.service;

import com.example.purnima.api.DasaCalculator;
import com.example.purnima.model.BirthData;
import com.example.purnima.model.DasaResult;
import com.example.purnima.util.SwissEphCalculator;
import com.example.purnima.util.SwissEphCalculator.NakshatraInfo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of Vimshottari Dasa system (120 years cycle).
 */
public class VimshottariDasaCalculator implements DasaCalculator {

    // Planet order in Vimshottari Dasa: Ketu, Venus, Sun, Moon, Mars, Rahu, Jupiter, Saturn, Mercury
    private static final String[] DASA_PLANETS = {
        "Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter", "Saturn", "Mercury"
    };

    // Duration of each Mahadasa in years
    private static final int[] DASA_YEARS = {
        7, 20, 6, 10, 7, 18, 16, 19, 17
    };
    
    // Total cycle duration
    private static final int TOTAL_DASA_YEARS = 120;

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
        // Nakshatras 1-9 start with Ketu, 10-18 with Ketu, 19-27 with Ketu? No.
        // The sequence repeats every 9 Nakshatras.
        // 1 (Ashwini) -> Ketu
        // 2 (Bharani) -> Venus
        // ...
        // So (Nakshatra - 1) % 9 gives the index in DASA_PLANETS
        
        int nakshatraIndex = nakshatraInfo.getNakshatraNumber() - 1;
        int planetIndex = nakshatraIndex % 9;
        
        // 3. Calculate balance of Dasa
        // Degree in Nakshatra (0 to 13.3333)
        // Fraction elapsed = degreeInNakshatra / 13.3333
        // Fraction remaining = 1 - Fraction elapsed
        
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
        DasaResult firstDasa = new DasaResult(DASA_PLANETS[planetIndex], currentStartDate, firstEndDate, 1);
        // Generate Antardasas for the first Mahadasa
        // Note: For the first Mahadasa, we need to find the correct starting Antardasa based on the balance
        // But typically, Dasa listings show the full structure. 
        // However, the "balance" means we are starting in the middle of a Mahadasa.
        // For simplicity in this list, we'll show the actual start/end dates from birth.
        // A more detailed view would show the Antardasa running at birth.
        generateAntardasas(firstDasa, DASA_YEARS[planetIndex]);
        mahadasas.add(firstDasa);
        
        currentStartDate = firstEndDate;
        
        // Subsequent Mahadasas
        // We generate for 120 years from birth, or maybe just one full cycle?
        // Usually people want to see up to 100-120 years of life.
        
        int currentPlanetIndex = (planetIndex + 1) % 9;
        LocalDateTime birthDate = birthData.getBirthDateTime();
        
        while (ChronoUnit.YEARS.between(birthDate, currentStartDate) < 120) {
            int duration = DASA_YEARS[currentPlanetIndex];
            LocalDateTime endDate = addYears(currentStartDate, (double) duration);
            
            DasaResult dasa = new DasaResult(DASA_PLANETS[currentPlanetIndex], currentStartDate, endDate, 1);
            generateAntardasas(dasa, duration);
            mahadasas.add(dasa);
            
            currentStartDate = endDate;
            currentPlanetIndex = (currentPlanetIndex + 1) % 9;
        }
        
        return mahadasas;
    }

    @Override
    public DasaResult getCurrentDasa(BirthData birthData) {
        List<DasaResult> mahadasas = calculateMahadasas(birthData);
        LocalDateTime now = LocalDateTime.now(); // Or use a passed time if needed, but interface says getCurrentDasa(BirthData) implies "now" relative to system time? 
        // Actually, usually "current" means for a specific date. The interface signature I defined is getCurrentDasa(BirthData).
        // Let's assume "now".
        
        for (DasaResult md : mahadasas) {
            if (isDateWithin(now, md.getStartDate(), md.getEndDate())) {
                // Found Mahadasa, now find Antardasa
                for (DasaResult ad : md.getSubPeriods()) {
                    if (isDateWithin(now, ad.getStartDate(), ad.getEndDate())) {
                        // Found Antardasa, now find Pratyantardasa
                        // We need to generate Pratyantardasas for this Antardasa on the fly if not already
                        if (ad.getSubPeriods().isEmpty()) {
                            // Find the planet index for this Antardasa
                            int mdPlanetIndex = getPlanetIndex(md.getPlanet());
                            int adPlanetIndex = getPlanetIndex(ad.getPlanet());
                            // Duration of Antardasa in months = DasaYears(MD) * DasaYears(AD) / 10
                            // Actually, let's just reuse a generation method
                            generatePratyantardasas(ad, DASA_YEARS[mdPlanetIndex], DASA_YEARS[adPlanetIndex]);
                        }
                        
                        for (DasaResult pd : ad.getSubPeriods()) {
                            if (isDateWithin(now, pd.getStartDate(), pd.getEndDate())) {
                                return pd; // Return the deepest level found
                            }
                        }
                        return ad; // Fallback
                    }
                }
                return md; // Fallback
            }
        }
        return null;
    }
    
    // Helper to add fractional years
    private LocalDateTime addYears(LocalDateTime date, double years) {
        long days = (long) (years * 365.2425); // Using Gregorian average year
        // Or more precisely:
        long fullYears = (long) years;
        double fraction = years - fullYears;
        long extraDays = (long) (fraction * 365.2425);
        
        return date.plusYears(fullYears).plusDays(extraDays);
    }
    
    private boolean isDateWithin(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return (target.isEqual(start) || target.isAfter(start)) && target.isBefore(end);
    }
    
    private int getPlanetIndex(String planet) {
        for (int i = 0; i < DASA_PLANETS.length; i++) {
            if (DASA_PLANETS[i].equals(planet)) return i;
        }
        return -1;
    }
    
    private void generateAntardasas(DasaResult mahadasa, int mahadasaYears) {
        // Antardasa sequence starts from the Mahadasa lord itself
        int startPlanetIndex = getPlanetIndex(mahadasa.getPlanet());
        LocalDateTime currentStart = mahadasa.getStartDate();
        
        // However, if this is the first Mahadasa of life (with balance), 
        // the start date of the Mahadasa is birth date, but the Antardasa cycle 
        // might be starting in the middle.
        // This is complex. The standard way is:
        // Calculate full dates for all Antardasas as if the Mahadasa started fully.
        // Then clip them to the birth date.
        
        // Let's refine the logic for the first Mahadasa.
        // We know the END date of the Mahadasa.
        // We can work backwards or calculate the full theoretical start date.
        
        // Theoretical start of this Mahadasa = EndDate - MahadasaYears
        LocalDateTime theoreticalStart = addYears(mahadasa.getEndDate(), -mahadasaYears);
        
        LocalDateTime adStart = theoreticalStart;
        
        for (int i = 0; i < 9; i++) {
            int planetIdx = (startPlanetIndex + i) % 9;
            int adYears = DASA_YEARS[planetIdx];
            
            // Formula: Antardasa months = MahadasaYears * AntardasaYears / 10
            // Example: Sun (6) * Sun (6) / 10 = 3.6 months
            
            double months = (double) mahadasaYears * adYears / 10.0;
            // Convert months to years for addYears helper? 
            // Or just add months? 3.6 months is tricky with standard plusMonths.
            // Better to use fraction of years. 
            // months / 12 = years
            double adDurationYears = months / 12.0;
            
            LocalDateTime adEnd = addYears(adStart, adDurationYears);
            
            // Only add if it overlaps with the actual Mahadasa period (Birth to End)
            if (adEnd.isAfter(mahadasa.getStartDate())) {
                LocalDateTime effectiveStart = adStart.isBefore(mahadasa.getStartDate()) ? mahadasa.getStartDate() : adStart;
                DasaResult ad = new DasaResult(DASA_PLANETS[planetIdx], effectiveStart, adEnd, 2);
                mahadasa.addSubPeriod(ad);
            }
            
            adStart = adEnd;
        }
    }

    private void generatePratyantardasas(DasaResult antardasa, int mahadasaYears, int antardasaYears) {
        int startPlanetIndex = getPlanetIndex(antardasa.getPlanet());
        
        // Similar logic for theoretical start
        // PD duration = MD * AD * PD / 1000 (roughly? No, let's check formula)
        // Standard: MD years * AD years * PD years / 40 / 3 = days? No.
        // Formula: 
        // AD = MD * AD / 120 of total cycle? No.
        // Simple rule: Proportion is always PlanetYears / 120.
        // So AD duration = MD_Duration * (AD_Years / 120)
        // PD duration = AD_Duration * (PD_Years / 120)
        
        // Let's verify AD duration:
        // MD_Duration (years) = MD_Years
        // AD_Duration = MD_Years * (AD_Years / 120) = MD_Years * AD_Years / 120
        // My previous formula: MD * AD / 10 months.
        // MD * AD / 10 months = MD * AD / 120 years. (Since 120 months = 10 years? No. 10 * 12 = 120).
        // Yes, MD * AD / 120 years = (MD * AD / 10) / 12 years = MD * AD / 10 months. Correct.
        
        // So PD Duration = AD_Duration_Years * (PD_Years / 120)
        
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
                DasaResult pd = new DasaResult(DASA_PLANETS[planetIdx], effectiveStart, pdEnd, 3);
                antardasa.addSubPeriod(pd);
            }
            
            pdStart = pdEnd;
        }
    }
}
