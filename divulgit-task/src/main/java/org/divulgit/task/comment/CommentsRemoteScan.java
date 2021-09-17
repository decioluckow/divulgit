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

    private static Pattern HASH_TAG_PATTERN = Pattern.compile("(?:\\s|\\A|^)[##]+([A-Za-z0-9-_]+)");

    @Autowired
    private CommentCaller caller;

    @Autowired
    private MergeRequestService mergeRequestService;

    private final Remote remote;
    private final Project project;
    private final MergeRequest mergeRequest;
    private final String token;

    public static RemoteScan build(Remote remote,
                                           Project project,
                                           MergeRequest mergeRequest,
                                           String token) {
        return (RemoteScan) ApplicationContextProvider.getApplicationContext().getBean("commentsRemoteScan", remote, project, mergeRequest, token);
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
            log.debug("Start retrieving comments from merge request");
            List<GitLabComment> remoteComments = caller.retrieveComments(remote, project, mergeRequest, token);
            log.debug("Finished retrieving comments from merge request, {} retrieved", remoteComments.size());

            //TODO depois de gastar o tempo de busca dos comentarios, deveriamos recarregar o merge request?
            // alguém pode ter alterado o registro neste tempo?

            log.debug("Start merging comments");
            for (GitLabComment remoteComment : remoteComments) {
                if (remoteComment.isSystem() || !"DiffNote".equals(remoteComment.getType()) || !remoteComment.getText().contains("#"))
                    continue;
                var hashTags = extractRelevantHashTags(remoteComment.getText());
                if (!hashTags.isEmpty()) {
                    Optional<MergeRequest.Comment> existingComment = findExistingComment(remoteComment);
                    if (existingComment.isPresent()) {
                        existingComment.get().setText(remoteComment.getText());
                        existingComment.get().setHashTags(hashTags);
                    } else {
                        mergeRequest.getComments().add(remoteComment.toComment(hashTags));
                    }
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

    private Optional<MergeRequest.Comment> findExistingComment(GitLabComment remoteComment) {
        return mergeRequest.getComments().stream().filter(c -> c.getExternalId() == remoteComment.getExternalId()).findFirst();
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
