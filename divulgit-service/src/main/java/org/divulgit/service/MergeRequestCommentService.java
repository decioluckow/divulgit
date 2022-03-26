package org.divulgit.service;

import org.divulgit.model.MergeRequest;
import org.divulgit.repository.MergeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MergeRequestCommentService {

    private MergeRequestRepository mergeRequestRepository;

    @Autowired
    private MergeRequestCommentService(MergeRequestRepository mergeRequestRepository) {
        this.mergeRequestRepository = mergeRequestRepository;
    }

    public void markDiscussed(MergeRequest mergeRequest, String commentExternalId, boolean discussed) {
        List<MergeRequest.Comment> comments = mergeRequest.getComments();
        Optional<MergeRequest.Comment> comment = findComment(commentExternalId, comments);
        if (comment.isPresent()) {
            comment.get().setDiscussed(discussed);
            mergeRequestRepository.save(mergeRequest);
        } else {
            throw new RuntimeException("Comment not found to mark discussed");
        }
    }

    public void delete(MergeRequest mergeRequest, String commentExternalId) {
        List<MergeRequest.Comment> comments = mergeRequest.getComments();
        Optional<MergeRequest.Comment> comment = findComment(commentExternalId, comments);
        if (comment.isPresent()) {
            comments.remove(comment.get());
            mergeRequestRepository.save(mergeRequest);
        } else {
            throw new RuntimeException("Comment not found do delete");
        }
    }

    private Optional<MergeRequest.Comment> findComment(String commentExternalId, List<MergeRequest.Comment> comments) {
        return comments.stream()
                .filter(c -> commentExternalId.equals(c.getExternalId())).findFirst();
    }
}
