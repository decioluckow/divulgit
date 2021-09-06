package org.divulgit.gitlab.user;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Remote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Slf4j
@Component
public class UserURLGenerator {

    public String build(Remote remote) {
        return MessageFormat.format("https://{0}/api/v4/user/", remote.getUrl());
    }
}
