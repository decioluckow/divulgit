package org.divulgit.service.remote;

import org.divulgit.model.Remote;
import org.divulgit.remote.RemoteCallerFacadeFactory;
import org.divulgit.remote.RemoteFacade;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.util.vo.RemoteIdentify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RemoteDiscoveryService {

    @Autowired
    private RemoteCallerFacadeFactory remoteCallerFacadeFactory;

    @Autowired
    private RemoteRepository remoteRepository;

    public Remote findRemote(RemoteIdentify remoteIdentify, String token) throws RemoteException {
        Optional<Remote> remote = remoteRepository.findByUrl(remoteIdentify.getDomain());
        if (!remote.isPresent()) {
            remote = Optional.ofNullable(testAndRegister(remoteIdentify, token));
        }
        return remote.get();
    }

    private Remote testAndRegister(RemoteIdentify remoteIdentify, String token) throws RemoteException {
        Remote remote = Remote.builder().url(remoteIdentify.getDomain()).type(remoteIdentify.getRemoteType()).build();
        RemoteFacade remoteFacade = remoteCallerFacadeFactory.build(remoteIdentify.getRemoteType());
        if (remoteFacade.testAPI(remote, token)) {
            return remoteRepository.save(remote);
        } else {
            throw new RemoteException("It was not possible to identify " + remoteIdentify.getDomain() + " as a api git url reconized by Divulgit");
        }
    }
}
