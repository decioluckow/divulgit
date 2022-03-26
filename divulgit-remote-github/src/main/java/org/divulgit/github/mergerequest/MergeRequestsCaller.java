package org.divulgit.github.mergerequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.github.error.ErrorMapper;
import org.divulgit.github.error.ErrorMessage;
import org.divulgit.github.restcaller.GitLabRestCaller;
import org.divulgit.github.util.LinkHeaderUtil;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.remote.model.RemoteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MergeRequestsCaller {

    public static final String STARTING_PAGE = "1";

    @Autowired
    private GitLabRestCaller restCaller;

    @Autowired
    private MergeRequestMapper mapper;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private MergeRequestURLGenerator urlGenerator;

    public List<GitHubMergeRequest> retrieveMergeRequests(
            Remote remote,
            RemoteUser user,
            Project project,
            List<Integer> requestedMergeRequestExternalIds,
            String token) throws RemoteException {
        final List<GitHubMergeRequest> mergeRequests = new ArrayList<>();
        for (Integer id : requestedMergeRequestExternalIds) {
            mergeRequests.add(retrieveMergeRequest(remote, user, project, id, token));
        }
        return mergeRequests;
    }

    public List<GitHubMergeRequest> retrieveMergeRequests(
            Remote remote,
            RemoteUser user,
            Project project,
            Integer scanFrom,
            String token) throws RemoteException {
        final List<GitHubMergeRequest> mergeRequests = new ArrayList<>();
        retrieveMergeRequests(remote, user, project, mergeRequests, scanFrom, token, STARTING_PAGE);
        return mergeRequests;
    }

    private void retrieveMergeRequests(
            Remote remote,
            RemoteUser user,
            Project project,
            List<GitHubMergeRequest> loadedMergeRequests,
            Integer scanFrom,
            String token,
            final String page) throws RemoteException {
        String url = urlGenerator.build(remote, user, project, page);
        ResponseEntity<String> response = restCaller.call(url, token);
        boolean stopScan = false;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitHubMergeRequest> remoteMergeRequests = handle200ResponseMultipleResult(response);
            for (GitHubMergeRequest remoteMergeRequest : remoteMergeRequests) {
                if (remoteMergeRequest.getExternalId() >= scanFrom) {
                    loadedMergeRequests.add(remoteMergeRequest);
                } else {
                    stopScan = true;
                }
            }
        } else if (response.getBody().contains("message")) {
            handleErrorResponse(response);
        }
        String linkHeaderValue = response.getHeaders().getFirst("Link");
        if (LinkHeaderUtil.hasNextPage(linkHeaderValue) && !stopScan) {
            retrieveMergeRequests(remote, user, project, loadedMergeRequests, scanFrom, token, linkHeaderValue);
        }
    }

    private GitHubMergeRequest retrieveMergeRequest(
            Remote remote,
            RemoteUser user,
            Project project,
            Integer mergeRequestExternalId,
            String token) throws RemoteException {
        String url = urlGenerator.build(remote, user, project, mergeRequestExternalId);
        ResponseEntity<String> response = restCaller.call(url, token);
        GitHubMergeRequest resultMergeRequest = null;
        if (response.getStatusCode().is2xxSuccessful()) {
            resultMergeRequest = handle200ResponseSingleResult(response);
        } else if (response.getBody().contains("message")) {
            handleErrorResponse(response);
        }
        return resultMergeRequest;
    }

    private List<GitHubMergeRequest> handle200ResponseMultipleResult(ResponseEntity<String> response) throws RemoteException {
        try {
            return mapper.convertToMergeRequests(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }

    private GitHubMergeRequest handle200ResponseSingleResult(ResponseEntity<String> response) throws RemoteException {
        try {
            return mapper.convertToMergeRequest(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }

    private void handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            ErrorMessage errorMessage = errorMapper.convertFrom(response.getBody());
            throw new RemoteException(errorMessage.getMessage());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }
}
