package com.example.purnima.api;

import com.example.purnima.model.BirthData;
import com.example.purnima.model.AsthakootResult;

/**
 * Interface for calculating Asthakoot (eight-fold compatibility) between two birth charts.
 * Asthakoot is a traditional Vedic astrology method for assessing marriage compatibility.
 */
public interface AsthakootCalculator {
    
    /**
     * Calculate the complete Asthakoot compatibility between two individuals.
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return AsthakootResult containing all eight kootas and total score
     */
    AsthakootResult calculateAsthakoot(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Calculate Varna Koota (1 point) - Caste compatibility
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return Score out of 1
     */
    int calculateVarnaKoota(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Calculate Vashya Koota (2 points) - Control compatibility
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return Score out of 2
     */
    int calculateVashyaKoota(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Calculate Tara Koota (3 points) - Star compatibility
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return Score out of 3
     */
    int calculateTaraKoota(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Calculate Yoni Koota (4 points) - Sexual compatibility
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return Score out of 4
     */
    int calculateYoniKoota(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Calculate Graha Maitri Koota (5 points) - Planetary friendship
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return Score out of 5
     */
    int calculateGrahaMaitriKoota(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Calculate Gana Koota (6 points) - Temperament compatibility
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return Score out of 6
     */
    int calculateGanaKoota(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Calculate Bhakoot Koota (7 points) - Rashi compatibility
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return Score out of 7
     */
    int calculateBhakootKoota(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Calculate Nadi Koota (8 points) - Health compatibility
     * 
     * @param maleBirthData Birth data of the male
     * @param femaleBirthData Birth data of the female
     * @return Score out of 8
     */
    int calculateNadiKoota(BirthData maleBirthData, BirthData femaleBirthData);
    
    /**
     * Get the total possible score for Asthakoot (36 points).
     * 
     * @return Total possible score
     */
    default int getTotalPossibleScore() {
        return 36;
    }
    
    /**
     * Get compatibility level based on total score.
     * 
     * @param totalScore Total Asthakoot score
     * @return Compatibility level description
     */
    default String getCompatibilityLevel(int totalScore) {
        if (totalScore >= 32) return "Excellent";
        else if (totalScore >= 28) return "Very Good";
        else if (totalScore >= 24) return "Good";
        else if (totalScore >= 20) return "Average";
        else if (totalScore >= 16) return "Below Average";
        else return "Poor";
    }
} 