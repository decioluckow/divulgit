package org.divulgit.bitbucket.pullrequest;
import org.divulgit.bitbucket.Links;
import org.divulgit.bitbucket.user.BitBucketUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.remote.model.RemoteMergeRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BitBucketPullRequest implements RemoteMergeRequest {

    private static final String STATE_OPENED = "open";
    private static final String STATE_CLOSED = "closed";
    @JsonProperty("id")
    private int externalId;
    private String title;
    private String description;
    private BitBucketUser author;
    private String state;
    @JsonProperty("merge_commit")
    private String mergedAt;

    private Links links;

    @Override
    public String getAuthor() {
        return author.getNickName();
    }

    public String getUrl() {
        return getLinks().getHtml().getHref();
    }

    public MergeRequest toMergeRequest(Project project) {
        return MergeRequest.builder()
                .projectId(project.getId())
                .externalId(externalId)
                .title(title)
                .description(description)
                .url(getUrl())
                .author(author.getNickName())
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
