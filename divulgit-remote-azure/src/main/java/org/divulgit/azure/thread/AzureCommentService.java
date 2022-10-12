package org.divulgit.azure.thread;

import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AzureCommentService {

    @Autowired
    private AzureThreadCaller azureThreadCaller;

    @Autowired
    private AzureURLBuilder urlBuilder;

    public List<RemoteComment> retrieveComments(
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
        List<AzureThread> threads = azureThreadCaller.retrieveThreads(project, mergeRequest, authentication);
        List<RemoteComment> comments = new ArrayList<>();
        for (AzureThread thread : threads) {
            for (AzureComment comment : thread.getComments()) {
                if (comment.getCommentType() == CommentType.TEXT) {
                    comments.add(buildRemoteComment(project, mergeRequest, thread, comment));
                }
            }
        }
        return comments;
    }

    private RemoteComment buildRemoteComment(Project project, MergeRequest mergeRequest, AzureThread thread, AzureComment comment) {
        return new RemoteComment() {
            @Override
            public String getExternalId() {
                return comment.getId() + "@" + thread.getId();
            }

            @Override
            public String getText() {
                return comment.getText();
            }

            @Override
            public String getAuthor() {
                return comment.getAzureAuthor().getUsername();
            }

            @Override
            public String getUrl() {
                return urlBuilder.buildPullRequestCommentWebURL(project, mergeRequest, thread, comment);
            }

            @Override
            public MergeRequest.Comment.CommentBuilder toComment() {
                return MergeRequest.Comment.builder()
                        .externalId(getExternalId())
                        .text(getText())
                        .url(getUrl())
                        .author(getAuthor());
            }
        };
    }
}
