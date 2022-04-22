package org.divulgit.vo;

import org.divulgit.model.Project;
import org.divulgit.model.User.UserProject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProjectVO {

    private Project project;
    private UserProject.State state;
    private long commentsTotal;
    private long commentsDiscussed;
    
    public String getId() {
        return project.getId();
    }

    public String getExternalId() {
        return project.getExternalId();
    }

    public String getRemoteId() {
        return project.getRemoteId();
    }

    public String getName() {
        return project.getName();
    }

    public String getUrl() {
        return project.getUrl();
    }

    public String getDescription() {
        return project.getDescription();
    }

    public UserProject.State getState() {
        return state;
    }
    
    public boolean isNew() {
    	return state == UserProject.State.NEW;
    }
    
    public boolean isActive() {
    	return state == UserProject.State.ACTIVE;
    }
    
    public boolean isIgnored() {
    	return state == UserProject.State.IGNORED;
    }
}