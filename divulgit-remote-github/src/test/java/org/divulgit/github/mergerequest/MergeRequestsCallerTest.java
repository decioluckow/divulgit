package org.divulgit.github.mergerequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.divulgit.github.error.ErrorMapper;
import org.divulgit.github.restcaller.GitLabRestCaller;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.remote.model.RemoteUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MergeRequestsCallerTest {

    private Remote REMOTE = Remote.builder().url("localhost").build();
    private RemoteUser USER = buildRemoteUser();
    private Project PROJECT = Project.builder().build();
    private String TOKEN = "xpto";

    private GitHubMergeRequest MERGE_REQUEST = GitHubMergeRequest.builder().build();
    private List<GitHubMergeRequest> MERGE_REQUESTS = Arrays.asList(MERGE_REQUEST);

    @Mock
    private GitLabRestCaller restCaller;

    @Mock
    private MergeRequestMapper mapper;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private ErrorMapper errorMapper;

    @Mock
    private MergeRequestURLGenerator urlGenerator;

    @InjectMocks
    private MergeRequestsCaller caller;

    @Test
    void retrieveMergeRequests() throws RemoteException, JsonProcessingException {
        Mockito.when(urlGenerator.build(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString())).thenReturn("localhost");
        Mockito.when(restCaller.call(Mockito.anyString(), Mockito.anyString())).thenReturn(ResponseEntity.ok("{}"));
        Mockito.when(mapper.convertToMergeRequests(Mockito.anyString())).thenReturn(MERGE_REQUESTS);
        Mockito.verify(errorMapper, Mockito.never()).convertFrom(Mockito.anyString());

        List<GitHubMergeRequest> mergeRequests = caller.retrieveMergeRequests(REMOTE, USER, PROJECT, 0, TOKEN);

        assertTrue(mergeRequests.get(0) == MERGE_REQUEST);
    }

    @Test
    void retrieveError() throws RemoteException, JsonProcessingException {
        Mockito.when(urlGenerator.build(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString())).thenReturn("localhost");
        Mockito.when(restCaller.call(Mockito.anyString(), Mockito.anyString())).thenReturn(ResponseEntity.status(500).body("{\"message\":\"Bad credentials\",\"documentation_url\":\"https://docs.github.com/rest\"}"));
        Mockito.verify(mapper, Mockito.never()).convertToMergeRequests(Mockito.anyString());

        List<GitHubMergeRequest> mergeRequests = caller.retrieveMergeRequests(REMOTE, USER, PROJECT, 0, TOKEN);

        assertTrue(mergeRequests.get(0) == MERGE_REQUEST);
    }

     private RemoteUser buildRemoteUser() {
        return new RemoteUser() {
            @Override
            public String getInternalId() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getUsername() {
                return null;
            }

            @Override
            public String getAvatarURL() {
                return null;
            }
        };
    }
}