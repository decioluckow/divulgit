package org.divulgit.gitlab.comments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.gitlab.util.LinkHeaderUtil;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommentCaller {

    @Autowired
    private RestCaller gitLabRestCaller;

    @Autowired
    private GitLabURLBuilder urlBuilder;
    
    @Autowired
    private CommentResponseHandler commentResponseHandler;

    public List<GitLabComment> retrieveComments(Remote remote, Project project, MergeRequest mergeRequest, Authentication authentication)
            throws RemoteException {
        List<GitLabComment> comments = new ArrayList<>();
        retrieveComments(remote, project, mergeRequest, comments, authentication, GitLabURLBuilder.INITIAL_PAGE);
        comments = removeUseless(comments);
        return comments;
    }
    
    private List<GitLabComment> removeUseless(List<GitLabComment> comments) {
    	return comments.stream().filter(c -> !c.isSystem() && "DiffNote".equals(c.getType())).collect(Collectors.toList());
    }

    private void retrieveComments(Remote remote, Project project, MergeRequest mergeRequest,
                                  List<GitLabComment> comments, Authentication authentication, int page) throws RemoteException {
        String url = urlBuilder.buildCommentURL(remote, project, mergeRequest, page);
        ResponseEntity<String> response = gitLabRestCaller.call(url, authentication);
        if (response.getStatusCode().is2xxSuccessful()) {
            comments.addAll(commentResponseHandler.handle200ResponseMultipleResult(response));
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveComments(remote, project, mergeRequest, comments, authentication, ++page);
        }
    }
}
