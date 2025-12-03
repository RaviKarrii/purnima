package com.example.purnima.api;

import com.example.purnima.model.BirthData;
import com.example.purnima.model.DasaResult;
import java.util.List;

/**
 * Interface for Dasa (Planetary Period) calculations.
 */
public interface DasaCalculator {
    
    /**
     * Calculate Mahadasas for a given birth data.
     * 
     * @param birthData The birth data
     * @return List of Mahadasas
     */
    List<DasaResult> calculateMahadasas(BirthData birthData);
    
    /**
     * Calculate current Dasa period (Mahadasa, Antardasa, Pratyantardasa) for a given time.
     * 
     * @param birthData The birth data
     * @return The current Dasa period hierarchy
     */
    DasaResult getCurrentDasa(BirthData birthData);
}
