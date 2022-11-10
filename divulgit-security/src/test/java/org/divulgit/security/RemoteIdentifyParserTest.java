package org.divulgit.security;

import org.divulgit.type.RemoteType;
import org.divulgit.util.vo.RemoteIdentify;
import org.divulgit.security.identify.RemoteIdentifyParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RemoteIdentifyParserTest {

    @Test
    public void testParseValid() {
        RemoteIdentify remoteIdentify = RemoteIdentifyParser.parsePrincipal("{\"username\":\"decioluckow\",\"domain\":\"api.github.com\",\"plataform\":\"GITHUB\"}");
        assertEquals("decioluckow", remoteIdentify.getUsername());
        assertEquals("api.github.com", remoteIdentify.getDomain());
        assertEquals(RemoteType.GITHUB, remoteIdentify.getRemoteType());
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