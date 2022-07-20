package org.divulgit.bitbucket.mergerequest;
import bitbucket.BitBucketURLBuilder;
import bitbucket.error.BitBucketErrorResponseHandler;
import bitbucket.pullrequest.BitBucketPullRequest;
import bitbucket.pullrequest.PullRequestResponseHandler;
import bitbucket.pullrequest.PullRequestsCaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MergeRequestsCallerTest {

    private static final String LAST_PAGE_LIST_HEADER = "<https://url.com>; rel=\"prev\", <https://url.com>; rel=\"first\"";
    private static final String NOT_LAST_PAGE_LIST_HEADER= "<https://url.com>; rel=\"prev\", <https://url.com>; rel=\"next\"";
    private static final int TOTAL_PAGES = 3;

    private Remote REMOTE = Remote.builder().url("localhost").build();
    private User USER = buildUser();
    private Project PROJECT = Project.builder().build();
    private String TOKEN = "xpto";

    private BitBucketPullRequest MERGE_REQUEST = BitBucketPullRequest.builder().build();
    private List<BitBucketPullRequest> MERGE_REQUESTS = Arrays.asList(MERGE_REQUEST);

    @Mock
    private HeaderAuthRestCaller bitBucketRestCaller;;

    @Mock
    private PullRequestResponseHandler successHandler;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private BitBucketErrorResponseHandler errorResponseHandler;

    @Mock
    private BitBucketURLBuilder urlBuilder;

    @InjectMocks
    private PullRequestsCaller caller;

    @Test
    void retrieveMergeRequests() throws RemoteException, JsonProcessingException {
        Mockito.when(urlBuilder.buildPullRequestsURL(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("localhost");
        Mockito.when(bitBucketRestCaller.call(Mockito.anyString(), Mockito.anyString())).thenReturn(ResponseEntity.ok("{}"));
        Mockito.when(successHandler.handle200ResponseMultipleResult(Mockito.any())).thenReturn(MERGE_REQUESTS);
        Mockito.verify(errorResponseHandler, Mockito.never()).handleErrorResponse(ArgumentMatchers.<ResponseEntity<String>>any());

        List<BitBucketPullRequest> mergeRequests = caller.retrievePullRequests(REMOTE, USER, PROJECT, 0, TOKEN);

        assertTrue(mergeRequests.get(0) == MERGE_REQUEST);
    }

    //@Test
    void retrieveMergeRequestsPaginating() throws RemoteException, JsonProcessingException {
        Mockito.when(urlBuilder.buildPullRequestsURL(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("localhost");
        AtomicInteger page = new AtomicInteger();
        Mockito.when(bitBucketRestCaller.call(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> callPaginatingRest(page));
        Mockito.when(successHandler.handle200ResponseMultipleResult(Mockito.any())).thenReturn(MERGE_REQUESTS);
        Mockito.verify(errorResponseHandler, Mockito.never()).handleErrorResponse(ArgumentMatchers.<ResponseEntity<String>>any());

        List<BitBucketPullRequest> mergeRequests = caller.retrievePullRequests(REMOTE, USER, PROJECT, 0, TOKEN);

        assertTrue(mergeRequests.get(0) == MERGE_REQUEST);
    }

    private ResponseEntity callPaginatingRest(AtomicInteger page) {
        ResponseEntity.BodyBuilder response = ResponseEntity.ok();
        log.info("this:>> {}", this);
        response.body("[]");
        response.header("Link", (page.incrementAndGet() < TOTAL_PAGES) ? NOT_LAST_PAGE_LIST_HEADER : LAST_PAGE_LIST_HEADER);
        log.info("Entregando pagina {}", page);
        return response.build();
    }

    @Test
    void retrieveError() throws RemoteException, JsonProcessingException {
        Mockito.when(urlBuilder.buildPullRequestsURL(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("localhost");
        Mockito.when(bitBucketRestCaller.call(Mockito.anyString(), Mockito.anyString())).thenReturn(ResponseEntity.status(401).body("{\n" +
                "    \"type\": \"error\",\n" +
                "    \"error\": {\n" +
                "        \"message\": \"Resource not found\",\n" +
                "        \"detail\": \"There is no API hosted at this URL.\\n\\nFor information about our API's, please refer to the documentation at: https://developer.atlassian.com/bitbucket/api/2/reference/\"\n" +
                "    }\n" +
                "}"));
        Exception exception = Assertions.assertThrows(RemoteException.class, () ->
                caller.retrievePullRequests(REMOTE, USER, PROJECT, 0, TOKEN));
        assertEquals("Resource not found", exception.getMessage());
    }
     private User buildUser() {
        return User.builder().build();
    }
}