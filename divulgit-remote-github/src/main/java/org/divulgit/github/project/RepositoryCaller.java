package org.divulgit.github.project;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.annotation.ForRemote;
import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.github.util.LinkHeaderUtil;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RepositoryCaller {

    @Autowired
    private HeaderAuthRestCaller gitHubRestCaller;

    @Autowired
    private GitHubURLBuilder urlBuilder;
    
    @Autowired
    @ForRemote(RemoteType.GITHUB)
    private ErrorResponseHandler errorResponseHandler;
    
    @Autowired
    private RepositoryResponseHandler responseHandler;

    public List<GitHubRepository> retrieveRepositories(final Remote remote, final Authentication authentication) throws RemoteException {
        final List<GitHubRepository> projects = new ArrayList<>();
        retrieveRepositories(remote, authentication, projects, GitHubURLBuilder.INITIAL_PAGE);
        return projects;
    }

    private void retrieveRepositories(final Remote remote, final Authentication authentication, final List<GitHubRepository> projects, int page) throws RemoteException {
        String url = urlBuilder.buildRepository(remote, page);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
        if (response.getStatusCode().is2xxSuccessful()) {
            projects.addAll(responseHandler.handle200Response(response));
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
        	retrieveRepositories(remote, authentication, projects, ++page);
        }
    }
}
