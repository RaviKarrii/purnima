package com.example.purnima.model;

import java.util.Objects;

/**
 * Result class containing Asthakoot (eight-fold compatibility) calculations.
 * Contains scores for all eight kootas and total compatibility score.
 */
public class AsthakootResult {
    private final BirthData maleBirthData;
    private final BirthData femaleBirthData;
    private final int varnaKoota;
    private final int vashyaKoota;
    private final int taraKoota;
    private final int yoniKoota;
    private final int grahaMaitriKoota;
    private final int ganaKoota;
    private final int bhakootKoota;
    private final int nadiKoota;
    private final int totalScore;
    private final String compatibilityLevel;
    private final String overallAssessment;

    public AsthakootResult(BirthData maleBirthData, BirthData femaleBirthData,
                          int varnaKoota, int vashyaKoota, int taraKoota, int yoniKoota,
                          int grahaMaitriKoota, int ganaKoota, int bhakootKoota, int nadiKoota) {
        this.maleBirthData = maleBirthData;
        this.femaleBirthData = femaleBirthData;
        this.varnaKoota = varnaKoota;
        this.vashyaKoota = vashyaKoota;
        this.taraKoota = taraKoota;
        this.yoniKoota = yoniKoota;
        this.grahaMaitriKoota = grahaMaitriKoota;
        this.ganaKoota = ganaKoota;
        this.bhakootKoota = bhakootKoota;
        this.nadiKoota = nadiKoota;
        this.totalScore = varnaKoota + vashyaKoota + taraKoota + yoniKoota + 
                         grahaMaitriKoota + ganaKoota + bhakootKoota + nadiKoota;
        this.compatibilityLevel = getCompatibilityLevel(this.totalScore);
        this.overallAssessment = generateOverallAssessment();
    }

    // Getters
    public BirthData getMaleBirthData() {
        return maleBirthData;
    }

    public BirthData getFemaleBirthData() {
        return femaleBirthData;
    }

    public int getVarnaKoota() {
        return varnaKoota;
    }

    public int getVashyaKoota() {
        return vashyaKoota;
    }

    public int getTaraKoota() {
        return taraKoota;
    }

    public int getYoniKoota() {
        return yoniKoota;
    }

    public int getGrahaMaitriKoota() {
        return grahaMaitriKoota;
    }

    public int getGanaKoota() {
        return ganaKoota;
    }

    public int getBhakootKoota() {
        return bhakootKoota;
    }

    public int getNadiKoota() {
        return nadiKoota;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getCompatibilityLevel() {
        return compatibilityLevel;
    }

    public String getOverallAssessment() {
        return overallAssessment;
    }

    /**
     * Get the maximum possible score for Asthakoot.
     */
    public int getMaximumScore() {
        return 36;
    }

    /**
     * Get the percentage compatibility.
     */
    public double getCompatibilityPercentage() {
        return (double) totalScore / getMaximumScore() * 100;
    }

    /**
     * Check if the compatibility is considered good (24+ points).
     */
    public boolean isGoodCompatibility() {
        return totalScore >= 24;
    }

    /**
     * Check if the compatibility is considered excellent (32+ points).
     */
    public boolean isExcellentCompatibility() {
        return totalScore >= 32;
    }

    /**
     * Get detailed breakdown of all kootas.
     */
    public String getDetailedBreakdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("Asthakoot Compatibility Analysis\n");
        sb.append("================================\n\n");
        sb.append("Male: ").append(maleBirthData.getPlaceName()).append("\n");
        sb.append("Female: ").append(femaleBirthData.getPlaceName()).append("\n\n");
        
        sb.append("1. Varna Koota (Caste): ").append(varnaKoota).append("/1\n");
        sb.append("2. Vashya Koota (Control): ").append(vashyaKoota).append("/2\n");
        sb.append("3. Tara Koota (Star): ").append(taraKoota).append("/3\n");
        sb.append("4. Yoni Koota (Sexual): ").append(yoniKoota).append("/4\n");
        sb.append("5. Graha Maitri Koota (Planetary): ").append(grahaMaitriKoota).append("/5\n");
        sb.append("6. Gana Koota (Temperament): ").append(ganaKoota).append("/6\n");
        sb.append("7. Bhakoot Koota (Rashi): ").append(bhakootKoota).append("/7\n");
        sb.append("8. Nadi Koota (Health): ").append(nadiKoota).append("/8\n\n");
        
        sb.append("Total Score: ").append(totalScore).append("/36\n");
        sb.append("Percentage: ").append(String.format("%.1f", getCompatibilityPercentage())).append("%\n");
        sb.append("Level: ").append(compatibilityLevel).append("\n");
        sb.append("Assessment: ").append(overallAssessment);
        
        return sb.toString();
    }

    private String getCompatibilityLevel(int totalScore) {
        if (totalScore >= 32) return "Excellent";
        else if (totalScore >= 28) return "Very Good";
        else if (totalScore >= 24) return "Good";
        else if (totalScore >= 20) return "Average";
        else if (totalScore >= 16) return "Below Average";
        else return "Poor";
    }

    private String generateOverallAssessment() {
        if (totalScore >= 32) {
            return "Excellent compatibility. This is an ideal match with strong foundation for a harmonious relationship.";
        } else if (totalScore >= 28) {
            return "Very good compatibility. This couple has strong potential for a successful and happy marriage.";
        } else if (totalScore >= 24) {
            return "Good compatibility. This match shows promise with some areas that may need attention.";
        } else if (totalScore >= 20) {
            return "Average compatibility. This match has both strengths and challenges that should be considered.";
        } else if (totalScore >= 16) {
            return "Below average compatibility. This match may face significant challenges and requires careful consideration.";
        } else {
            return "Poor compatibility. This match is not recommended as it may lead to difficulties in the relationship.";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsthakootResult that = (AsthakootResult) o;
        return varnaKoota == that.varnaKoota &&
                vashyaKoota == that.vashyaKoota &&
                taraKoota == that.taraKoota &&
                yoniKoota == that.yoniKoota &&
                grahaMaitriKoota == that.grahaMaitriKoota &&
                ganaKoota == that.ganaKoota &&
                bhakootKoota == that.bhakootKoota &&
                nadiKoota == that.nadiKoota &&
                totalScore == that.totalScore &&
                Objects.equals(maleBirthData, that.maleBirthData) &&
                Objects.equals(femaleBirthData, that.femaleBirthData) &&
                Objects.equals(compatibilityLevel, that.compatibilityLevel) &&
                Objects.equals(overallAssessment, that.overallAssessment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maleBirthData, femaleBirthData, varnaKoota, vashyaKoota, taraKoota,
                yoniKoota, grahaMaitriKoota, ganaKoota, bhakootKoota, nadiKoota, totalScore,
                compatibilityLevel, overallAssessment);
    }

    @Override
    public String toString() {
        return "AsthakootResult{" +
                "totalScore=" + totalScore +
                ", compatibilityLevel='" + compatibilityLevel + '\'' +
                ", percentage=" + String.format("%.1f", getCompatibilityPercentage()) + "%" +
                '}';
    }
} 