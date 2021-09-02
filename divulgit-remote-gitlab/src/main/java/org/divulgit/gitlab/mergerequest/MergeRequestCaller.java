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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MergeRequestCaller {

    @Autowired
    private GitLabRestCaller restCaller;
    
    @Autowired
    private MergeRequestMapper mapper;

    @Autowired
    private ErrorMapper errorMapper;

    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    //getForEntity
    //https://www.baeldung.com/spring-resttemplate-json-list

    public List<GitLabMergeRequest> retrieveMergeRequests(final Remote remote, Project project, final String token) throws RemoteException {
        final List<GitLabMergeRequest> mergeRequests = new ArrayList<>();
        retrieveMergeRequests(remote, project, "1");
        return mergeRequests;
    }

    private void retrieveMergeRequests(Remote remote, Project project, List<GitLabMergeRequest> loadedMergeRequests, List<String> requestedMergeRequestExternalIds, String token, final String page) throws RemoteException {
        ResponseEntity<String> response = restCaller.call(mountURL(remote, project, requestedMergeRequestExternalIds, page), token);
        if (response.getStatusCode().is2xxSuccessful()) {
            loadedMergeRequests.addAll(handle200Response(response));
        } else if (response.getBody().contains("error_description")) {
            handleErrorResponse(response);
        }
        String nextPage = response.getHeaders().getFirst("x-next-page");
        if (!Strings.isNullOrEmpty(nextPage)) {
            retrieveMergeRequests(remote, project, loadedMergeRequests, requestedMergeRequestExternalIds, token, nextPage);
        }
    }

    private String mountURL(final Remote remote, Project project, List<String> requestedMergeRequestExternalIds, final String page) {
        String requestedMergeRequestExternalIdsParam = String.join(",", requestedMergeRequestExternalIds);
        return MessageFormat.format("https://{0}/api/v4/projects/{1}/merge_requests?iids[]={2}per_page={3}&page={4}",
                remote.getUrl(),
                project.getId(),
                requestedMergeRequestExternalIdsParam,
                pageSize, page);
    }

    private List<GitLabMergeRequest> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return mapper.convertoToMergeRequests(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }

    private void handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            ErrorMessage errorMessage = errorMapper.convertFrom(response.getBody());
            throw new RemoteException(errorMessage.getErrorDescription());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }
}
