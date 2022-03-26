package org.divulgit.github.user;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Remote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Slf4j
@Component
public class UserURLGenerator {

    public String build(Remote remote) {
        return MessageFormat.format("https://{0}/user", remote.getUrl());
    }
}
