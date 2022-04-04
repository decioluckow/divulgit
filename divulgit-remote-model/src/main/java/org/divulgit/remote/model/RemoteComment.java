package org.divulgit.remote.model;

import java.util.List;

import org.divulgit.model.MergeRequest;

public interface RemoteComment {

     String getExternalId();
     String getText();
     String getAuthor();
     String getUrl();
     
     MergeRequest.Comment toComment(List<String> hashTags);
}
