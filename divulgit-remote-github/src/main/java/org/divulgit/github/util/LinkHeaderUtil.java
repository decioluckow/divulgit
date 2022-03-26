package org.divulgit.github.util;

import org.apache.logging.log4j.util.Strings;

public class LinkHeaderUtil {

    public static boolean hasNextPage(String linkHeaderValue) {
        return Strings.isNotEmpty(linkHeaderValue) && linkHeaderValue.contains("rel=\"next\"");
    }
}
