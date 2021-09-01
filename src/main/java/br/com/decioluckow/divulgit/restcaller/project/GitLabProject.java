package br.com.decioluckow.divulgit.restcaller.project;

import br.com.decioluckow.divulgit.model.Project;
import lombok.Data;

@Data
public class GitLabProject {
    private String id;
    private String name;
    private String description;

    public Project convertToProject() {
        return Project.builder().externalProjectId(id).name(name).description(description).build();
    }
}
