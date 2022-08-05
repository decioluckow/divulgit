package org.divulgit.service.remote;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.gitlab.test.GitLabTestCaller;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        //TODO procurar lugar certo para colocar a versão 2.0 (aki e la no front botei o 2.0)
        ensureRegistred("api.bitbucket.org/2.0", RemoteType.BITBUCKET);
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
