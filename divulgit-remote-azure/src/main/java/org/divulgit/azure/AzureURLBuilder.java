package org.divulgit.azure;

import java.text.MessageFormat;

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
    
    public String buildIssueComment(Remote remote, User user, Project project, MergeRequest mergeRequest, int page) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/issues/{3}/comments?per_page={4}&page={5}",
            remote.getUrl(),
            user.getUsername(),
            project.getName(),
            mergeRequest.getExternalId(),
            String.valueOf(pageSize),
            String.valueOf(page));
    }
    
    public String buildPullRequestComment(Remote remote, User user, Project project, MergeRequest mergeRequest, int page) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/pulls/{3}/comments?per_page={4}&page={5}",
            remote.getUrl(),
            user.getUsername(),
            project.getName(),
            mergeRequest.getExternalId(),
            String.valueOf(pageSize),
            String.valueOf(page));
    }

    public String buildPullRequestsURL(Remote remote, User user, Project project) {
        return buildPullRequestsURL(remote, user, project, INITIAL_PAGE);
    }

    public String buildPullRequestsURL(Remote remote, User user, Project project, int page) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/pulls?per_page={3}&page={4}",
            remote.getUrl(),
            user.getUsername(),
            project.getName(),
            String.valueOf(pageSize),
            String.valueOf(page));
    }

    public String buildPullRequestURL(Remote remote, User user, Project project, Integer mergeRequestExternalId) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/pulls/{3}",
            remote.getUrl(),
            user.getUsername(),
            project.getName(),
            mergeRequestExternalId);
    }
    
    public String buildRepository(Remote remote, int page) {
        return MessageFormat.format("https://dev.azure.com/{0}/_apis/git/repositories?api-version=6.0", remote.getOrganizationId());
    }
}
