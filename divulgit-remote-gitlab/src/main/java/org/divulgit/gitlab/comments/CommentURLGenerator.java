package org.divulgit.gitlab.comments;

import java.text.MessageFormat;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommentURLGenerator {

    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    public String build(Remote remote, Project project, MergeRequest mergeRequest, String page) {
        return MessageFormat.format("https://{0}/api/v4/projects/{1}/merge_requests/{2}/notes?page={3}",
                remote.getUrl(),
                project.getExternalId(),
                String.valueOf(mergeRequest.getExternalId()),
                page);
    }
}
