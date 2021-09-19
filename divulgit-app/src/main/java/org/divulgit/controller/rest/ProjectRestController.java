package org.divulgit.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
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
    private ScanExecutor taskExecutor;

    @Autowired
    private EntityLoader loader;

    @PostMapping("/in/project/{projectId}/ignore")
    public ResponseEntity<String> ignore(Authentication auth, @PathVariable String projectId) {
        //TODO verificar se o usuário autenticado tem acesso ao projeto, ou embaralhar id
        User user = loader.loadUser(auth);
        Project project = loader.loadProject(user, projectId);
        project.setState(ProjectState.IGNORED);
        projectRepos.save(project);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/in/project/{projectId}/scanFrom/{scanFrom}")
    public ResponseEntity<RemoteScan.UniqueKey> scanFrom(
            Authentication authentication,
            @PathVariable String projectId,
            @PathVariable int scanFrom) {
        User user = loader.loadUser(authentication);
        Remote remote = loader.loadRemote(user.getRemoteId());
        Project project = loader.loadProject(user, projectId);
        project.setState(ProjectState.ACTIVE);
        projectRepos.save(project);
        String remoteToken = ((UserAuthentication) authentication).getRemoteToken();
        RemoteScan.UniqueKey taskUniqueKey = taskExecutor.scanProjectForMergeRequests(
                remote, project, Optional.of(scanFrom), remoteToken);
        return ResponseEntity.ok(taskUniqueKey);
    }

    @PostMapping("/in/project/{projectId}/scanFrom/lastest")
    public ResponseEntity<RemoteScan.UniqueKey> scanLastest(
            Authentication authentication, @PathVariable String projectId) {
        User user = loader.loadUser(authentication);
        Remote remote = loader.loadRemote(user.getRemoteId());
        Project project = loader.loadProject(user, projectId);
        String remoteToken = ((UserAuthentication) authentication).getRemoteToken();
        Optional<Integer> emptyScanFrom = Optional.empty();
        RemoteScan.UniqueKey taskUniqueKey = taskExecutor.scanProjectForMergeRequests(
                remote, project, emptyScanFrom, remoteToken);
        return ResponseEntity.ok(taskUniqueKey);
    }
}
