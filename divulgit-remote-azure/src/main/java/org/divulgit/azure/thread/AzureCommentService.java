package org.divulgit.azure.thread;

import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.remote.exception.RemoteException;
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

    public List<AzureComment> retrieveComments(
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
        List<AzureThread> threads = azureThreadCaller.retrieveThreads(project, mergeRequest, authentication);
        List<AzureComment> comments = new ArrayList<>();
        for (AzureThread thread : threads) {
            for (AzureComment comment : thread.getComments()) {
                if (comment.getCommentType() == CommentType.TEXT) {
                    comment.setUrl(urlBuilder.buildPullRequestCommentWebURL(project, mergeRequest, thread, comment));
                    comments.add(comment);
                }
            }
        }
        return comments;
    }
}
