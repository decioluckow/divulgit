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
    private RemoteCallerFacade gitLabCallerFacade;

    public RemoteCallerFacade build(Remote remote) {
        switch (remote.getType()) {
            case GITLAB: return gitLabCallerFacade;
            case GITHUB:
            default:
                throw new RuntimeException("Opcao nao implementada");
        }
    }
}
