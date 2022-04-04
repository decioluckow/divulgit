package org.divulgit.remote.model;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;

public interface RemoteMergeRequest {

     int getExternalId();
     String getTitle();
     String getDescription();
     String getAuthor();
     String getState();
     
     MergeRequest toMergeRequest(Project project); 
}
