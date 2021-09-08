package org.divulgit.remote.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.stream.Collectors;

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
