package org.divulgit.model;

import org.divulgit.type.ProjectState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "project")
public class Project {

    @Id
    private String id;

    private String externalId;
    private String remoteId;
    private String name;
    private String description;
    private int mergeRequestStart;
    private ProjectState state;
}
