package org.divulgit.service;

import java.time.LocalDateTime;
import java.util.List;

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
        comment.setDiscussed(discussed);
        comment.setDiscussedOn(LocalDateTime.now());
        mergeRequestRepository.save(mergeRequest);
    }

    public void delete(MergeRequest mergeRequest, String commentExternalId) {
        List<MergeRequest.Comment> comments = mergeRequest.getComments();
        MergeRequest.Comment comment = MergeRequestUtil.getComment(comments, commentExternalId);
        comments.remove(comment);
        mergeRequestRepository.save(mergeRequest);
    }


}
