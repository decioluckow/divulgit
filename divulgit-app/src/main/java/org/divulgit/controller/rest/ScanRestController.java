package org.divulgit.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.model.User.UserProject;
import org.divulgit.model.util.UserProjectUtil;
import org.divulgit.remote.RemoteCallerFacadeFactory;
import org.divulgit.repository.UserRepository;
import org.divulgit.task.RemoteScan;
import org.divulgit.task.executor.ScanExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class ScanRestController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ScanExecutor taskExecutor;

	@Autowired
	private EntityLoader loader;

	@Autowired
	private RemoteCallerFacadeFactory remoteCallerFacadeFactory;

	@PostMapping("/in/project/{projectId}/scanFrom/{scanFrom}")
	public ResponseEntity<RemoteScan.UniqueId> scanFrom(
			Authentication authentication, @PathVariable String projectId, @PathVariable int scanFrom) {
		log.info("Start scanning project {} from scan {}", projectId, scanFrom);
		User user = loader.loadUser(authentication);
		updateUserProjectState(user, projectId, UserProject.State.ACTIVE);
		Remote remote = loader.loadRemote(user.getRemoteId());
		Project project = loader.loadProject(user, projectId);
		RemoteScan.UniqueId taskUniqueId = taskExecutor.scanProjectForMergeRequests(remote, user, project,
				Optional.of(scanFrom), authentication);
		return ResponseEntity.ok(taskUniqueId);
	}

	private void updateUserProjectState(User user, String projectId, UserProject.State state) {
		UserProject userProject = UserProjectUtil.getUserProject(user, projectId);
		userProject.setState(state);
		userRepository.save(user);
	}

	@PostMapping("/in/project/{projectId}/scanFrom/lastest")
	public ResponseEntity<Map<String, RemoteScan.UniqueId>> scanLastest(
			Authentication authentication, @PathVariable String projectId) {
		log.info("Start scanning project {} from last scan", projectId);
		User user = loader.loadUser(authentication);
		Remote remote = loader.loadRemote(user.getRemoteId());
		Project project = loader.loadProject(user, projectId);
		Optional<Integer> emptyScanFrom = Optional.empty();
		RemoteScan.UniqueId rescanOpenedUniqueId = taskExecutor.rescanOpenedMergeRequests(
				remote, user, project, authentication);
		RemoteScan.UniqueId scanLastUniqueId = taskExecutor.scanProjectForMergeRequests(
				remote, user, project, emptyScanFrom, authentication);
		return new ResponseEntity<Map<String, RemoteScan.UniqueId>>(
				Map.of("rescanOpenedUniqueId", rescanOpenedUniqueId, "scanLastUniqueId", scanLastUniqueId),
				HttpStatus.OK) ;
	}
}
