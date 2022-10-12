package org.divulgit.gitlab.comments;

import java.util.List;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class GitLabCommentService {
	
    @Autowired
    private CommentCaller commentCaller;
    
    public List<? extends RemoteComment> retrieveComments(Remote remote, Project project, MergeRequest mergeRequest,
    		Authentication authentication) throws RemoteException {
    	List<GitLabComment> comments = commentCaller.retrieveComments(remote, project, mergeRequest, authentication);
    	comments.stream().forEach(comment -> fillCommentURL(project, mergeRequest, comment));
		return comments;
    }

    private void fillCommentURL(Project project, MergeRequest mergeRequest, GitLabComment comment) {
        var url = "[projectURL]/merge_requests/[mergeRequestId]#note_[commentId]";
        var commentURL = url.replace("[projectURL]", project.getUrl());
        commentURL = commentURL.replace("[mergeRequestId]", String.valueOf(mergeRequest.getExternalId()));
        commentURL = commentURL.replace("[commentId]", comment.getExternalId());
        comment.setUrl(commentURL);
    }
}
