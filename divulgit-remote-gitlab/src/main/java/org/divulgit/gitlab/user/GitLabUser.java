package org.divulgit.gitlab.user;

import org.divulgit.remote.model.RemoteUser;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GitLabUser implements RemoteUser {
	
    @JsonProperty("id")
    private String internalId;
    private String name;
    private String username;
    @JsonProperty("avatar_url")
    private String avatarURL;
}
