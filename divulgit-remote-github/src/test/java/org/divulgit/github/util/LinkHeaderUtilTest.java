package org.divulgit.github.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkHeaderUtilTest {

    @Test
    public void testLinkHeaderWithNextPage() {
        assertTrue(LinkHeaderUtil.hasNextPage("<https://api.github.com/user/repos?per_page=2&page=2>; rel=\"next\", <https://api.github.com/user/repos?per_page=2&page=10>; rel=\"last\""));
    }

    @Test
    public void testLinkHeaderWithoutNextPage() {
        assertFalse(LinkHeaderUtil.hasNextPage("<https://api.github.com/user/repos?per_page=2&page=2>; rel=\"prev\", <https://api.github.com/user/repos?per_page=2&page=10>; rel=\"last\""));
    }

}