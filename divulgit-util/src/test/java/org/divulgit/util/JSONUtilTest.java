package org.divulgit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONUtilTest {

    @Test
    void extractContentSimple() {
        String content = "{\"name\":\"someone\"}";

        String value = JSONUtil.extractContent("name", content);

        assertEquals("someone", value);
    }

    @Test
    void extractContentBlock() {
        String content = "{\"name\":\"someone\",\"items\":[\"1\",\"2\"]}";

        String value = JSONUtil.extractContent("items", content);

        assertEquals("[\"1\",\"2\"]", value);
    }

    @Test
    void extractContentDeeperBlock() {
        String content = "{\"name\":\"someone\",\"items\":[\"1\",\"2\"],\"details\":{\"level\":\"1\",\"info\":\"xpto\"}}";

        String value = JSONUtil.extractContent("details", content);

        assertEquals("{\"level\":\"1\",\"info\":\"xpto\"}", value);
    }

    @Test
    void isValid() {
        assertFalse(JSONUtil.isValid("Internal Server Error"));
        assertFalse(JSONUtil.isValid("<401,Not Authorized Access"));
        assertFalse(JSONUtil.isValid("<html><body>Internal Server Error</body></html>"));
        assertTrue(JSONUtil.isValid("{}"));
        assertTrue(JSONUtil.isValid("[]"));
        assertTrue(JSONUtil.isValid("{\"name\":\"someone\"}"));
        assertTrue(JSONUtil.isValid("[{\"name\":\"someone\"},{\"name\":\"someone\"}]"));
    }
}
