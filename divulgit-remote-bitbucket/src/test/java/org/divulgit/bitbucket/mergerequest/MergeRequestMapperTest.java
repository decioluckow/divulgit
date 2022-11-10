package org.divulgit.bitbucket.mergerequest;
import org.divulgit.bitbucket.pullrequest.BitBucketPullRequest;
import org.divulgit.bitbucket.pullrequest.BitBucketPullRequestResponseHandler;
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
        BitBucketPullRequestResponseHandler handler = new BitBucketPullRequestResponseHandler();
        BitBucketPullRequest mergeRequest = handler.handle200ResponseSingleResult(ResponseEntity.ok(json));
        assertEquals(1, mergeRequest.getExternalId());
        assertEquals("testando PR", mergeRequest.getTitle());
        assertEquals("wesleyeduardocr7", mergeRequest.getAuthor());
        assertEquals("https://bitbucket.org/wesleyeduardocr7/teste_api_bitbucket/pull-requests/1", mergeRequest.getUrl());
        assertNotNull(mergeRequest.getCreatedAt());
    }

    @Test
    public void testMultipleResponse() throws IOException, RemoteException {

        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        BitBucketPullRequestResponseHandler handler = new BitBucketPullRequestResponseHandler();
        List<BitBucketPullRequest> mergeRequests = handler.handle200ResponseMultipleResult(ResponseEntity.ok(json));
        assertEquals(2, mergeRequests.size());

        BitBucketPullRequest firstMergeRequest = mergeRequests.get(1);
        assertEquals(1, firstMergeRequest.getExternalId());
        assertEquals("testando PR", firstMergeRequest.getTitle());
        assertEquals("wesleyeduardocr7", firstMergeRequest.getAuthor());
        assertEquals("https://bitbucket.org/wesleyeduardocr7/teste_api_bitbucket/pull-requests/1", firstMergeRequest.getUrl());
        assertNotNull(firstMergeRequest.getCreatedAt());

        BitBucketPullRequest secondMergeRequest = mergeRequests.get(0);
        assertEquals(2, secondMergeRequest.getExternalId());
        assertEquals("testando reposta em String", secondMergeRequest.getTitle());
        assertEquals("wesleyeduardocr7", secondMergeRequest.getAuthor());
        assertEquals("https://bitbucket.org/wesleyeduardocr7/teste_api_bitbucket/pull-requests/2", secondMergeRequest.getUrl());
        assertNotNull(secondMergeRequest.getCreatedAt());
    }
}