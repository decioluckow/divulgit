package org.divulgit.util;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class PeriodUtil {

    public static String formatDuration(Period period) {
        List<String> parts = new ArrayList<>();
        long months = period.getMonths() + (period.getYears() * 12);
        if (months > 0) parts.add(plural(months, "month"));
        long days = period.getDays();
        if (days > 0) parts.add(plural(days, "day"));
        else if (days == 0) parts.add("today");
        return String.join(" ", parts);
    }

    private static String plural(long num, String unit) {
        return num + " " + unit + (num == 1 ? "" : "s");
    }
}
