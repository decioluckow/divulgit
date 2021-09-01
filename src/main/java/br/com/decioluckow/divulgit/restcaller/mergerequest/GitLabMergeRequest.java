package br.com.decioluckow.divulgit.restcaller.mergerequest;

import br.com.decioluckow.divulgit.model.Project;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitLabMergeRequest {
    @JsonProperty("iid")
    private String externalId;
    private String title;
    private String state;
}
