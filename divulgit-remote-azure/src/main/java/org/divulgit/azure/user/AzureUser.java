package org.divulgit.azure.user;

import org.divulgit.remote.model.RemoteUser;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AzureUser implements RemoteUser {
    @JsonProperty("id")
    private String internalId;
    private String name;
    @JsonProperty("login")
    private String username;
    @JsonProperty("avatar_url")
    private String avatarURL;
}
