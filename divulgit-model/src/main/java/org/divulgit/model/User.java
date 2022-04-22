package org.divulgit.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    @Builder.Default
    private List<UserProject> userProjects = new ArrayList<>();
    private String remoteId;
    
    @Getter
    @Setter
    @Builder
    public static class UserProject {
        private String projectId;
        private State state;
        
        public static enum State {
        	NEW, ACTIVE, IGNORED;
        }
    }


}
