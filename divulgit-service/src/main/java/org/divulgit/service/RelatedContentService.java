package org.divulgit.service;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.User;
import org.divulgit.repository.MergeRequestRepository;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.vo.NotDiscussedByAuthorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatedContentService {

    private MergeRequestRepository mergeRequestRepository;
    private ProjectRepository projectRepository;

    @Autowired
    private RelatedContentService(MergeRequestRepository mergeRequestRepository, ProjectRepository projectRepository) {
        this.mergeRequestRepository = mergeRequestRepository;
        this.projectRepository = projectRepository;
    }

    public Map<String, NotDiscussedByAuthorVO> findUndiscussedByAuthor(User user) {
        List<MergeRequest> mergeRequests = mergeRequestRepository.findUndiscussedRelatedTo(user.getUsername());
        Map<String, NotDiscussedByAuthorVO> projectsCount = new HashMap<>();
        for (MergeRequest mergeRequest : mergeRequests) {
            NotDiscussedByAuthorVO counters = projectsCount.get(mergeRequest.getProjectId());
            if (counters == null) {
                counters = new NotDiscussedByAuthorVO(mergeRequest.getProjectId(), user.getUsername());
                projectsCount.put(mergeRequest.getProjectId(), counters);
            }
            if (mergeRequest.getAuthor().equals(user.getUsername()))
                counters.incrementMergeRequestCount();
            counters.incrementCommentCount(mergeRequest.getValidComments().stream().filter(c -> c.getAuthor().equals(user.getUsername()) && !c.isDiscussed()).count());
        }
        return projectsCount;
    }
}
