package org.divulgit.util.vo;

import lombok.Data;
import org.divulgit.type.RemoteType;

@Data
public class RemoteIdentify {
    private String username;
    private String domain;
    private RemoteType provider;

    public RemoteIdentify(String username, String domain, String provider) {
        this.username = username;
        this.domain = domain;
        this.provider = RemoteType.valueOf(provider);
    }
}
