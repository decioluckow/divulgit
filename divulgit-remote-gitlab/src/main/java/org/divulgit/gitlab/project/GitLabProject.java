package org.divulgit.gitlab.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.divulgit.model.Project;
import lombok.Data;
import org.divulgit.remote.remote.model.RemoteProject;

@Getter
@Data
public class GitLabProject implements RemoteProject {

    @JsonProperty("id")
    private String externalId;
    private String name;
    private String description;

    public Project convertToProject() {
        return Project.builder().externalId(externalId).name(name).description(description).build();
    }
}
