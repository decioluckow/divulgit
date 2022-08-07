package org.divulgit.bitbucket.nextpage;
import org.divulgit.bitbucket.util.ResponseUtil;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class NextPageTest {

    @Test
    public void testRepositoriesWithNextPage() throws IOException {
        String json = TestUtils.getResourceAsString(this, "repositorieWithNextPage.json");
        ResponseEntity<String> response = ResponseEntity.ok(json);
        Assertions.assertTrue(ResponseUtil.hasNextPage(response));
    }

    @Test
    public void testRepositoriesWithoutNextPage() throws IOException {
        String json = TestUtils.getResourceAsString(this, "rerpositorieWithoutNextPage.json");
        ResponseEntity<String> response = ResponseEntity.ok(json);
    	assertFalse(ResponseUtil.hasNextPage(response));
    }
    @Test
    public void testPullRequestsWithNextPage() throws IOException {
        String json = TestUtils.getResourceAsString(this, "pullRequestsWithNextPage.json");
        ResponseEntity<String> response = ResponseEntity.ok(json);
        Assertions.assertTrue(ResponseUtil.hasNextPage(response));
    }

    @Test
    public void testPullRequestsWithoutNextPage() throws IOException {
        String json = TestUtils.getResourceAsString(this, "pullRequestsWithoutNextPage.json");
        ResponseEntity<String> response = ResponseEntity.ok(json);
        assertFalse(ResponseUtil.hasNextPage(response));
    }

    @Test
    public void testComentsPullRequestsWithNextPage() throws IOException {
        String json = TestUtils.getResourceAsString(this, "comentsPullRequestsWithNextPage.json");
        ResponseEntity<String> response = ResponseEntity.ok(json);
        Assertions.assertTrue(ResponseUtil.hasNextPage(response));
    }

    @Test
    public void testComentsPullRequestsWithoutNextPage() throws IOException {
        String json = TestUtils.getResourceAsString(this, "comentsPullRequestsWithoutNextPage.json");
        ResponseEntity<String> response = ResponseEntity.ok(json);
        assertFalse(ResponseUtil.hasNextPage(response));
    }
}
