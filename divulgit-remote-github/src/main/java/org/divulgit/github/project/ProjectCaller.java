package org.divulgit.github.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.github.error.ErrorMapper;
import org.divulgit.github.error.ErrorMessage;
import org.divulgit.github.restcaller.GitLabRestCaller;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ProjectCaller {

    @Autowired
    private GitLabRestCaller restCaller;

    @Autowired
    private ProjectURLGenerator urlGenerator;
    
    @Autowired
    private ProjectMapper mapper;

    @Autowired
    private ErrorMapper errorMapper;

    //getForEntity
    //https://www.baeldung.com/spring-resttemplate-json-list

    public List<GitHubProject> retrieveProjects(final Remote remote, final String token) throws RemoteException {
        final List<GitHubProject> projects = new ArrayList<>();
        retrieveProjects(remote, token, projects, "1");
        return projects;
    }

    private void retrieveProjects(final Remote remote, final String token, final List<GitHubProject> projects, final String page) throws RemoteException {
        ResponseEntity<String> response = restCaller.call(urlGenerator.build(remote, page), token);
        if (response.getStatusCode().is2xxSuccessful()) {
            projects.addAll(handle200Response(response));
        } else if (response.getBody().contains("error_description")) {
            handleErrorResponse(response);
        }
        String nextPage = response.getHeaders().getFirst("x-next-page");
        if (!Strings.isNullOrEmpty(nextPage)) {
            retrieveProjects(remote, token, projects, nextPage);
        }
    }

    private List<GitHubProject> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return mapper.convertToProjects(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }

    private Optional<GitHubProject> handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            ErrorMessage errorMessage = errorMapper.convertFrom(response.getBody());
            throw new RemoteException(errorMessage.getMessage());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }
}
