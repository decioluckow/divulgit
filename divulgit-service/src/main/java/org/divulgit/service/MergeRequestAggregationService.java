package org.divulgit.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.divulgit.repository.MergeRequestRepository;
import org.divulgit.vo.ProjectIdCommentsSum;
import org.divulgit.vo.ProjectIdMaxDiscussion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

@Service
public class MergeRequestAggregationService {

    private MergeRequestRepository mergeRequestRepository;

    @Autowired
    private MergeRequestAggregationService(MergeRequestRepository mergeRequestRepository) {
        this.mergeRequestRepository = mergeRequestRepository;
    }

    public Map<String, ProjectIdCommentsSum> sumProjectComments(List<String> projectIds) {
        AggregationResults<ProjectIdCommentsSum> projectIdCommentsSums = mergeRequestRepository.sumProjectComments(projectIds);
        List<ProjectIdCommentsSum> projectCommentsCounts = projectIdCommentsSums.getMappedResults();
        return projectCommentsCounts.stream()
                .collect(Collectors.toMap(ProjectIdCommentsSum::getId, Function.identity()));
    }
    public Map<String, ProjectIdMaxDiscussion> maxCommentDiscussedOn(List<String> projectIds) {
        AggregationResults<ProjectIdMaxDiscussion> projectIdMaxDiscussions = mergeRequestRepository.maxCommentDiscussedOn(projectIds);
        List<ProjectIdMaxDiscussion> projectCommentsCounts = projectIdMaxDiscussions.getMappedResults();
        return projectCommentsCounts.stream()
                .collect(Collectors.toMap(ProjectIdMaxDiscussion::getId, Function.identity()));
    }
}
