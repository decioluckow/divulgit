package org.divulgit.bitbucket;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.text.MessageFormat;

@Component
public class BitBucketURLBuilder {

    public static final int INITIAL_PAGE = 1;

    public static final String CURRENT_API_VERSION = "2.0";

    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    public String buildTestURL(Remote remote) {
        return MessageFormat.format("https://{0}/zen", getUrlApi(remote.getUrl()));
    }

    public String buildUserURL(Remote remote) {
        return MessageFormat.format("https://{0}/user", getUrlApi(remote.getUrl()));
    }

    public String buildIssueComment(Remote remote, User user, Project project, MergeRequest mergeRequest, int page) {
        return MessageFormat.format("https://{0}/repositories/{1}/{2}/issues/{3}/comments?per_page={4}&page={5}",
                getUrlApi(remote.getUrl()),
                user.getUsername(),
                project.getName(),
                mergeRequest.getExternalId(),
                String.valueOf(pageSize),
                String.valueOf(page));
    }

    public String buildPullRequestComment(Remote remote, User user, Project project, MergeRequest mergeRequest) {
        return MessageFormat.format("https://{0}/repositories/{1}/{2}/pullrequests/{3}/comments?per_page={4}",
                getUrlApi(remote.getUrl()),
                user.getUsername(),
                project.getName(),
                mergeRequest.getExternalId(),
                String.valueOf(pageSize));
    }

    public String buildPullRequestsURL(Remote remote, User user, Project project) {
        return MessageFormat.format("https://{0}/repositories/{1}/{2}/pullrequests?per_page={3}",
                getUrlApi(remote.getUrl()),
                user.getUsername(),
                project.getName(),
                String.valueOf(pageSize));
    }

    public String buildPullRequestURL(Remote remote, User user, Project project, Integer mergeRequestExternalId) {
        return MessageFormat.format("https://{0}/repositories/{1}/{2}/pullrequests/{3}",
                getUrlApi(remote.getUrl()),
                user.getUsername(),
                project.getName(),
                mergeRequestExternalId);
    }

    public String buildRepository(Remote remote, String workspace) {
        return MessageFormat.format("https://{0}/repositories/{1}?per_page={2}",
                getUrlApi(remote.getUrl()),
                workspace,
                String.valueOf(pageSize));
    }

    private String getUrlApi(String urlRemote) {
        return urlRemote.concat("/").concat(CURRENT_API_VERSION);
    }
}
