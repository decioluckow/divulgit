package org.divulgit.test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtils {

    public static String getResourceAsString(Object origin, String filename) throws IOException {
        try {
            try (InputStream input = origin.getClass().getResourceAsStream(filename)) {
                return CharStreams.toString(new InputStreamReader(input, Charsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
