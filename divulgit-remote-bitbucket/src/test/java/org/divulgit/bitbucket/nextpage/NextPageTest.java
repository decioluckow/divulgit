package org.divulgit.bitbucket.nextpage;
import org.divulgit.bitbucket.util.LinkHeaderUtil;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class NextPageTest {

    @Test
    public void testLinkHeaderWithNextPage() throws IOException {
        String json = TestUtils.getResourceAsString(this, "hasNextPage.json");
        ResponseEntity<String> response = ResponseEntity.ok(json);
        Assertions.assertTrue(LinkHeaderUtil.hasNextPage(response));
    }

    @Test
    public void testLinkHeaderWithoutNextPage() throws IOException {
        String json = TestUtils.getResourceAsString(this, "noNextPage.json");
        ResponseEntity<String> response = ResponseEntity .ok(json);
    	assertFalse(LinkHeaderUtil.hasNextPage(response));
    }
}