package org.divulgit.task.comment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
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
    private final Authentication authentication;

    public static RemoteScan build(Remote remote,
    		 					   User user,
                                   Project project,
                                   MergeRequest mergeRequest,
                                   Authentication authentication) {
        return (RemoteScan) ApplicationContextProvider.getApplicationContext().getBean("commentsRemoteScan", remote, user, project, mergeRequest, authentication);
    }

    public CommentsRemoteScan(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) {
        this.remote = remote;
        this.user = user;
        this.project = project;
        this.mergeRequest = mergeRequest;
        this.authentication = authentication;
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
            List<? extends RemoteComment> remoteComments = callerFactory.build(remote).retrieveComments(remote, user, project, mergeRequest, authentication);
            log.debug("Finished retrieving comments from merge request, {} retrieved", remoteComments.size());
            log.debug("Start merging comments");
            for (RemoteComment remoteComment : remoteComments) {
            	Optional<MergeRequest.Comment> existingComment = findExistingComment(remoteComment);
                List<String> hashTags = HashTagIdentifierUtil.extractHashTag(remoteComment.getText());
                if (! hashTags.isEmpty()) {
                    if (existingComment.isPresent()) {
                        updateIfNecessary(remoteComment, hashTags, existingComment);
                    } else {
                        addNewComment(remoteComment, hashTags);
                    }
                } else {
                    removeComment(remoteComment);
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

	private void updateIfNecessary(RemoteComment remoteComment, List<String> hashTags, Optional<MergeRequest.Comment> existingComment) {
		if (!existingComment.get().getText().equals(remoteComment.getText())) {
			existingComment.get().setText(remoteComment.getText());
		    existingComment.get().setHashTags(hashTags);
		}
	}

	private void addNewComment(RemoteComment remoteComment, List<String> hashTags) {
        mergeRequest.getComments().add(remoteComment
                .toComment()
                .hashTags(hashTags)
                .state(MergeRequest.Comment.State.VALID)
                .build());
	}

    private Optional<MergeRequest.Comment> findExistingComment(RemoteComment remoteComment) {
        return mergeRequest.getComments().stream().filter(c -> c.getExternalId().equals(remoteComment.getExternalId())).findFirst();
    }

    private void removeComment(RemoteComment remoteComment) {
        mergeRequest.getComments().removeIf(c -> c.getExternalId().equals(remoteComment.getExternalId()));
    }
}
