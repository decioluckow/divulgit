package org.divulgit.controller.rest;

import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.security.UserAuthentication;
import org.divulgit.service.mergeRequest.MergeRequestCommentService;
import org.divulgit.task.executor.ScanExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@RestController
public class MergeRequestRestController {

    @Autowired
    private MergeRequestCommentService mergeRequestCommentService;

    @Autowired
    private ScanExecutor taskExecutor;

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

    @PostMapping("/in/project/{projectId}/mergeRequest/{mergeRequestId}/rescan")
    public ResponseEntity<String> rescan(
            Authentication authentication,
            @PathVariable String projectId,
            @PathVariable String mergeRequestId
    ) {
        log.info("Start rescan for mergeRequest {}", mergeRequestId);
        User user = loader.loadUser(authentication);
        Remote remote = loader.loadRemote(user.getRemoteId());
        String remoteToken = ((UserAuthentication) authentication).getRemoteToken();
        Project project = loader.loadProject(user, projectId);
        taskExecutor.scanProjectForMergeRequests(remote, user, project, Arrays.asList(mergeRequestId), remoteToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/in/mergeRequest/{mergeRequestId}/comment/{commentExternalId}/delete/")
    public ResponseEntity<String> deleteComment(
            Authentication authentication,
            @PathVariable String mergeRequestId,
            @PathVariable String commentExternalId
    ) {
        updateCommentState(authentication, mergeRequestId, commentExternalId, MergeRequest.Comment.State.DELETED);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/in/mergeRequest/{mergeRequestId}/comment/{commentExternalId}/hide/")
    public ResponseEntity<String> hideComment(
            Authentication authentication,
            @PathVariable String mergeRequestId,
            @PathVariable String commentExternalId
    ) {
        updateCommentState(authentication, mergeRequestId, commentExternalId, MergeRequest.Comment.State.HIDDEN);
        return ResponseEntity.ok().build();
    }

    public void updateCommentState(
            Authentication authentication,
            String mergeRequestId,
            String commentExternalId,
            MergeRequest.Comment.State state
    ) {
        User user = loader.loadUser(authentication);
        MergeRequest mergeRequest = loader.loadMergeRequest(mergeRequestId);
        loader.verifyProject(user, mergeRequest.getProjectId());
        mergeRequestCommentService.updateState(mergeRequest, commentExternalId, state);
    }
}
