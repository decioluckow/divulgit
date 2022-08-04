package org.divulgit.azure.thread;

import org.divulgit.azure.user.AzureUser;
import org.divulgit.model.MergeRequest;
import org.divulgit.remote.model.RemoteComment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AzureThread  {

    private List<Comment> comments;
}
