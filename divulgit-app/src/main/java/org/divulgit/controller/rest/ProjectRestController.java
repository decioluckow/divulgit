package org.divulgit.controller.rest;

import java.util.Optional;

import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.model.User.UserProject;
import org.divulgit.model.util.UserProjectUtil;
import org.divulgit.remote.RemoteCallerFacadeFactory;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.repository.UserRepository;
import org.divulgit.task.executor.ScanExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ProjectRestController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ScanExecutor taskExecutor;

	@Autowired
	private EntityLoader loader;

	@Autowired
	private RemoteCallerFacadeFactory remoteCallerFacadeFactory;

	@GetMapping("/in/project/{projectId}/mergerequest/last")
	public ResponseEntity<Integer> lastMergeRequest(Authentication authentication, @PathVariable String projectId) throws RemoteException {
		log.info("Ignoring project {}", projectId);
		User user = loader.loadUser(authentication);
		Remote remote = loader.loadRemote(user.getRemoteId());
		Project project = loader.loadProject(user, projectId);
		Integer lastMergeRequest = remoteCallerFacadeFactory.build(remote).retrieveLastMergeRequestExternalId(remote, user, project, authentication);
		return ResponseEntity.ok(lastMergeRequest);
	}

	@PostMapping("/in/project/{projectId}/ignore")
	public ResponseEntity<String> ignore(Authentication auth, @PathVariable String projectId) {
		log.info("Ignoring project {}", projectId);
		User user = loader.loadUser(auth);
		updateUserProjectState(user, projectId, User.UserProject.State.IGNORED);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/in/project/{projectId}/unignore")
	public ResponseEntity<String> unIgnore(Authentication auth, @PathVariable String projectId) {
		log.info("Ignoring project {}", projectId);
		User user = loader.loadUser(auth);
		updateUserProjectState(user, projectId, UserProject.State.NEW);
		return ResponseEntity.ok().build();
	}

	private void updateUserProjectState(User user, String projectId, UserProject.State state) {
		UserProject userProject = UserProjectUtil.getUserProject(user, projectId);
		userProject.setState(state);
		userRepository.save(user);
	}
}
