package org.divulgit.gitlab.mergerequest;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.model.Remote;
import org.divulgit.model.Project;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.gitlab.error.ErrorMapper;
import org.divulgit.gitlab.error.ErrorMessage;
import org.divulgit.gitlab.restcaller.GitLabRestCaller;
import org.divulgit.remote.util.URLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MergeRequestCaller {

    @Autowired
    private GitLabRestCaller restCaller;

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
            List<String> requestedMergeRequestExternalIds,
            int scanFrom,
            String token) throws RemoteException {
        final List<GitLabMergeRequest> mergeRequests = new ArrayList<>();
        retrieveMergeRequests(remote, project, mergeRequests, requestedMergeRequestExternalIds, scanFrom, token, "1");
        return mergeRequests;
    }

    private void retrieveMergeRequests(
            Remote remote,
            Project project,
            List<GitLabMergeRequest> loadedMergeRequests,
            List<String> requestedMergeRequestExternalIds,
            int scanFrom,
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
