package org.divulgit.gitlab.user;

import java.text.MessageFormat;

import org.divulgit.model.Remote;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
public class UserURLGenerator {

    public String build(Remote remote) {
        return MessageFormat.format("https://{0}/api/v4/user/", remote.getUrl());
    }
}
