package org.divulgit.service.mergeRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.util.MergeRequestUtil;
import org.divulgit.repository.MergeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MergeRequestCommentService {

    private MergeRequestRepository mergeRequestRepository;

    @Autowired
    private MergeRequestCommentService(MergeRequestRepository mergeRequestRepository) {
        this.mergeRequestRepository = mergeRequestRepository;
    }

    public void markDiscussed(MergeRequest mergeRequest, String commentExternalId, boolean discussed) {
        List<MergeRequest.Comment> comments = mergeRequest.getComments();
        MergeRequest.Comment comment = MergeRequestUtil.getComment(comments, commentExternalId);
        comment.setDiscussedOn(discussed ? LocalDateTime.now() : null);
        mergeRequestRepository.save(mergeRequest);
    }

    public void updateState(MergeRequest mergeRequest, String commentExternalId, MergeRequest.Comment.State state) {
        List<MergeRequest.Comment> comments = mergeRequest.getComments();
        MergeRequest.Comment comment = MergeRequestUtil.getComment(comments, commentExternalId);
        comment.setState(state);
        mergeRequestRepository.save(mergeRequest);
    }
}
