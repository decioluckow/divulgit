package org.divulgit.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.divulgit.model.Task;
import org.divulgit.model.User;
import org.divulgit.model.util.TaskUtil;
import org.divulgit.repository.TaskRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.type.TaskState;
import org.divulgit.vo.TaskTreeVO;
import org.divulgit.vo.UserProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class TaskTreeService {

    public static final Map<String, User> EMPTY_USERS = Collections.emptyMap();;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TaskTreeVO> findTasksTree(String remoteId) {
        List<Task> tasks = taskRepository.findByRemoteIdAndRegisteredAtGreaterThanOrderByRegisteredAtDesc(remoteId, LocalDateTime.now().minusHours(24));
        Map<String, Task> tasksById = tasks
                .stream().collect(Collectors.toMap(Task::getId, Function.identity()));
        Map<String, User> taskUsers = extractUsernames(tasks);
        List<TaskTreeVO> tasksTree = new ArrayList<>();
        for ( Task task : tasksById.values() ) {
            if (CollectionUtils.isNotEmpty(task.getSubTasks())) {
                log.trace("Adicionando task {} com filhos", task.getId());
                final TaskTreeVO taskTree = buildTaskTree(task, taskUsers);
                taskTree.getSubTaskDetail().initSubTaskValues();
                task.getSubTasks().forEach(st -> addSubTaskTree(st, taskTree, tasksById) );
                tasksTree.add(taskTree);
            } else if (!isChildTask(task.getId(), tasksById)) {
                log.trace("Adicionando task {} sem filhos", task.getId());
                tasksTree.add(buildTaskTree(task, taskUsers));
            } else {
                log.trace("Task {} será adicionada como filha", task.getId());
            }
        }
        return tasksTree.stream().sorted(Comparator.comparing(TaskTreeVO::getRegisteredAt).reversed()).collect(Collectors.toList());
    }

    private boolean isChildTask(String taskId, Map<String, Task> tasksById) {
        return tasksById.values().stream().anyMatch(t -> t.getSubTasks().contains(taskId));
    }

    private void addSubTaskTree(String subTaskId, TaskTreeVO taskTreeVO, Map<String, Task> tasksById) {
        Task subTask = tasksById.get(subTaskId);
        if (subTask != null) {
            taskTreeVO.getSubTaskDetail().setDescription(subTask.getTitle());
            taskTreeVO.getSubTaskDetail().incrementSubTaskState(subTask.getCurrentStep().getState());
        }
    }

    private TaskTreeVO buildTaskTree(Task task) {
        return buildTaskTree(task, EMPTY_USERS);
    }

    private TaskTreeVO buildTaskTree(Task task, Map<String, User> users) {
        return TaskTreeVO.builder()
                .id(task.getId())
                .registeredAt(task.getRegisteredAt())
                .description(task.getDetail())
                .username(extractUsername(users, task.getUserId()))
                .currentState(task.getCurrentStep().getState().name())
                .currentStateDescription(task.getCurrentStep().getDescription())
                .currentStateMoment(task.getCurrentStep().getDateTime()).build();
    }

    public String extractUsername(Map<String, User> users, String userId) {
        User user = users.get(userId);
        return user != null ? user.getUsername() : Strings.EMPTY;
    }

    public Map<String, User> extractUsernames(List<Task> tasks) {
        List<String> taskUsersId = TaskUtil.extractUsersId(tasks);
        Iterable<User> users = userRepository.findAllById(taskUsersId);
        return StreamSupport.stream(users.spliterator(), false).collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
