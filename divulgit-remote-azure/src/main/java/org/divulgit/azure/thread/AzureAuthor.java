package org.divulgit.azure.thread;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.divulgit.remote.model.RemoteUser;

@Data
public class AzureAuthor {
    @JsonProperty("id")
    private String uid;
    @JsonProperty("displayName")
    private String name;
    @JsonProperty("uniqueName")
    private String username;
}
