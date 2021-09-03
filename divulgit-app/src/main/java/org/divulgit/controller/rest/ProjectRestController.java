package org.divulgit.controller.rest;

import org.divulgit.model.Project;
import org.divulgit.security.UserAuthentication;
import org.divulgit.security.UserDetails;
import org.divulgit.task.TaskExecutor;
import org.divulgit.task.TaskUniqueKey;
import org.divulgit.type.ProjectState;
import org.divulgit.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
public class ProjectRestController {

    @Autowired
    private ProjectRepository projectRepos;

    @Autowired
    private TaskExecutor taskExecutor;

    @PostMapping("/in/project/{projectId}/ignore")
    public ResponseEntity<String> ignore(Authentication auth, @PathVariable String projectId) {
        //TODO verificar se o usuário autenticado tem acesso ao projeto, ou embaralhar id
        Project project = loadProject(projectId);
        project.setState(ProjectState.IGNORED);
        projectRepos.save(project);
        return ResponseEntity.ok("project ignored");
    }

    @PostMapping("/in/project/{projectId}/activate")
    public ResponseEntity<String> activate(Authentication auth, @PathVariable String projectId) {
        //TODO verificar se o usuário autenticado tem acesso ao projeto, ou embaralhar id
        Project project = loadProject(projectId);
        project.setState(ProjectState.ACTIVE);
        projectRepos.save(project);
        return ResponseEntity.ok("project activated");
    }

    @PostMapping("/in/project/{projectId}/setstart/{start}")
    public ResponseEntity<String> setStart(Authentication auth, @PathVariable String projectId, @PathVariable int start) {
        //TODO verificar se o usuário autenticado tem acesso ao projeto, ou embaralhar id
        Project project = loadProject(projectId);
        project.setMergeRequestStart(start);
        projectRepos.save(project);
        return ResponseEntity.ok("start set");
    }

    @PostMapping("/in/project/{projectId}/scan/mergerequests")
    public ResponseEntity<TaskUniqueKey> scanMergeRequests(Authentication authentication, @PathVariable String projectId) {
        //TODO verificar se o usuário autenticado tem acesso ao projeto, ou embaralhar id
        UserDetails userDetails = getUserDetails(authentication);
        Project project = loadProject(projectId);
        TaskUniqueKey taskUniqueKey = taskExecutor.scanProjectForMergeRequests(project, userDetails.getRemoteToken());
        return ResponseEntity.ok(taskUniqueKey);
    }

    private Project loadProject(String projectId) {
        final Optional<Project> project = projectRepos.findById(projectId);
        if (!project.isPresent()) {
            throw new RuntimeException("Project " + projectId + " not found");
        }
        return project.get();
    }

    private UserDetails getUserDetails(Authentication authentication) {
        return ((UserAuthentication) authentication).getUserDetails();
    }
}