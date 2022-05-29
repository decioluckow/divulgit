package org.divulgit.task.comment;

import java.util.List;
import java.util.Optional;

import org.divulgit.config.ApplicationContextProvider;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.RemoteCallerFacadeFactory;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteComment;
import org.divulgit.repository.TaskRepository;
import org.divulgit.service.mergeRequest.MergeRequestService;
import org.divulgit.task.AbstractRemoteScan;
import org.divulgit.task.RemoteScan;
import org.divulgit.task.listener.PersistenceScanListener;
import org.divulgit.util.HashTagIdentifierUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class CommentsRemoteScan extends AbstractRemoteScan {

    @Autowired
    private RemoteCallerFacadeFactory callerFactory;

    @Autowired
    private MergeRequestService mergeRequestService;

    @Autowired
    private TaskRepository taskRepository;

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
    public String mountTitle() {
        return "Scanning comments";
    }

    @Override
    public String mountDetail() {
        return "for merge request " + mergeRequest.getExternalId();
    }

    @Override
    public RemoteScan.UniqueId register() {
        super.addScanListener(new PersistenceScanListener(taskRepository, remote, user));
        return super.register();
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
			List<String> hashTags = HashTagIdentifierUtil.extractHashTag(remoteComment.getText());
			existingComment.get().setText(remoteComment.getText());
		    existingComment.get().setHashTags(hashTags);
		}
	}

	private void addNewComment(RemoteComment remoteComment) {
		List<String> hashTags = HashTagIdentifierUtil.extractHashTag(remoteComment.getText());
        mergeRequest.getComments().add(remoteComment
                .toComment()
                .hashTags(hashTags)
                .state(MergeRequest.Comment.State.VALID)
                .build());
	}

    private Optional<MergeRequest.Comment> findExistingComment(RemoteComment remoteComment) {
        return mergeRequest.getComments().stream().filter(c -> c.getExternalId().equals(remoteComment.getExternalId())).findFirst();
    }
}
