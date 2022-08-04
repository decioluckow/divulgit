package org.divulgit.azure;

import java.text.MessageFormat;

import org.divulgit.azure.thread.Comment;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AzureURLBuilder {

    public static final int INITIAL_PAGE = 1;

    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    public String buildTestURL() {
        return "https://status.dev.azure.com/_apis/status/health?api-version=6.0-preview.1";
    }

    public String buildUserURL() {
        return "https://app.vssps.visualstudio.com/_apis/profile/profiles/me?api-version=6.0";
    }

    public String buildPullRequestComments(String organization, Project project, MergeRequest mergeRequest) {
        return MessageFormat.format("https://dev.azure.com/{0}/_apis/git/repositories/{1}/pullRequests/{2}/threads?api-version=6.0",
                organization,
                project.getExternalId(),
                mergeRequest.getExternalId());
    }

    public String buildPullRequestCommentWebURL(Project project, MergeRequest mergeRequest, Comment comment) {
        return MessageFormat.format("{0}/pullrequest/{1}?_a=files&path={2}&discussionId={3}",
                project.getUrl(),
                mergeRequest.getExternalId(),
                comment.getThreadContext().getFilePath(),
                comment.getExternalId());
    }

    public String buildPullRequestsURL(String organization, Project project) {
        return MessageFormat.format("https://dev.azure.com/{0}/_apis/git/repositories/{1}/pullrequests?api-version=6.0",
                organization,
                project.getExternalId());
    }

    public String buildPullRequestURL(String organization, int pullRequestExternalId) {
        return MessageFormat.format("https://dev.azure.com/{0}/_apis/git/pullrequests/{1}?api-version=6.0",
                organization,
                pullRequestExternalId);
    }

    public String buildRepository(String organization) {
        return MessageFormat.format("https://dev.azure.com/{0}/_apis/git/repositories?api-version=6.0", organization);
    }
}
