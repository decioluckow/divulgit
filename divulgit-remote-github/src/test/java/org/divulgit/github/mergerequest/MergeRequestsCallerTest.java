package org.divulgit.github.mergerequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.github.error.ErrorMessage;
import org.divulgit.github.error.ErrorResponseHandler;
import org.divulgit.github.error.GitHubErrorMapper;
import org.divulgit.github.pullrequest.GitHubPullRequest;
import org.divulgit.github.pullrequest.PullRequestResponseHandler;
import org.divulgit.github.pullrequest.PullRequestsCaller;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.remote.rest.RestCaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MergeRequestsCallerTest {

    private static final String LAST_PAGE_LIST_HEADER = "<https://url.com>; rel=\"prev\", <https://url.com>; rel=\"first\"";
    private static final String NOT_LAST_PAGE_LIST_HEADER= "<https://url.com>; rel=\"prev\", <https://url.com>; rel=\"next\"";
    private static final int TOTAL_PAGES = 3;

    private Remote REMOTE = Remote.builder().url("localhost").build();
    private User USER = buildRemoteUser();
    private Project PROJECT = Project.builder().build();
    private String TOKEN = "xpto";

    private GitHubPullRequest MERGE_REQUEST = GitHubPullRequest.builder().build();
    private List<GitHubPullRequest> MERGE_REQUESTS = Arrays.asList(MERGE_REQUEST);

    @Mock
    private RestCaller restCaller;

    @Mock
    private PullRequestResponseHandler successHandler;

    @Mock
    private ErrorResponseHandler errorResponseHandler;

    @Mock
    private GitHubURLBuilder urlBuilder;

    @InjectMocks
    private PullRequestsCaller caller;

    @Test
    void retrieveMergeRequests() throws RemoteException, JsonProcessingException {
        Mockito.when(urlBuilder.buildPullRequestsURL(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("localhost");
        Mockito.when(restCaller.call(Mockito.anyString(), Mockito.anyString())).thenReturn(ResponseEntity.ok("{}"));
        Mockito.when(successHandler.handle200ResponseMultipleResult(Mockito.any())).thenReturn(MERGE_REQUESTS);
        Mockito.verify(errorResponseHandler, Mockito.never()).handleErrorResponse(ArgumentMatchers.<ResponseEntity<String>>any());

        List<GitHubPullRequest> mergeRequests = caller.retrievePullRequests(REMOTE, USER, PROJECT, 0, TOKEN);

        assertTrue(mergeRequests.get(0) == MERGE_REQUEST);
    }

    //@Test
    void retrieveMergeRequestsPaginating() throws RemoteException, JsonProcessingException {
        Mockito.when(urlBuilder.buildPullRequestsURL(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn("localhost");
        AtomicInteger page = new AtomicInteger();
        Mockito.when(restCaller.call(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> callPaginatingRest(page));
        Mockito.when(successHandler.handle200ResponseMultipleResult(Mockito.any())).thenReturn(MERGE_REQUESTS);
        Mockito.verify(errorResponseHandler, Mockito.never()).handleErrorResponse(ArgumentMatchers.<ResponseEntity<String>>any());

        List<GitHubPullRequest> mergeRequests = caller.retrievePullRequests(REMOTE, USER, PROJECT, 0, TOKEN);

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
        Mockito.when(restCaller.call(Mockito.anyString(), Mockito.anyString())).thenReturn(ResponseEntity.status(401).body("{\"message\":\"Bad credentials\",\"documentation_url\":\"https://docs.github.com/rest\"}"));
        Mockito.when(errorResponseHandler.handleErrorResponse(Mockito.any())).thenThrow(new RemoteException("Bad credentials"));

        Exception exception = Assertions.assertThrows(RemoteException.class, () ->
                caller.retrievePullRequests(REMOTE, USER, PROJECT, 0, TOKEN));
        
        assertEquals("Bad credentials", exception.getMessage());
    }

     private User buildRemoteUser() {
        return User.builder().build();
    }
}