package org.divulgit.bitbucket.pullrequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.time.FastDateParser;
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
import org.divulgit.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.TimeZone;

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

    @JsonProperty("created_on")
    private String createdAt;

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
                .createdAt(DateUtil.parseDateFromDateTime(createdAt))
                .state(convertState(state)).build();
    }

    public MergeRequest.State convertState(String state) {
        MergeRequest.State mergeRequestState;
        if (STATE_OPENED.equalsIgnoreCase(state))
            mergeRequestState = MergeRequest.State.OPENED;
        else if (STATE_CLOSED.equalsIgnoreCase(state) && Strings.isEmpty(mergedAt))
            mergeRequestState = MergeRequest.State.CLOSED;
        else if (STATE_CLOSED.equalsIgnoreCase(state) && Strings.isNotEmpty(mergedAt))
            mergeRequestState = MergeRequest.State.MERGED;
        else throw new IllegalArgumentException("MergeRequest.State not found for value " + state);
        return mergeRequestState;
    }
}
