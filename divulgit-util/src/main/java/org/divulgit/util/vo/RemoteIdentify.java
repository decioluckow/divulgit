package org.divulgit.util.vo;

import lombok.Builder;
import lombok.Data;
import org.divulgit.type.RemoteType;

@Data
@Builder
public class RemoteIdentify {
    private String username;
    private String domain;
    private RemoteType remoteType;
}
