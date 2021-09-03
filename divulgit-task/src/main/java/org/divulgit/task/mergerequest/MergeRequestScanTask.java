package org.divulgit.task.mergerequest;

import org.divulgit.gitlab.mergerequest.MergeRequestCaller;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.task.Task;
import org.divulgit.task.TaskUniqueKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("prototype")
public class MergeRequestScanTask extends Task {

    @Autowired
    private MergeRequestCaller caller;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Remote remote;
    private Project project;
    private String token;

    //@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MergeRequestScanTask(Remote remote, Project project, String token) {
        this.remote = remote;
        this.project = project;
        this.token = token;
    }

    @Override
    public TaskUniqueKey uniqueKey() {
        return new TaskUniqueKey("project:" + project.getId());
    }

    @Override
    public void execute() {
    }
}
