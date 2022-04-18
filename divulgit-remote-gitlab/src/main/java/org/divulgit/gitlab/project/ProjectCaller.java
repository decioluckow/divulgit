package org.divulgit.gitlab.project;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.divulgit.model.Remote;
import org.divulgit.gitlab.error.ErrorMapper;
import org.divulgit.gitlab.error.ErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
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
    private RestCaller restCaller;
    
    @Autowired
    private ProjectMapper mapper;

    @Autowired
    private ErrorMapper errorMapper;

    //getForEntity
    //https://www.baeldung.com/spring-resttemplate-json-list

    public List<GitLabProject> retrieveProjects(final Remote remote, final String token) throws RemoteException {
        final List<GitLabProject> projects = new ArrayList<>();
        retrieveProjects(token, projects, "1");
        return projects;
    }

    private void retrieveProjects(final String token, final List<GitLabProject> projects, final String page) throws RemoteException {
        ResponseEntity<String> response = restCaller.call("https://git.neogrid.com/api/v4/projects?membership=true&per_page=100&page=" + page, token);
        if (response.getStatusCode().is2xxSuccessful()) {
            projects.addAll(handle200Response(response));
        } else if (response.getBody().contains("error_description")) {
            handleErrorResponse(response);
        }
        String nextPage = response.getHeaders().getFirst("x-next-page");
        if (!Strings.isNullOrEmpty(nextPage)) {
            retrieveProjects(token, projects, nextPage);
        }
    }

    private List<GitLabProject> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return mapper.convertToProjects(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }

    private Optional<GitLabProject> handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
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
