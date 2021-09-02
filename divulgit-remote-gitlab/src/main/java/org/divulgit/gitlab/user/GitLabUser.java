package org.divulgit.gitlab.user;

import org.divulgit.model.Remote;
import org.divulgit.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.divulgit.remote.remote.model.RemoteUser;

@Data
public class GitLabUser implements RemoteUser {
    @JsonProperty("id")
    private String internalId;
    private String name;
    private String username;
    @JsonProperty("avatar_url")
    private String avatarURL;
}
