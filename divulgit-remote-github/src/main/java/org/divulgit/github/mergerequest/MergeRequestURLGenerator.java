package org.divulgit.github.mergerequest;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.remote.model.RemoteMergeRequest;
import org.divulgit.remote.remote.model.RemoteUser;
import org.divulgit.remote.util.URLUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Component
public class MergeRequestURLGenerator {

    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    public String build(final Remote remote, RemoteUser user, Project project, final String page) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/pulls?per_page={2}&page={3}",
                remote.getUrl(),
                user.getUsername(),
                project.getName(),
                String.valueOf(pageSize),
                page);
    }

    public String build(final Remote remote, RemoteUser user, Project project, Integer mergeRequestExternalId) {
        return MessageFormat.format("https://{0}/repos/{1}/{2}/pulls/{3}",
                remote.getUrl(),
                user.getUsername(),
                project.getName(),
                mergeRequestExternalId);
    }
}
