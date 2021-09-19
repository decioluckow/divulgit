package org.divulgit.controller;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.security.UserAuthentication;
import org.divulgit.security.UserDetails;
import org.divulgit.service.MergeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        model.addAttribute("project", project);
        model.addAttribute("mergeRequests", mergeRequests);
        model.addAttribute("commentURLs", mountCommentURLs(project, mergeRequests));
        return "mergeRequests";
    }

    private Map<String, String> mountCommentURLs(Project project, List<MergeRequest> mergeRequests) {
        var url = "[projectURL]/merge_requests/[mergeRequestId]#note_[commentId]";
        Map<String, String> commentURLs = new HashMap<>();
        for (MergeRequest mergeRequest : mergeRequests) {
            for (MergeRequest.Comment comment : mergeRequest.getComments() ) {
                var commentURL = url.replace("[projectURL]", project.getUrl());
                commentURL = commentURL.replace("[mergeRequestId]", String.valueOf(mergeRequest.getExternalId()));
                commentURL = commentURL.replace("[commentId]", comment.getExternalId());
                commentURLs.put(mergeRequest.getExternalId() + "-" + comment.getExternalId(), commentURL);
            }
        }
        return commentURLs;
    }
}
