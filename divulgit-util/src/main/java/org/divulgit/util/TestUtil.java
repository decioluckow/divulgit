package org.divulgit.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtil {

    public static String getResourceAsString(Object source, String file) throws IOException {
        InputStream jsonResource = source.getClass().getResourceAsStream("/user.json");
        return new String(jsonResource.readAllBytes(), StandardCharsets.UTF_8);
    }
}
