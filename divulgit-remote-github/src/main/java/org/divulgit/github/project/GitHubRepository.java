package org.divulgit.github.project;

import org.divulgit.model.Project;
import org.divulgit.remote.model.RemoteProject;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class GitHubRepository implements RemoteProject {

    @JsonProperty("id")
    private String externalId;
    @JsonProperty("html_url")
    private String url;
    private String name;
    private String description;

    public Project convertToProject() {
        return Project.builder().externalId(externalId).url(url).name(name).description(description).build();
    }
}
