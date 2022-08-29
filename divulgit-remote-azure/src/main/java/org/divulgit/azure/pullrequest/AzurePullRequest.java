package org.divulgit.azure.pullrequest;

import lombok.*;
import org.divulgit.azure.repository.AzureRepository;
import org.divulgit.azure.thread.AzureAuthor;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.remote.model.RemoteMergeRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.divulgit.util.DateUtil;

import java.time.LocalDateTime;

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

    @Setter
    private String url;

    @JsonProperty("creationDate")
    private String createdAt;

    @Getter
    @JsonProperty("createdBy")
    private AzureAuthor azureAuthor;

    private AzurePullRequestStatus status;

    public String getAuthor() {
        return azureAuthor.getUsername();
    }

    public String getState() {
        return status.getCorrespondingTo().name();
    }

    public MergeRequest toMergeRequest(Project project) {
        return MergeRequest.builder()
                .projectId(project.getId())
                .externalId(externalId)
                .title(title)
                .description(description)
                .createdAt(DateUtil.parseDateFromDateTime(createdAt))
                .url(url)
                .author(getAuthor())
                .state(status.getCorrespondingTo()).build();
    }
}
