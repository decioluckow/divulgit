package org.divulgit.controller;

import org.divulgit.repository.ProjectRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.security.UserAuthentication;
import org.divulgit.security.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class ProjectController {

    @Autowired
    private UserRepository userRepos;

    @Autowired
    private ProjectRepository projectRepos;

    @GetMapping("/in/projects")
    public String list(Authentication authentication, Model model) {
        UserDetails userDetails = getUserDetails(authentication);
        Iterable<Project> projects = new ArrayList<>();
        User user = loadUser(userDetails.getUser().getId());
        List<String> projectIds = user.getProjectIds();
        if (!projectIds.isEmpty()) {
            projects = projectRepos.findAllById(projectIds);
        }
        model.addAttribute("projects", projects);
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
        final UserAuthentication userAuthentication = (UserAuthentication) authentication;
        return userAuthentication.getUserDetails();
    }
}