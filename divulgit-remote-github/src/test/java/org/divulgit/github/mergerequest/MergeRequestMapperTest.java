package org.divulgit.github.mergerequest;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MergeRequestMapperTest {

    @Test
    public void testSingleResponse() throws IOException {
        String json = TestUtils.getResourceAsString(this, "singleResponse.json");
        MergeRequestMapper mapper = new MergeRequestMapper();

        GitHubMergeRequest mergeRequest = mapper.convertToMergeRequest(json);

        assertEquals(8, mergeRequest.getExternalId());
        assertEquals("Update README.md", mergeRequest.getTitle());
        assertEquals("decioluckow", mergeRequest.getAuthor());
    }

    @Test
    public void testMultipleResponse() throws IOException {
        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        MergeRequestMapper mapper = new MergeRequestMapper();

        List<GitHubMergeRequest> mergeRequests = mapper.convertToMergeRequests(json);

        assertEquals(2, mergeRequests.size());
        GitHubMergeRequest firstMergeRequest = mergeRequests.get(0);
        assertEquals(9, firstMergeRequest.getExternalId());
        assertEquals("Update HELP.md", firstMergeRequest.getTitle());
        assertEquals("decioluckow", firstMergeRequest.getAuthor());
        GitHubMergeRequest secondMergeRequest = mergeRequests.get(1);
        assertEquals(8, secondMergeRequest.getExternalId());
        assertEquals("Update README.md", secondMergeRequest.getTitle());
        assertEquals("decioluckow", secondMergeRequest.getAuthor());
    }
}