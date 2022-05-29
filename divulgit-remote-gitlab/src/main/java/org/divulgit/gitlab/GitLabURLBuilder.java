package org.divulgit.gitlab;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.util.URLUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@Component
public class GitLabURLBuilder {
	
	public static final int INITIAL_PAGE = 1;
	
    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    public String buildTestURL(Remote remote) {
        return MessageFormat.format("https://{0}/api/v4/version", remote.getUrl());
    }

    public String buildCommentURL(Remote remote, Project project, MergeRequest mergeRequest, int page) {
        return MessageFormat.format("https://{0}/api/v4/projects/{1}/merge_requests/{2}/notes?page={3}",
                remote.getUrl(),
                project.getExternalId(),
                String.valueOf(mergeRequest.getExternalId()),
                String.valueOf(page));
    }

    public String buildMergeRequestURL(final Remote remote, Project project) {
        return buildMergeRequestURL(remote, project, Collections.EMPTY_LIST, INITIAL_PAGE);
    }

    public String buildMergeRequestURL(final Remote remote, Project project, List<Integer> requestedMergeRequestExternalIds, final int page) {
        String idParams = URLUtil.toListOfParams(requestedMergeRequestExternalIds, "iids[]");
        return MessageFormat.format("https://{0}/api/v4/projects/{1}/merge_requests?per_page={2}&page={3}{4}",
                remote.getUrl(),
                project.getExternalId(),
                String.valueOf(pageSize),
                String.valueOf(page),
                URLUtil.prepareToConcat(idParams, true));
    }

    public String buildProjectURL(Remote remote, int page) {
        return MessageFormat.format("https://{0}/api/v4/projects?membership=true&per_page={1}&page={2}",
                remote.getUrl(),
                String.valueOf(pageSize),
                String.valueOf(page));
    }

    public String buildUserURL(Remote remote) {
        return MessageFormat.format("https://{0}/api/v4/user/", remote.getUrl());
    }
}
