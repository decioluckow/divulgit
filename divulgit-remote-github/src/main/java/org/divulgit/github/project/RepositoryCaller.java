package org.divulgit.github.project;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.github.error.ErrorResponseHandler;
import org.divulgit.github.util.LinkHeaderUtil;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RepositoryCaller {

    @Autowired
    private RestCaller restCaller;

    @Autowired
    private GitHubURLBuilder urlBuilder;
    
    @Autowired
    private ErrorResponseHandler errorResponseHandler;
    
    @Autowired
    private RepositoryResponseHandler responseHandler;

    public List<GitHubRepository> retrieveRepositories(final Remote remote, final String token) throws RemoteException {
        final List<GitHubRepository> projects = new ArrayList<>();
        retrieveRepositories(remote, token, projects, GitHubURLBuilder.INITIAL_PAGE);
        return projects;
    }

    private void retrieveRepositories(final Remote remote, final String token, final List<GitHubRepository> projects, int page) throws RemoteException {
        ResponseEntity<String> response = restCaller.call(urlBuilder.buildRepository(remote, page), token);
        if (response.getStatusCode().is2xxSuccessful()) {
            projects.addAll(responseHandler.handle200Response(response));
        } else if (response.getBody().contains("message")) {
        	errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
        	retrieveRepositories(remote, token, projects, ++page);
        }
    }


}
