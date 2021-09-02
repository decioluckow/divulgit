package org.divulgit.gitlab.mergerequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.divulgit.remote.remote.model.RemoteMergeRequest;

@Data
public class GitLabMergeRequest implements RemoteMergeRequest {
    @JsonProperty("iid")
    private String externalId;
    private String title;
    private String state;
}
