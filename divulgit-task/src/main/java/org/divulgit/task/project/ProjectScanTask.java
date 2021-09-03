package org.divulgit.task.project;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.gitlab.project.GitLabProject;
import org.divulgit.gitlab.project.ProjectCaller;
import org.divulgit.model.Remote;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.type.ProjectState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Scope("prototype")
public class ProjectScanTask extends Task {

    @Autowired
    private ProjectCaller caller;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Remote remote;
    private User user;
    private String token;

    public ProjectScanTask(Remote remote, User user, String token) {
        this.remote = remote;
        this.user = user;
        this.token = token;
    }

    @Override
    public UniqueKey uniqueKey() {
        return new UniqueKey("remote:" + remote.getId() + "|user:" + user.getId());
    }

    @Override
    public void execute() {
        try {
            log.info("Starting scanning projects for remote {}", remote.getId());
            List<GitLabProject> projects = caller.retrieveProjects(remote, token);
            log.info("Finished scanning, found {} projects", projects);
            List<String> existingExternalProjectIds = projectRepository.findExternalProjectIdByRemoteId(remote.getId());
            List<Project> newProjects = addNewProjects(projects, existingExternalProjectIds);
            log.info("Found {} new projects", newProjects.size());
            List<String> newUserProjectIds = addNewProjectsToUser(newProjects, user);
            log.info("Added {} new projects to user {}", newUserProjectIds.size(), user.getId());
        } catch (RemoteException e) {
            final String message = "Error executing project scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }
    }

    private List<Project> addNewProjects(final List<GitLabProject> projects, final List<String> existingExternalProjectIds) {
        var newProjects = new ArrayList<Project>();
        for (GitLabProject remoteProject: projects) {
            boolean exist = existingExternalProjectIds.stream().anyMatch(p -> p.equals(remoteProject.getExternalId()));
            if (!exist) {
                final Project project = remoteProject.convertToProject();
                project.setState(ProjectState.NEW);
                project.setMergeRequestStart(0);
                newProjects.add(project);
            }
        }
        projectRepository.saveAll(newProjects);
        return newProjects;
    }

    private List<String> addNewProjectsToUser(final List<Project> newProjects, final User user) {
        var newUserProjectIds = new ArrayList<String>();
        for (Project project : newProjects) {
            boolean exist = user.getProjectIds().stream().anyMatch(id -> id.equals(project.getId()));
            if (!exist) {
                newUserProjectIds.add(project.getId());
            }
        }
        if (!newUserProjectIds.isEmpty()) {
            Optional<User> freshUser = userRepository.findById(user.getId());
            if (freshUser.isPresent()) {
                freshUser.get().getProjectIds().addAll(newUserProjectIds);
            }
            userRepository.save(user);
        }
        return newUserProjectIds;
    }
}
