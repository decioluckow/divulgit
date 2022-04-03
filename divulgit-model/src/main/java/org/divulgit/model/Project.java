package org.divulgit.model;

import lombok.*;
import org.divulgit.type.ProjectState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "project")
public class Project {

    @Id
    private String id;

    private String externalId;
    private String remoteId;
    private String name;
    private String url;
    private String description;
    private ProjectState state;

}
