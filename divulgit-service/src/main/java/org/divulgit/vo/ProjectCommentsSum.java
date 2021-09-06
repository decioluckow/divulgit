package org.divulgit.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.divulgit.model.Project;
import org.divulgit.type.ProjectState;

@Getter
@Builder
@AllArgsConstructor
public class ProjectCommentsSum {

    private Project project;
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

    public int getMergeRequestStart() {
        return project.getMergeRequestStart();
    }

    public ProjectState getState() {
        return project.getState();
    }
}