package org.divulgit.task.comment;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.config.ApplicationContextProvider;
import org.divulgit.gitlab.comments.CommentCaller;
import org.divulgit.gitlab.comments.GitLabComment;
import org.divulgit.gitlab.mergerequest.GitLabMergeRequest;
import org.divulgit.gitlab.mergerequest.MergeRequestCaller;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.repository.UserRepository;
import org.divulgit.service.MergeRequestService;
import org.divulgit.task.AbstractRemoteScan;
import org.divulgit.task.RemoteScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
public class CommentsRemoteScan extends AbstractRemoteScan {

    private static Pattern HASH_TAG_PATTERN = Pattern.compile("(^|\\s)(#[A-Za-z\\d-]+)");

    @Autowired
    private CommentCaller caller;

    @Autowired
    private MergeRequestService mergeRequestService;

    private final Remote remote;
    private final Project project;
    private final MergeRequest mergeRequest;
    private final String token;

    public static CommentsRemoteScan build(Remote remote,
                                           Project project,
                                           MergeRequest mergeRequest,
                                           String token) {
        return (CommentsRemoteScan) ApplicationContextProvider.getApplicationContext().getBean("commentsRemoteScan", remote, project, mergeRequest, token);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public CommentsRemoteScan(
            Remote remote,
            Project project,
            MergeRequest mergeRequest,
            String token) {
        this.remote = remote;
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
            log.debug("Start retrieving merge requests from remote");
            List<GitLabComment> remoteComments = caller.retrieveComments(remote, project, mergeRequest, token);
            log.debug("Finished retrieving merge requests from remote, {} retrieved", remoteComments.size());

            log.debug("Start merging merge requests");
            for (GitLabComment remoteComment : remoteComments) {
                if (remoteComment.isSystem() /*DiffNote?*/)
                    continue;
                Optional<MergeRequest.Comment> existingComment = mergeRequest.getComments().stream().filter(c -> c.getExternalId() == remoteComment.getExternalId()).findFirst();
                if (existingComment.isPresent()) {
                    existingComment.get().setText(remoteComment.getText());
                } else {
                    mergeRequest.getComments().add(remoteComment.toComment());
                }
            }
            mergeRequestService.save(mergeRequest);
            log.debug("Finished merge requests merging");
        } catch (RemoteException e) {
            final String message = "Error executing project scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }
    }

    public static List<String> extractHashTag(String text) {
        Matcher matcher = HASH_TAG_PATTERN.matcher(text);
        List<String> hashTags = new ArrayList<>();
        if (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                hashTags.add(matcher.group(i));
            }
        }
        return hashTags;
    }
}
