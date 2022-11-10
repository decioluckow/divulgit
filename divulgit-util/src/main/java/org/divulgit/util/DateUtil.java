package org.divulgit.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@UtilityClass
public class DateUtil {

    public static LocalDate parseDateFromDateTime(String dateTime) {
        LocalDate date = null;
        if (dateTime != null && dateTime.length() > 10) {
            String datePortion = dateTime.substring(0, 10);
            date = LocalDate.parse(datePortion);
        }
        return date;
    }
}
