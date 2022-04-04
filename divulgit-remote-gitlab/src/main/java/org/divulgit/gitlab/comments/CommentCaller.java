package org.divulgit.gitlab.comments;

import org.divulgit.model.MergeRequest;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.gitlab.error.ErrorMapper;
import org.divulgit.gitlab.error.ErrorMessage;
import org.divulgit.gitlab.restcaller.GitLabRestCaller;
import org.divulgit.model.Remote;
import org.divulgit.model.Project;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
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
public class CommentCaller {

    public static final String START_PAGE = "1";
    @Autowired
    private GitLabRestCaller restCaller;
    
    @Autowired
    private CommentMapper mapper;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private CommentURLGenerator urlGenerator;

    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    //getForEntity
    //https://www.baeldung.com/spring-resttemplate-json-list

    public List<GitLabComment> retrieveComments(Remote remote, Project project, MergeRequest mergeRequest, String token) throws RemoteException {
        List<GitLabComment> comments = new ArrayList<>();
        retrieveComments(remote, project, mergeRequest, comments, token, START_PAGE);
        comments = removeUseless(comments);
        return comments;
    }
    
    private List<GitLabComment> removeUseless(List<GitLabComment> comments) {
    	return comments.stream().filter(c -> !c.isSystem() && "DiffNote".equals(c.getType()) && c.getText().contains("#")).collect(Collectors.toList());
    }

    private void retrieveComments(Remote remote, Project project, MergeRequest mergeRequest, List<GitLabComment> comments, String token, String page) throws RemoteException {
        String url = urlGenerator.build(remote, project, mergeRequest, page);
        ResponseEntity<String> response = restCaller.call(url, token);
        if (response.getStatusCode().is2xxSuccessful()) {
            comments.addAll(handle200Response(response));
        } else if (response.getBody().contains("error_description")) {
            handleErrorResponse(response);
        }
        String nextPage = response.getHeaders().getFirst("x-next-page");
        if (!Strings.isNullOrEmpty(nextPage)) {
            retrieveComments(remote, project, mergeRequest, comments, token, nextPage);
        }
    }

    private List<GitLabComment> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return mapper.convertFrom(response.getBody());
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
