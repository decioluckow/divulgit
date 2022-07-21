package org.divulgit.util;

import lombok.experimental.UtilityClass;
import org.json.JSONObject;

@UtilityClass
public class JSONUtil {

    public static String extractContent(String content, String response) {
        JSONObject json = new JSONObject(response);
        return json.getString(content);
    }
}
