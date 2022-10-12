package org.divulgit.gitlab.project;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.gitlab.util.LinkHeaderUtil;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProjectCaller {

    @Autowired
    private RestCaller gitLabRestCaller;

    @Autowired
    private GitLabURLBuilder urlBuilder;
    
    @Autowired
    private ProjectResponseHandler responseHandler;

    public List<GitLabProject> retrieveProjects(final Remote remote, final Authentication authentication) throws RemoteException {
        final List<GitLabProject> projects = new ArrayList<>();
        retrieveProjects(remote, authentication, projects, GitLabURLBuilder.INITIAL_PAGE);
        return projects;
    }

    private void retrieveProjects(final Remote remote, final Authentication authentication, final List<GitLabProject> projects, int page) throws RemoteException {
        String url = urlBuilder.buildProjectURL(remote, page);
        ResponseEntity<String> response = gitLabRestCaller.call(url, authentication);
        if (response.getStatusCode().is2xxSuccessful()) {
            projects.addAll(responseHandler.handle200Response(response));
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveProjects(remote, authentication, projects, ++page);
        }
    }
}
