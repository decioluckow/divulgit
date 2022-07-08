package org.divulgit.security.identify;

import org.divulgit.util.vo.RemoteIdentify;
import org.springframework.security.authentication.InsufficientAuthenticationException;

public class RemoteIdentifyParser {

    public static RemoteIdentify parsePrincipal(String principal) {
        String[] remoteIdentifyData = principal.split(" at | of ");
        if (remoteIdentifyData.length == 3) {
            return new RemoteIdentify(remoteIdentifyData[0], remoteIdentifyData[1], remoteIdentifyData[2]);
        } else {
            throw new InsufficientAuthenticationException("Não foi possível interpretar o principal '" + principal + "'");
        }
    }


}
