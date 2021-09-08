package org.divulgit.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.security.UserAuthentication;
import org.divulgit.security.UserDetails;
import org.divulgit.task.RemoteScan;
import org.divulgit.task.ScanExecutor;
import org.divulgit.type.ProjectState;
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
    private RemoteRepository remoteRepository;

    @Autowired
    private ScanExecutor taskExecutor;

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

    @PostMapping("/in/project/{projectId}/scanFrom/{scanFrom}")
    public ResponseEntity<RemoteScan.UniqueKey> scanFrom(
            Authentication authentication,
            @PathVariable String projectId,
            @PathVariable int scanFrom) {
        UserDetails userDetails = getUserDetails(authentication);
        Remote remote = loadRemote(userDetails.getUser().getRemoteId());
        Project project = loadProject(projectId);
        RemoteScan.UniqueKey taskUniqueKey = taskExecutor.scanProjectForMergeRequests(
                remote, project, Optional.of(scanFrom), userDetails.getRemoteToken());
        return ResponseEntity.ok(taskUniqueKey);
    }

    @PostMapping("/in/project/{projectId}/scanFrom/latest")
    public ResponseEntity<RemoteScan.UniqueKey> scanFrom(
            Authentication authentication, @PathVariable String projectId) {

        UserDetails userDetails = getUserDetails(authentication);
        Remote remote = loadRemote(userDetails.getUser().getRemoteId());
        Project project = loadProject(projectId);
        Optional<Integer> emptyScanFrom = Optional.empty();
        RemoteScan.UniqueKey taskUniqueKey = taskExecutor.scanProjectForMergeRequests(remote, project, emptyScanFrom, userDetails.getRemoteToken());

        return ResponseEntity.ok(taskUniqueKey);
    }

    private Project loadProject(String projectId) {
        final Optional<Project> project = projectRepos.findById(projectId);
        if (!project.isPresent()) {
            throw new RuntimeException("Project " + projectId + " not found");
        }
        return project.get();
    }

    private Remote loadRemote(String remoteId) {
        Optional<Remote> remote = remoteRepository.findById(remoteId);
        if (!remote.isPresent()) {
            throw new RuntimeException("Remote not found");
        }
        return remote.get();
    }

    private UserDetails getUserDetails(Authentication authentication) {
        return ((UserAuthentication) authentication).getUserDetails();
    }
}