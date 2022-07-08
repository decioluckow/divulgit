package org.divulgit.security;

import org.divulgit.util.vo.RemoteIdentify;
import org.divulgit.security.identify.RemoteIdentifyParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RemoteIdentifyParserTest {

    @Test
    public void testParseValid() {
        RemoteIdentify remoteIdentify = RemoteIdentifyParser.parsePrincipal("decioluckow at api.github.com of github");
        assertEquals("decioluckow", remoteIdentify.getUsername());
        assertEquals("api.github.com", remoteIdentify.getDomain());
        assertEquals("github", remoteIdentify.getProvider());
    }

    @Test
    public void testParseInvalid() {
        try {
            RemoteIdentify remoteIdentify = RemoteIdentifyParser.parsePrincipal("invalid");
            fail("This test should fail");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

}