package org.divulgit.azure.pullrequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.divulgit.model.MergeRequest;

@AllArgsConstructor
public enum AzurePullRequestStatus {
    ABANDONED(MergeRequest.State.CLOSED),
    ACTIVE(MergeRequest.State.OPENED),
    COMPLETED(MergeRequest.State.MERGED),
    NOTSET(MergeRequest.State.OPENED);

    @Getter
    private MergeRequest.State correspondingTo;

    @JsonCreator
    public static AzurePullRequestStatus getEnumFromValue(String value) {
        for (AzurePullRequestStatus state : values()) {
            if (state.name().toLowerCase().equals(value.toLowerCase())) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid value " + value);
    }
}
