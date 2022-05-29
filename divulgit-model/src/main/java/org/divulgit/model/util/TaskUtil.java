package org.divulgit.model.util;

import org.divulgit.model.Task;
import org.divulgit.model.User;
import org.divulgit.model.User.UserProject;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskUtil {

	public static List<String> extractUsersId(List<Task> tasks) {
		return tasks.stream().map(Task::getUserId).distinct().collect(Collectors.toList());
	}
}
