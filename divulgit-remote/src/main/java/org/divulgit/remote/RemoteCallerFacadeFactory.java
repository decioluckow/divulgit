package org.divulgit.remote;

import org.divulgit.annotation.ForRemote;
import org.divulgit.model.Remote;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoteCallerFacadeFactory {

    @Autowired
    @ForRemote(RemoteType.GITLAB)
    private RemoteFacade gitLabCallerFacade;

    @Autowired
    @ForRemote(RemoteType.GITHUB)
    private RemoteFacade gitHubCallerFacade;

    @Autowired
    @ForRemote(RemoteType.AZURE)
    private RemoteFacade azureCallerFacade;

    public RemoteFacade build(Remote remote) {
        return build(remote.getType());
    }

    public RemoteFacade build(RemoteType type) {
        switch (type) {
            case AZURE: return azureCallerFacade;
            case GITLAB: return gitLabCallerFacade;
            case GITHUB: return gitHubCallerFacade;
            default:
                throw new IllegalArgumentException("Opcão "+ type +" não implementada");
        }
    }
}
