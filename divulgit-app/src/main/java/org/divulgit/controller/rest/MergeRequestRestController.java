package org.divulgit.controller.rest;

import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.User;
import org.divulgit.service.MergeRequestCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MergeRequestRestController {

    @Autowired
    private MergeRequestCommentService mergeRequestCommentService;

    @Autowired
    private EntityLoader loader;

    @PostMapping("/in/mergeRequest/{mergeRequestId}/comment/{commentExternalId}/discussed/{discussed}")
    public ResponseEntity<String> markDiscussed(
            Authentication authentication,
            @PathVariable String mergeRequestId,
            @PathVariable String commentExternalId,
            @PathVariable boolean discussed
    ) {
        User user = loader.loadUser(authentication);
        MergeRequest mergeRequest = loader.loadMergeRequest(mergeRequestId);
        loader.verifyProject(user, mergeRequest.getProjectId());
        mergeRequestCommentService.markDiscussed(mergeRequest, commentExternalId, discussed);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/in/mergeRequest/{mergeRequestId}/comment/{commentExternalId}/")
    public ResponseEntity<String> delete(
            Authentication authentication,
            @PathVariable String mergeRequestId,
            @PathVariable String commentExternalId
    ) {
        User user = loader.loadUser(authentication);
        MergeRequest mergeRequest = loader.loadMergeRequest(mergeRequestId);
        loader.verifyProject(user, mergeRequest.getProjectId());
        mergeRequestCommentService.delete(mergeRequest, commentExternalId);
        return ResponseEntity.ok().build();
    }
}
