package org.divulgit.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.divulgit.repository.MergeRequestRepository;
import org.divulgit.vo.ProjectIdCommentsSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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
}
