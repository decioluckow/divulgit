package org.divulgit.remote.remote.model;

public interface RemoteMergeRequest {

     int getExternalId();
     String getTitle();
     String getDescription();
     String getAuthor();
     String getState();
}
