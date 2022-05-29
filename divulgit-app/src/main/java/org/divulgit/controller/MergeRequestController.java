package org.divulgit.controller;

import java.util.List;

import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.divulgit.service.mergeRequest.MergeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MergeRequestController {

    @Autowired
    private MergeRequestService mergeRequestService;

    @Autowired
    private EntityLoader loader;

    @GetMapping("/in/project/{projectId}/mergeRequests")
    public String list(Authentication authentication, @PathVariable String projectId, Model model) {
        User user = loader.loadUser(authentication);
        Project project = loader.loadProject(user, projectId);
        List<MergeRequest> mergeRequests = mergeRequestService.findAllWithHashTaggedCommentsByProjectId(project);
        model.addAttribute("user", user);
        model.addAttribute("project", project);
        model.addAttribute("mergeRequests", mergeRequests);
        return "mergeRequests";
    }
}
