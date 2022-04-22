package org.divulgit.gitlab.mergerequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.divulgit.gitlab.error.ErrorMapper;
import org.divulgit.gitlab.error.ErrorMessage;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MergeRequestCaller {

    public static final String STARTING_PAGE = "1";

    @Autowired
    private RestCaller restCaller;

    @Autowired
    private MergeRequestMapper mapper;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private MergeRequestURLGenerator urlGenerator;

    //getForEntity
    //https://www.baeldung.com/spring-resttemplate-json-list

    public List<GitLabMergeRequest> retrieveMergeRequests(
            Remote remote,
            Project project,
            List<Integer> requestedMergeRequestExternalIds,
            String token) throws RemoteException {
        final List<GitLabMergeRequest> mergeRequests = new ArrayList<>();
        Integer emptyScanFrom = 0;
        retrieveMergeRequests(remote, project, mergeRequests, requestedMergeRequestExternalIds, emptyScanFrom, token, STARTING_PAGE);
        return mergeRequests;
    }

    public List<GitLabMergeRequest> retrieveMergeRequests(
            Remote remote,
            Project project,
            Integer scanFrom,
            String token) throws RemoteException {
        final List<GitLabMergeRequest> mergeRequests = new ArrayList<>();
        List<Integer> emptyRequestedMergeRequestExternalIds = Collections.emptyList();
        retrieveMergeRequests(remote, project, mergeRequests, emptyRequestedMergeRequestExternalIds, scanFrom, token, STARTING_PAGE);
        return mergeRequests;
    }

    private void retrieveMergeRequests(
            Remote remote,
            Project project,
            List<GitLabMergeRequest> loadedMergeRequests,
            List<Integer> requestedMergeRequestExternalIds,
            Integer scanFrom,
            String token,
            final String page) throws RemoteException {
        String url = urlGenerator.build(remote, project, requestedMergeRequestExternalIds, page);
        ResponseEntity<String> response = restCaller.call(url, token);
        boolean stopScan = false;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitLabMergeRequest> remoteMergeRequests = handle200Response(response);
            for (GitLabMergeRequest remoteMergeRequest : remoteMergeRequests) {
                if (remoteMergeRequest.getExternalId() >= scanFrom) {
                    loadedMergeRequests.add(remoteMergeRequest);
                } else {
                    stopScan = true;
                }
            }
        } else if (response.getBody().contains("error_description")) {
            handleErrorResponse(response);
        }
        String nextPage = response.getHeaders().getFirst("x-next-page");
        if (!Strings.isNullOrEmpty(nextPage) && !stopScan) {
            retrieveMergeRequests(remote, project, loadedMergeRequests, requestedMergeRequestExternalIds, scanFrom, token, nextPage);
        }
    }

    private List<GitLabMergeRequest> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return mapper.convertoToMergeRequests(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }

    private void handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            ErrorMessage errorMessage = errorMapper.convertFrom(response.getBody());
            throw new RemoteException(errorMessage.getErrorDescription());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }
}
