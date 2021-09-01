package br.com.decioluckow.divulgit.model;

import br.com.decioluckow.divulgit.restcaller.user.GitLabUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "user")
public class User {

    @Id
    private String id;

    private String externalUserId;
    private String name;
    private String username;
    private String avatarURL;
    private List<String> projectIds;
    private String originId;

    public static User of(final GitLabUser gitLabUser, final Origin origin) {
        return User.builder()
                .externalUserId(gitLabUser.getInternalId())
                .name(gitLabUser.getName())
                .username(gitLabUser.getUsername())
                .avatarURL(gitLabUser.getAvatarURL())
                .originId(origin.getId()).build();
    }
}
