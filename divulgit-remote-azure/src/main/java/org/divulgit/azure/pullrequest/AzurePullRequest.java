package org.divulgit.azure.pullrequest;

import lombok.*;
import org.divulgit.azure.repository.AzureRepository;
import org.divulgit.azure.thread.AzureAuthor;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.remote.model.RemoteMergeRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AzurePullRequest implements RemoteMergeRequest {

    @Getter
    @JsonProperty("pullRequestId")
    private int externalId;

    private AzureRepository repository;

    @Getter
    private String title;

    @Getter
    private String description;

    @Getter
    @JsonProperty("createdBy")
    private AzureAuthor azureAuthor;

    private AzurePullRequestStatus status;

    public String getAuthor() {
        return azureAuthor.getUsername();
    }

    //TODO mudar para enum do divulgit
    public String getState() {
        return status.getCorrespondingTo().name();
    }

    public MergeRequest toMergeRequest(Project project) {
        return MergeRequest.builder()
                .projectId(project.getId())
                .externalId(externalId)
                .title(title)
                .description(description)
                .url(repository.getUrl() + "/" + externalId)
                .author(getAuthor())
                .state(status.getCorrespondingTo()).build();
    }
}
