package org.divulgit.azure.pullrequest;

import org.apache.logging.log4j.util.Strings;
import org.divulgit.azure.user.AzureUser;
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
public class AzurePullRequest implements RemoteMergeRequest {

    private static final String STATE_ACTIVE = "active";
    private static final String STATE_CLOSED = "closed";

    /*
abandoned
string
Pull request is abandoned.

active
string
Pull request is active.

all
string
Used in pull request search criteria to include all statuses.

completed
string
Pull request is completed.

notSet
string
Status not set. Default state.
*/



    @JsonProperty("pullRequestId")
    private int externalId;
    private String title;
    private String description;
    private AzureUser user;
    private String url;
    private String state;
    @JsonProperty("merged_at")
    private String mergedAt;

    /*
    {
        "value": [
        {
            "pullRequestId": 1,
                "status": "active",
                "createdBy": {
            "uniqueName": "decioluckow@outlook.com",
        },
            "creationDate": "2022-07-02T19:46:34.2134471Z",
                "title": "teste1 1'",
                "description": "[1] description do pull request",
                "mergeStatus": "succeeded",

        }
    ],
        "count": 1
    }
    */

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
                .url(url)
                .author(user.getUsername())
                .state(convertState(state)).build();
    }

    public MergeRequest.State convertState(String state) {
        MergeRequest.State mergeRequestState = null;
        /*
        if (STATE_OPENED.equals(state))
            mergeRequestState = MergeRequest.State.OPENED;
        else if (STATE_CLOSED.equals(state) && Strings.isEmpty(mergedAt))
            mergeRequestState = MergeRequest.State.CLOSED;
        else if (STATE_CLOSED.equals(state) && Strings.isNotEmpty(mergedAt))
            mergeRequestState = MergeRequest.State.MERGED;
        else throw new IllegalArgumentException("MergeRequest.State not found for value " + state);

         */
        return mergeRequestState;
    }
}
