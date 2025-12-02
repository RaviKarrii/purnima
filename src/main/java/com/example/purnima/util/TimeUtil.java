package com.example.purnima.util;

public class TimeUtil {

    /**
     * Formats decimal hours into HH:mm format.
     * Example: 10.5 -> "10:30"
     * 
     * @param decimalHours Time in decimal hours (0.0 to 24.0)
     * @return Formatted time string "HH:mm"
     */
    public static String formatDecimalTime(double decimalHours) {
        int hours = (int) decimalHours;
        int minutes = (int) ((decimalHours - hours) * 60);
        return String.format("%02d:%02d", hours, minutes);
    }
}
