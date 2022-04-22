package org.divulgit.model.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.divulgit.model.User;
import org.divulgit.model.User.UserProject;

public class UserProjectUtil {

	public static UserProject.State getState(User user, String projectId) {
		return getUserProject(user.getUserProjects(), projectId).getState();
	}

	public static UserProject.State getState(List<UserProject> userProjects, String projectId) {
		return getUserProject(userProjects, projectId).getState();
	}

	public static UserProject getUserProject(User user, String projectId) {
		return getUserProject(user.getUserProjects(), projectId);
	}
	
	public static UserProject getUserProject(List<UserProject> userProjects, String projectId) {
		Optional<UserProject> userProject = userProjects.stream().filter(up -> up.getProjectId().equals(projectId)).findFirst();
		if (userProject.isPresent()) {
			return userProject.get();
		} else {
			throw new IllegalStateException("Project " + projectId + " not found on user");
		}
	}

	public static List<String> findByState(User user, UserProject.State... states) {
		List<UserProject> userProjects = user.getUserProjects();
		return userProjects.stream().filter(up -> Arrays.asList(states).stream().anyMatch(s -> s == up.getState()))
				.map(UserProject::getProjectId).collect(Collectors.toList());
	}

}
