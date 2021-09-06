package org.divulgit.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
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
    private List<String> projectIds = new ArrayList<>();
    private String remoteId;

}
