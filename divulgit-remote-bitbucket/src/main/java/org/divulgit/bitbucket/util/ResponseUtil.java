package org.divulgit.bitbucket.util;
import com.mashape.unirest.http.JsonNode;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseUtil {

    private static final String NEXT_PAGE_KEY = "next";

    public static String getNextPage(ResponseEntity<String> response) {
        String nextPage = StringUtils.EMPTY;
        if(hasNextPage(response)){
            nextPage = new JsonNode(response.getBody()).getObject().get(NEXT_PAGE_KEY).toString();
        }
        return nextPage;
    }

    public static boolean hasNextPage(ResponseEntity<String> response) {
        return new JsonNode(response.getBody()).getObject().has(NEXT_PAGE_KEY);
    }
}
