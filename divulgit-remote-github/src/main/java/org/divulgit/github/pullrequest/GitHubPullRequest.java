package org.divulgit.github.pullrequest;

import org.apache.logging.log4j.util.Strings;
import org.divulgit.github.user.GitHubUser;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.remote.model.RemoteMergeRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubPullRequest implements RemoteMergeRequest {

    private static final String STATE_OPENED = "open";
    private static final String STATE_CLOSED = "closed";

    @JsonProperty("number")
    private int externalId;
    private String title;
    @JsonProperty("body")
    private String description;
    private GitHubUser user;
    private String state;
    @JsonProperty("merged_at")
    private String mergedAt;

    @Override
    public String getAuthor() {
        return user.getUsername();
    }

    public MergeRequest toMergeRequest(Project project) {
        return MergeRequest.builder()
                .projectId(project.getId())
                .externalId(externalId)
                .title(title)
                .description(description)
                .author(user.getUsername())
                .state(convertState(state)).build();
    }

    public MergeRequest.State convertState(String state) {
        MergeRequest.State mergeRequestState;
        if (STATE_OPENED.equals(state))
            mergeRequestState = MergeRequest.State.OPENED;
        else if (STATE_CLOSED.equals(state) && Strings.isEmpty(mergedAt))
            mergeRequestState = MergeRequest.State.CLOSED;
        else if (STATE_CLOSED.equals(state) && Strings.isNotEmpty(mergedAt))
            mergeRequestState = MergeRequest.State.MERGED;
        else throw new IllegalArgumentException("MergeRequest.State not found for value " + state);
        return mergeRequestState;
    }
}
