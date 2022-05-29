package org.divulgit.service.remote;

import com.google.common.collect.ImmutableList;
import org.divulgit.gitlab.test.GitLabTestCaller;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RemoteDiscoveryService {

    @Autowired
    private GitLabTestCaller gitLabTestCaller;

    @Autowired
    private RemoteRepository remoteRepository;

    public Remote findRemote(String url, String token) throws RemoteException {
        Optional<Remote> remote = remoteRepository.findByUrl(url);
        if (! remote.isPresent()) {
            remote = Optional.ofNullable(testAndRegister(url, token));
        }
        return remote.get();
    }

    private Remote testAndRegister(String url, String token) throws RemoteException {
        /*
         * Right now the only git provider that could be on-premisse is GiLab. Because of that, if we receive
         * a URL that is not found on base, it´s necessary to check only on gitlab to garantee that it really exists
         * to be registred.
         */
        Remote remote = new Remote(url);
        boolean isGitLab = gitLabTestCaller.test(remote, token);
        if (isGitLab) {
            remote.setType(RemoteType.GITLAB);
            return remoteRepository.save(remote);
        } else {
            throw new RemoteException("It was not possible to identify "+ url +" as a api git url reconized by Divulgit");
        }

    }
}
