package org.divulgit.util;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

import lombok.experimental.UtilityClass;

@UtilityClass
public class URLUtil {

    public static String toListOfParams(List<Integer> values, String paramName) {
        return values.stream().map(id -> paramName + "=" + id).collect(Collectors.joining("&"));
    }

    public static String prepareToConcat(String param, boolean before) {
        if (Strings.isNotEmpty(param)) {
            return (before ? "&":"") + param + (!before ? "&":"");
        }
        return "";
    }
}
