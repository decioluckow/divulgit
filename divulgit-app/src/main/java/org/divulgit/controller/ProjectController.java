package org.divulgit.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.divulgit.model.User.UserProject;
import org.divulgit.model.util.UserProjectUtil;
import org.divulgit.service.ProjectCommentsService;
import org.divulgit.service.ProjectService;
import org.divulgit.vo.UserProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectCommentsService projectCommentsService;

    @Autowired
    private EntityLoader loader;

    @GetMapping("/in/projects")
    public String list(Authentication authentication, Model model) {
        List<UserProjectVO> projects = retrieveProjectsByState(authentication, model, UserProject.State.NEW, UserProject.State.ACTIVE);

        List<UserProjectVO> projectsSorted = projects.stream()
                .sorted(Comparator.comparing(UserProjectVO::getState)
                        .thenComparing(up -> up.getProject().getName())).collect(Collectors.toList());

        model.addAttribute("projects", projectsSorted);
        model.addAttribute("viewMode", "main");
        return "projects";
    }

    @GetMapping("/in/projects/ignored")
    public String listIgnored(Authentication authentication, Model model) {
        List<UserProjectVO> projects = retrieveProjectsByState(authentication, model, UserProject.State.IGNORED);
        model.addAttribute("projects", projects);
        model.addAttribute("viewMode", "ignored");
        return "projects";
    }

    private List<UserProjectVO> retrieveProjectsByState(Authentication authentication, Model model, UserProject.State... states) {
        User user = loader.loadUser(authentication);
        List<UserProject> userProjects = user.getUserProjects();
        List<String> projectIds = UserProjectUtil.findByState(user, states);
        List<Project> projects = projectService.findAllById(projectIds);
        return projectCommentsService.populateCommentsSum(user, projects);
    }
}
