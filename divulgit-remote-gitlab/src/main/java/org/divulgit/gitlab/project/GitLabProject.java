package org.divulgit.gitlab.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.divulgit.model.Project;
import lombok.Data;
import org.divulgit.remote.model.RemoteProject;

@Getter
@Data
public class GitLabProject implements RemoteProject {

    @JsonProperty("id")
    private String externalId;
    @JsonProperty("web_url")
    private String url;
    private String name;
    private String description;

    @Override
    public Project convertToProject() {
        return Project.builder().externalId(externalId).url(url).name(name).description(description).build();
    }
}
