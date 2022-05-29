package org.divulgit.gitlab.project;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.annotation.ForRemote;
import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.gitlab.util.LinkHeaderUtil;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProjectCaller {

    @Autowired
    private HeaderAuthRestCaller gitLabRestCaller;

    @Autowired
    private GitLabURLBuilder urlBuilder;
    
    @Autowired
    private ProjectResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.GITLAB)
    private ErrorResponseHandler errorResponseHandler;

    public List<GitLabProject> retrieveProjects(final Remote remote, final String token) throws RemoteException {
        final List<GitLabProject> projects = new ArrayList<>();
        retrieveProjects(remote, token, projects, GitLabURLBuilder.INITIAL_PAGE);
        return projects;
    }

    private void retrieveProjects(final Remote remote, final String token, final List<GitLabProject> projects, int page) throws RemoteException {
        String url = urlBuilder.buildProjectURL(remote, page);
        ResponseEntity<String> response = gitLabRestCaller.call(url, token);
        if (response.getStatusCode().is2xxSuccessful()) {
            projects.addAll(responseHandler.handle200Response(response));
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveProjects(remote, token, projects, ++page);
        }
    }
}
