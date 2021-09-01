package br.com.decioluckow.divulgit.model;

import br.com.decioluckow.divulgit.model.type.ProjectState;
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

    private String externalProjectId;
    private String repositoryId;
    private String name;
    private String description;
    private int mergeRequestStart;
    private ProjectState state;
}
