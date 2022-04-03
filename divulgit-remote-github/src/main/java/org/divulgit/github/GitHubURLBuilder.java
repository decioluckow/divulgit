package org.divulgit.github;

import java.text.MessageFormat;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.model.RemoteUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GitHubURLBuilder {
	
	public static final int INITIAL_PAGE = 1;
	
    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    public String buildUserURL(Remote remote) {
        return MessageFormat.format("https://{0}/user", remote.getUrl());
    }
    
    public String buildIssueComment(Remote remote, RemoteUser user, Project project, MergeRequest mergeRequest, int page) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/issues/{3}/comments?per_page={4}&page={5}",
                remote.getUrl(),
                user.getUsername(),
                project.getName(),
                mergeRequest.getExternalId(),
                String.valueOf(pageSize),
                page);
    }
    
    public String buildPullRequestComment(Remote remote, RemoteUser user, Project project, MergeRequest mergeRequest, int page) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/pulls/{3}/comments?per_page={4}&page={5}",
                remote.getUrl(),
                user.getUsername(),
                project.getName(),
                mergeRequest.getExternalId(),
                String.valueOf(pageSize),
                page);
    }
    
    public String buildPullRequestsURL(Remote remote, RemoteUser user, Project project, int page) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/pulls?per_page={2}&page={3}",
                remote.getUrl(),
                user.getUsername(),
                project.getName(),
                String.valueOf(pageSize),
                page);
    }

    public String buildPullRequestURL(Remote remote, RemoteUser user, Project project, Integer mergeRequestExternalId) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/pulls/{3}",
                remote.getUrl(),
                user.getUsername(),
                project.getName(),
                mergeRequestExternalId);
    }
    
    public String buildRepository(Remote remote, int page) {
        return MessageFormat.format("https://{0}/user/repos",
            remote.getUrl(),
            pageSize,
            page);
    }
}
