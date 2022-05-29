package org.divulgit.controller;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.service.TaskTreeService;
import org.divulgit.vo.TaskTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class TaskController {

    @Autowired
    private TaskTreeService taskTreeService;

    @Autowired
    private EntityLoader loader;

    @GetMapping("/in/tasks")
    public String list(Authentication authentication, Model model) {
        User user = loader.loadUser(authentication);
        Remote remote = loader.loadRemote(user.getRemoteId());
        List<TaskTreeVO> tasksTree = taskTreeService.findTasksTree(remote.getId());
        model.addAttribute("tasks", tasksTree);
        return "tasks";
    }
}
