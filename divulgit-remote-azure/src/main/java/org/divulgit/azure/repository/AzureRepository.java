package org.divulgit.azure.repository;

import org.divulgit.model.Project;
import org.divulgit.remote.model.RemoteProject;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class AzureRepository implements RemoteProject {

    @JsonProperty("id")
    private String externalId;
    @JsonProperty("webUrl")
    private String url;
    private String name;

    @Override
    public String getDescription() {
        return null;
    }

    public Project convertToProject() {
        return Project.builder().externalId(externalId).url(url).name(name).build();
    }
}
