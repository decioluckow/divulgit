package org.divulgit.gitlab.project;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.util.URLUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Component
public class ProjectURLGenerator {

    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    public String build(Remote remote, String page) {
        return MessageFormat.format("https://{0}/api/v4/projects?membership=true&per_page={1}&page={2}",
            remote.getUrl(),
            pageSize,
            page);
    }
}
