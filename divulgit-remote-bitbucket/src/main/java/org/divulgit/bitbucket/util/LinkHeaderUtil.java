package org.divulgit.bitbucket.util;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.divulgit.util.JSONUtil;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class LinkHeaderUtil {

    public static boolean hasNextPage(ResponseEntity<String> response) {
        return StringUtils.isNotEmpty(getNextPage(response));
    }

    public static String getNextPage(ResponseEntity<String> response) {
        String nextPageLink = StringUtils.EMPTY;
        try{
            nextPageLink = JSONUtil.extractContent("next",response.getBody());
        }catch (JSONException ignored){}
        return nextPageLink;
    }
}
