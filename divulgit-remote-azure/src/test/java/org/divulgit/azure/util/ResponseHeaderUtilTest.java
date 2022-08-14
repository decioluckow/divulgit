package org.divulgit.azure.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ResponseHeaderUtilTest {

    @Test
    public void testResponseWithContinuationToken() {
        ResponseEntity<String> response = ResponseEntity.noContent().header("x-ms-continuationtoken", "xpto123").build();
        
        assertTrue(ResponseHeaderUtil.getContinuationToken(response).isPresent());
    }

    @Test
    public void testResponseWithoutContinuationToken() {
    	ResponseEntity<String> response = ResponseEntity.noContent().header("some-header", "some value").build();

    	assertFalse(ResponseHeaderUtil.getContinuationToken(response).isPresent());
    }
}
