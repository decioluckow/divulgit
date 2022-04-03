package org.divulgit.github.mergerequest;

import org.divulgit.github.pullrequest.GitHubPullRequest;
import org.divulgit.github.pullrequest.PullRequestMapper;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MergeRequestMapperTest {

    @Test
    public void testSingleResponse() throws IOException {
        String json = TestUtils.getResourceAsString(this, "singleResponse.json");
        PullRequestMapper mapper = new PullRequestMapper();

        GitHubPullRequest mergeRequest = mapper.parsePullRequest(json);

        assertEquals(8, mergeRequest.getExternalId());
        assertEquals("Update README.md", mergeRequest.getTitle());
        assertEquals("decioluckow", mergeRequest.getAuthor());
    }

    @Test
    public void testMultipleResponse() throws IOException {
        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        PullRequestMapper mapper = new PullRequestMapper();

        List<GitHubPullRequest> mergeRequests = mapper.parsePullRequests(json);

        assertEquals(2, mergeRequests.size());
        GitHubPullRequest firstMergeRequest = mergeRequests.get(0);
        assertEquals(9, firstMergeRequest.getExternalId());
        assertEquals("Update HELP.md", firstMergeRequest.getTitle());
        assertEquals("decioluckow", firstMergeRequest.getAuthor());
        GitHubPullRequest secondMergeRequest = mergeRequests.get(1);
        assertEquals(8, secondMergeRequest.getExternalId());
        assertEquals("Update README.md", secondMergeRequest.getTitle());
        assertEquals("decioluckow", secondMergeRequest.getAuthor());
    }
}