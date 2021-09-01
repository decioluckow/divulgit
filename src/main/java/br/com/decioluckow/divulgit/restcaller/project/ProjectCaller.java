package br.com.decioluckow.divulgit.restcaller.project;

import br.com.decioluckow.divulgit.model.Origin;
import br.com.decioluckow.divulgit.model.Project;
import br.com.decioluckow.divulgit.restcaller.GitLabRestCaller;
import br.com.decioluckow.divulgit.restcaller.error.ErrorMapper;
import br.com.decioluckow.divulgit.restcaller.error.ErrorMessage;
import br.com.decioluckow.divulgit.restcaller.exception.CallerException;
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
    private GitLabRestCaller restCaller;
    
    @Autowired
    private ProjectMapper mapper;

    @Autowired
    private ErrorMapper errorMapper;

    //getForEntity
    //https://www.baeldung.com/spring-resttemplate-json-list

    public List<GitLabProject> retrieveProjects(final Origin origin, final String token) throws CallerException {
        final List<GitLabProject> projects = new ArrayList<>();
        retrieveProjects(token, projects, "1");
        return projects;
    }

    private void retrieveProjects(final String token, final List<GitLabProject> projects, final String page) throws CallerException {
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

    private List<GitLabProject> handle200Response(ResponseEntity<String> response) throws CallerException {
        try {
            return mapper.convertFrom(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new CallerException(message, e);
        }
    }

    private Optional<GitLabProject> handleErrorResponse(ResponseEntity<String> response) throws CallerException {
        try {
            ErrorMessage errorMessage = errorMapper.convertFrom(response.getBody());
            throw new CallerException(errorMessage.getErrorDescription());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new CallerException(message, e);
        }
    }
}
