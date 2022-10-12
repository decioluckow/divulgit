package org.divulgit.azure.util;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ResponseHeaderUtil {

    public static Optional<String> getContinuationToken(ResponseEntity<String> response) {
        return Optional.ofNullable(response.getHeaders().getFirst("x-ms-continuationtoken"));
    }
}
