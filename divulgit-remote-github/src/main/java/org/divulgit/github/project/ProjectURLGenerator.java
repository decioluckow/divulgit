package org.divulgit.github.project;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Remote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Slf4j
@Component
public class ProjectURLGenerator {

    @Value("${rest.caller.pageSize:50}")
    private int pageSize;

    public String build(Remote remote, String page) {
        return MessageFormat.format("https://{0}/user/repos",
            remote.getUrl(),
            pageSize,
            page);
    }
}
