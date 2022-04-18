package org.divulgit.task.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.divulgit.config.ApplicationContextProvider;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.RemoteCallerFacadeFactory;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteProject;
import org.divulgit.repository.UserRepository;
import org.divulgit.service.ProjectService;
import org.divulgit.task.AbstractRemoteScan;
import org.divulgit.task.RemoteScan;
import org.divulgit.type.ProjectState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class ProjectRemoteScan extends AbstractRemoteScan {

    @Autowired
    private RemoteCallerFacadeFactory callerFactory;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    private Remote remote;
    private User user;
    private String token;

    public ProjectRemoteScan(Remote remote, User user, String token) {
        this.remote = remote;
        this.user = user;
        this.token = token;
    }

    public static ProjectRemoteScan build(Remote remote, User user, String token) {
        return (ProjectRemoteScan) ApplicationContextProvider.getApplicationContext()
                .getBean("projectRemoteScan", remote, user, token);
    }

    @Override
    public RemoteScan.UniqueKey uniqueKey() {
        return new RemoteScan.UniqueKey("remote:" + remote.getId() + "|user:" + user.getId());
    }

    @Override
    public void execute() {
        try {
            log.info("Starting scanning projects for remote {}", remote.getId());
            List<? extends RemoteProject> projects = callerFactory.build(remote).retrieveRemoteProjects(remote, token);
            log.info("Finished scanning, found {} projects", projects.size());
            List<String> existingExternalProjectIds = projectService.findExternalIdByRemote(remote);
            List<Project> newProjects = addNewProjects(remote, projects, existingExternalProjectIds);
            log.info("Found {} new projects", newProjects.size());
            List<String> newUserProjectIds = addNewProjectsToUser(newProjects, user);
            log.info("Added {} new projects to user {}", newUserProjectIds.size(), user.getId());
        } catch (RemoteException e) {
            final String message = "Error executing project scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }
    }

    private List<Project> addNewProjects(Remote remote, final List<? extends RemoteProject> projects, final List<String> existingExternalProjectIds) {
        var newProjects = new ArrayList<Project>();
        for (RemoteProject remoteProject : projects) {
            boolean exist = existingExternalProjectIds.stream().anyMatch(p -> p.equals(remoteProject.getExternalId()));
            if (!exist) {
                final Project project = remoteProject.convertToProject();
                project.setState(ProjectState.NEW);
                project.setRemoteId(remote.getId());
                newProjects.add(project);
            }
        }
        return projectService.saveAll(newProjects);
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
                userRepository.save(freshUser.get());
            }
        }
        return newUserProjectIds;
    }
}
