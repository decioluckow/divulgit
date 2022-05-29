package org.divulgit.service;

import org.divulgit.model.Task;
import org.divulgit.repository.TaskRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.type.TaskState;
import org.divulgit.vo.TaskTreeVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskTreeServiceTest {

    public static final String REMOTE_ID = "someRemote";
    public static final String USER_A = "userA";
    public static final String TASK_A = "a";
    public static final String TASK_A1 = "a1";
    public static final String TASK_A2 = "a2";
    public static final String TASK_B = "b";
    public static final String TASK_C = "c";
    public static final String TASK_C1 = "c1";
    public static final String TASK_C2 = "c2";

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskTreeService taskTreeService;

    @Test
    void findLastTasksTree() {
        List<Task> tasks = buildTasks();
        Mockito.when(taskRepository.findByRemoteIdAndRegisteredAtGreaterThanOrderByRegisteredAtDesc(Mockito.anyString(), Mockito.any(LocalDateTime.class))).thenReturn(tasks);

        List<TaskTreeVO> tasksTree = taskTreeService.findTasksTree(REMOTE_ID);

        assertEquals(3, tasksTree.size());
        Map<String, AtomicInteger> subTasksA = tasksTree.stream().filter(t -> t.getId().equals(TASK_A)).findFirst().get().getSubTaskDetail().getSubTasks();
        assertEquals(4, subTasksA.size());
        assertEquals(1, subTasksA.get(TaskState.FINISHED.name()).get());
        assertEquals(1, subTasksA.get(TaskState.RUNNING.name()).get());
        assertEquals(0, subTasksA.get(TaskState.WAITING.name()).get());
        assertEquals(0, subTasksA.get(TaskState.ERROR.name()).get());
        assertEquals(0, tasksTree.stream().filter(t -> t.getId().equals(TASK_B)).findFirst().get().getSubTaskDetail().getSubTasks().size());
        assertEquals(4, tasksTree.stream().filter(t -> t.getId().equals(TASK_C)).findFirst().get().getSubTaskDetail().getSubTasks().size());
    }

    private List<Task> buildTasks() {
        List<Task> tasks = new ArrayList<>();
        Task taskA = buildTask(TASK_A, "scanning merge resquests project XPTO1","", USER_A, TaskState.FINISHED, "");
        Task taskA1 = buildTask(TASK_A1, "scanning merge resquests project XPTO1","", USER_A, TaskState.FINISHED, "");
        Task taskA2 = buildTask(TASK_A2, "scanning merge resquests project XPTO1","", USER_A, TaskState.RUNNING, "");
        taskA.addSubTasks(taskA1.getId());
        taskA.addSubTasks(taskA2.getId());
        Task taskB = buildTask(TASK_B, "scanning merge resquests project XPTO1","", USER_A, TaskState.RUNNING, "");
        Task taskC = buildTask(TASK_C, "scanning merge resquests project XPTO1","", USER_A, TaskState.RUNNING, "");
        Task taskC1 = buildTask(TASK_C1, "scanning merge resquests project XPTO1","", USER_A, TaskState.RUNNING, "");
        Task taskC2 = buildTask(TASK_C2, "scanning merge resquests project XPTO1","", USER_A, TaskState.RUNNING, "");
        taskC.addSubTasks(taskC1.getId());
        taskC.addSubTasks(taskC2.getId());
        tasks.add(taskA);
        tasks.add(taskA1);
        tasks.add(taskA2);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskC1);
        tasks.add(taskC2);
        return tasks;
    }

    private Task buildTask(String id, String title, String detail, String userId, TaskState state, String stepDescription) {
        Task task = new Task(title, detail, REMOTE_ID, userId);
        task.setId(id);
        task.setCurrentStep(Task.TaskStep.builder().description("").state(state).dateTime(LocalDateTime.now()).build());
        return task;
    }
}