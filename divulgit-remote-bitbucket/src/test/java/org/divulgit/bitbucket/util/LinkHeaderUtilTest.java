package org.divulgit.bitbucket.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class LinkHeaderUtilTest {

    @Test
    public void testLinkHeaderWithNextPage() {
        ResponseEntity<String> response = ResponseEntity.noContent().header("Link", "\"<https://api.github.com/user/repos?per_page=2&page=2>; rel=\"next\", <https://api.github.com/user/repos?per_page=2&page=10>; rel=\"last\"").build();
        
        assertTrue(LinkHeaderUtil.hasNextPage(response));
    }

    @Test
    public void testLinkHeaderWithoutNextPage() {
    	ResponseEntity<String> response = ResponseEntity.noContent().header("Link", "<https://api.github.com/user/repos?per_page=2&page=2>; rel=\"prev\", <https://api.github.com/user/repos?per_page=2&page=10>; rel=\"last\"").build();

    	assertFalse(LinkHeaderUtil.hasNextPage(response));
    }

}