package org.divulgit.task.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.divulgit.config.ApplicationContextProvider;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.RemoteCallerFacadeFactory;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteComment;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.service.MergeRequestService;
import org.divulgit.task.AbstractRemoteScan;
import org.divulgit.task.RemoteScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class CommentsRemoteScan extends AbstractRemoteScan {

    private static Pattern HASH_TAG_PATTERN = Pattern.compile("(?:\\s|\\A|^)[##]+([A-Za-z0-9-_]+)");

    @Autowired
    private RemoteCallerFacadeFactory callerFactory;

    @Autowired
    private MergeRequestService mergeRequestService;

    private final Remote remote;
    private final User user;
    private final Project project;
    private final MergeRequest mergeRequest;
    private final String token;

    public static RemoteScan build(Remote remote,
    		 					   User user,
                                   Project project,
                                   MergeRequest mergeRequest,
                                   String token) {
        return (RemoteScan) ApplicationContextProvider.getApplicationContext().getBean("commentsRemoteScan", remote, user, project, mergeRequest, token);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public CommentsRemoteScan(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            String token) {
        this.remote = remote;
        this.user = user;
        this.project = project;
        this.mergeRequest = mergeRequest;
        this.token = token;
    }

    @Override
    public RemoteScan.UniqueKey uniqueKey() {
        return new RemoteScan.UniqueKey("project:" + project.getId() + ", mergeRequest:" + mergeRequest.getId());
    }

    @Override
    public void execute() {
        try {
            log.debug("Start retrieving comments from merge request");
            List<? extends RemoteComment> remoteComments = callerFactory.build(remote).retrieveComments(remote, user, project, mergeRequest, token);
            log.debug("Finished retrieving comments from merge request, {} retrieved", remoteComments.size());
            log.debug("Start merging comments");
            for (RemoteComment remoteComment : remoteComments) {
            	Optional<MergeRequest.Comment> existingComment = findExistingComment(remoteComment);
            	if (existingComment.isPresent()) {
            		updateIfNecessary(remoteComment, existingComment);
            	} else {
            		addNewComment(remoteComment);
            	}
            }
            mergeRequestService.save(mergeRequest);
            log.debug("Finished comments merging");
        } catch (RemoteException e) {
            final String message = "Error executing comments scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }
    }

	private void updateIfNecessary(RemoteComment remoteComment, Optional<MergeRequest.Comment> existingComment) {
		if (!existingComment.get().getText().equals(remoteComment.getText())) {
			List<String> hashTags = extractRelevantHashTags(remoteComment.getText());
			existingComment.get().setText(remoteComment.getText());
		    existingComment.get().setHashTags(hashTags);
		}
	}

	private void addNewComment(RemoteComment remoteComment) {
		List<String> hashTags = extractRelevantHashTags(remoteComment.getText());
		mergeRequest.getComments().add(remoteComment.toComment(hashTags));
	}

    private Optional<MergeRequest.Comment> findExistingComment(RemoteComment remoteComment) {
        return mergeRequest.getComments().stream().filter(c -> c.getExternalId().equals(remoteComment.getExternalId())).findFirst();
    }

    public static List<String> extractRelevantHashTags(String text) {
        return extractHashTag(text);
    }

    //TODO acredito que seja interessante mover este método para uma classe específica
    public static List<String> extractHashTag(String text) {
        var hashTags = new ArrayList<String>();
        final Matcher matcher = HASH_TAG_PATTERN.matcher(text);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                hashTags.add(matcher.group(i));
            }
        }
        return hashTags;
    }
}
