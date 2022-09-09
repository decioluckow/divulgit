package org.divulgit.service.remote;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Remote;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class RemoteService {

    @Autowired
    private RemoteRepository remoteRepository;

    @PostConstruct
    public void populate() {
        ensureRegistred("gitlab.com", RemoteType.GITLAB);
        ensureRegistred("api.github.com", RemoteType.GITHUB);
        ensureRegistred("dev.azure.com", RemoteType.AZURE);
        ensureRegistred("api.bitbucket.org", RemoteType.BITBUCKET);
    }

    private void ensureRegistred(String url, RemoteType type) {
        log.info("Ensuring the remote {}", url);
        boolean exists = remoteRepository.findByUrl(url).isPresent();
        if (! exists) {
            remoteRepository.save(Remote.builder().url(url).type(type).build());
            log.debug("New remote {} created", url);
        }
    }
}
