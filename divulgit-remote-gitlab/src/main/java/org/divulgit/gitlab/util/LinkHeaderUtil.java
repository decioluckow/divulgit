package org.divulgit.gitlab.util;

import com.google.common.base.Strings;
import org.springframework.http.ResponseEntity;

public class LinkHeaderUtil {

    public static boolean hasNextPage(ResponseEntity<String> response) {
        String nextPage = response.getHeaders().getFirst("x-next-page");
        return ! Strings.isNullOrEmpty(nextPage);
    }
}
