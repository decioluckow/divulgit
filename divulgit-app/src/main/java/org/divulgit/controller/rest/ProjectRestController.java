package org.divulgit.controller.rest;

import java.util.Optional;

import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.model.User.UserProject;
import org.divulgit.model.util.UserProjectUtil;
import org.divulgit.repository.UserRepository;
import org.divulgit.security.UserAuthentication;
import org.divulgit.task.RemoteScan;
import org.divulgit.task.ScanExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

	@PostMapping("/in/project/{projectId}/ignore")
	public ResponseEntity<String> ignore(Authentication auth, @PathVariable String projectId) {
		log.info("Ignoring project {}", projectId);
		User user = loader.loadUser(auth);
		updateUserProjectState(user, projectId, User.UserProject.State.IGNORED);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/in/project/{projectId}/activate")
	public ResponseEntity<String> activate(Authentication auth, @PathVariable String projectId) {
		log.info("Ignoring project {}", projectId);
		User user = loader.loadUser(auth);
		updateUserProjectState(user, projectId, User.UserProject.State.ACTIVE);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/in/project/{projectId}/scanFrom/{scanFrom}")
	public ResponseEntity<RemoteScan.UniqueKey> scanFrom(Authentication authentication, @PathVariable String projectId,
			@PathVariable int scanFrom) {
		log.info("Start scanning project {} from scan {}", projectId, scanFrom);
		User user = loader.loadUser(authentication);
		updateUserProjectState(user, projectId, User.UserProject.State.ACTIVE);
		Remote remote = loader.loadRemote(user.getRemoteId());
		String remoteToken = ((UserAuthentication) authentication).getRemoteToken();
		Project project = loader.loadProject(user, projectId);
		RemoteScan.UniqueKey taskUniqueKey = taskExecutor.scanProjectForMergeRequests(remote, user, project,
				Optional.of(scanFrom), remoteToken);
		return ResponseEntity.ok(taskUniqueKey);
	}

	private void updateUserProjectState(User user, String projectId, UserProject.State state) {
		UserProject userProject = UserProjectUtil.getUserProject(user, projectId);
		userProject.setState(state);
		userRepository.save(user);
	}

	@PostMapping("/in/project/{projectId}/scanFrom/lastest")
	public ResponseEntity<RemoteScan.UniqueKey> scanLastest(Authentication authentication,
			@PathVariable String projectId) {
		log.info("Start scanning project {} from last scan", projectId);
		User user = loader.loadUser(authentication);
		Remote remote = loader.loadRemote(user.getRemoteId());
		Project project = loader.loadProject(user, projectId);
		String remoteToken = ((UserAuthentication) authentication).getRemoteToken();
		Optional<Integer> emptyScanFrom = Optional.empty();
		RemoteScan.UniqueKey taskUniqueKey = taskExecutor.scanProjectForMergeRequests(remote, user, project,
				emptyScanFrom, remoteToken);
		return ResponseEntity.ok(taskUniqueKey);
	}
}
