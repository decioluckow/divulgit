package org.divulgit.security.identify;

import org.apache.commons.lang3.StringUtils;
import org.divulgit.type.RemoteType;
import org.divulgit.util.vo.RemoteIdentify;
import org.json.JSONObject;
import org.springframework.security.authentication.InsufficientAuthenticationException;

public class RemoteIdentifyParser {

    private static final String EMPTY_USERNAME = "";

    public static RemoteIdentify parsePrincipal(String principal) {
        JSONObject json = new JSONObject(principal);
        String username = json.getString("username");
        String domain = json.getString("domain");
        String plataform = json.getString("plataform");
        RemoteIdentify.RemoteIdentifyBuilder builder = RemoteIdentify.builder();
        if (! json.isNull("organization")) builder.organization(json.getString("organization"));
        if (StringUtils.isNotEmpty(username)) builder.username(username);
        builder.domain(domain);
        builder.remoteType(RemoteType.valueOf(plataform));
        return builder.build();
    }
}
