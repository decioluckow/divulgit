package org.divulgit.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.json.JSONObject;

@UtilityClass
public class JSONUtil {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static String extractContent(String property, String response) {
        JSONObject json = new JSONObject(response);
        return json.get(property).toString();
    }

    public boolean isValid(String json) {
        try {
            JSON_MAPPER.readTree(json);
        } catch (JacksonException e) {
            return false;
        }
        return true;
    }
}
