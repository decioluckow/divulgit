package org.divulgit.github.mergerequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.remote.remote.model.RemoteMergeRequest;

@Data
@Builder
public class GitHubMergeRequest implements RemoteMergeRequest {

    private static final String STATE_OPENED = "open";
    private static final String STATE_CLOSED = "closed";

    @JsonProperty("number")
    private int externalId;
    private String title;
    @JsonProperty("body")
    private String description;
    private User user;
    private String state;
    @JsonProperty("merged_at")
    private String mergedAt;

    @Override
    public String getAuthor() {
        return user.getLogin();
    }

    @Data
    public static class User {
        private String login;
    }

    public MergeRequest toMergeRequest(Project project) {
        return MergeRequest.builder()
                .projectId(project.getId())
                .externalId(externalId)
                .title(title)
                .description(description)
                .author(user.getLogin())
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
