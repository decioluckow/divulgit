package org.divulgit.github.mergerequest;

import org.divulgit.github.pullrequest.GitHubPullRequest;
import org.divulgit.github.pullrequest.PullRequestResponseHandler;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MergeRequestMapperTest {

    @Test
    public void testSingleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "singleResponse.json");
        PullRequestResponseHandler handler = new PullRequestResponseHandler();

        GitHubPullRequest mergeRequest = handler.handle200ResponseSingleResult(ResponseEntity.ok(json));

        assertEquals(8, mergeRequest.getExternalId());
        assertEquals("Update README.md", mergeRequest.getTitle());
        assertEquals("decioluckow", mergeRequest.getAuthor());
        assertNotNull(mergeRequest.getCreatedAt());
    }

    @Test
    public void testMultipleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        PullRequestResponseHandler handler = new PullRequestResponseHandler();
        
        List<GitHubPullRequest> mergeRequests = handler.handle200ResponseMultipleResult(ResponseEntity.ok(json));

        assertEquals(2, mergeRequests.size());
        GitHubPullRequest firstMergeRequest = mergeRequests.get(0);
        assertEquals(9, firstMergeRequest.getExternalId());
        assertEquals("Update HELP.md", firstMergeRequest.getTitle());
        assertEquals("decioluckow", firstMergeRequest.getAuthor());
        assertNotNull(firstMergeRequest.getCreatedAt());

        GitHubPullRequest secondMergeRequest = mergeRequests.get(1);
        assertEquals(8, secondMergeRequest.getExternalId());
        assertEquals("Update README.md", secondMergeRequest.getTitle());
        assertEquals("decioluckow", secondMergeRequest.getAuthor());
        assertNotNull(secondMergeRequest.getCreatedAt());
    }
}