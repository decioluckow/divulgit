package br.com.decioluckow.divulgit.restcaller.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitLabUser {
    @JsonProperty("id")
    private String internalId;
    private String name;
    private String username;
    @JsonProperty("avatar_url")
    private String avatarURL;
}
