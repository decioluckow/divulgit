package org.divulgit.controller;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.security.UserAuthentication;
import org.divulgit.security.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.divulgit.service.ProjectCommentsService;
import org.divulgit.service.ProjectService;
import org.divulgit.vo.ProjectCommentsSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Slf4j
@Controller
public class ProjectController {

    @Autowired
    private UserRepository userRepos;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectCommentsService projectCommentsService;

    @GetMapping("/in/projects")
    public String list(Authentication authentication, Model model) {
        UserDetails userDetails = getUserDetails(authentication);
        User user = loadUser(userDetails.getUser().getId());
        List<String> projectIds = user.getProjectIds();
        List<ProjectCommentsSum> projectsComments = new ArrayList<>();
        if (!ObjectUtils.isEmpty(projectIds)) {
            List<Project> projects = projectService.findAllById(projectIds);
            //projectsComments = projectCommentsService.populateCommentsSum(projects);
            model.addAttribute("projects", projects);
        }
        return "projects";
    }

    private User loadUser(String userId) {
        Optional<User> user = userRepos.findById(userId);
        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }
        return user.get();
    }

    private UserDetails getUserDetails(Authentication authentication) {
        return ((UserAuthentication) authentication).getUserDetails();
    }
}