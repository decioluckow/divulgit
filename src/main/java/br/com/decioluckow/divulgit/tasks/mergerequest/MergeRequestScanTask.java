package br.com.decioluckow.divulgit.tasks.mergerequest;

import br.com.decioluckow.divulgit.model.Origin;
import br.com.decioluckow.divulgit.model.Project;
import br.com.decioluckow.divulgit.model.User;
import br.com.decioluckow.divulgit.model.type.ProjectState;
import br.com.decioluckow.divulgit.repository.ProjectRepository;
import br.com.decioluckow.divulgit.repository.UserRepository;
import br.com.decioluckow.divulgit.restcaller.exception.CallerException;
import br.com.decioluckow.divulgit.restcaller.project.GitLabProject;
import br.com.decioluckow.divulgit.restcaller.project.ProjectCaller;
import br.com.decioluckow.divulgit.tasks.Task;
import br.com.decioluckow.divulgit.tasks.TaskUniqueKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Scope("prototype")
public class MergeRequestScanTask extends Task {

    @Autowired
    private ProjectCaller caller;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Project project;
    private String token;

    public MergeRequestScanTask(Project project, String token) {
        this.project = project;
        this.token = token;
    }

    @Override
    public TaskUniqueKey uniqueKey() {
        return new TaskUniqueKey("project:" + project.getId());
    }

    @Override
    public void execute() {
        try {
            log.info("Starting scanning mergeRequests for project {}", project.getId());
            List<GitLabProject> projects = caller.retrieveProjects(origin, token);
            log.info("Finished scanning, found {} projects", projects);
            List<String> existingExternalProjectIds = projectRepository.findExternalProjectIdByRepositoryId(origin.getId());
            List<Project> newProjects = addNewProjects(projects, existingExternalProjectIds);
            log.info("Found {} new projects", newProjects.size());
            List<String> newUserProjectIds = addNewProjectsToUser(newProjects, user);
            log.info("Added {} new projects to user {}", newUserProjectIds.size(), user.getId());
        } catch (CallerException e) {
            final String message = "Error executing project scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }
    }

    private List<Project> addNewProjects(final List<GitLabProject> projects, final List<String> existingExternalProjectIds) {
        var newProjects = new ArrayList<Project>();
        for (GitLabProject originProject: projects) {
            boolean exist = existingExternalProjectIds.stream().anyMatch(p -> p.equals(originProject.getId()));
            if (!exist) {
                final Project project = originProject.convertToProject();
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
