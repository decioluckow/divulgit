package org.divulgit.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.divulgit.vo.ProjectIdCommentsSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class MergeRequestAggregationService {

    private MongoTemplate mongoTemplate;

    @Autowired
    private MergeRequestAggregationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, ProjectIdCommentsSum> sumProjectComments(List<String> projectIds) {
        GroupOperation groupByProjectIdAndSumCommentsTotal = group("projectId")
                .sum("commentsTotal").as("commentsTotal");
        GroupOperation groupByProjectIdAndSumCommentsDiscussed = group("projectId")
                .sum("commentsDiscussed").as("commentsDiscussed");
        MatchOperation filterProjectIds = match(new Criteria("projectId").in(projectIds));
        Aggregation aggregation = newAggregation(
                groupByProjectIdAndSumCommentsTotal,
                groupByProjectIdAndSumCommentsDiscussed,
                filterProjectIds);
        AggregationResults<ProjectIdCommentsSum> result = mongoTemplate.aggregate(
                aggregation, "mergeRequest", ProjectIdCommentsSum.class);
        List<ProjectIdCommentsSum> projectCommentsCounts = result.getMappedResults();
        return projectCommentsCounts.stream()
                .collect(Collectors.toMap(ProjectIdCommentsSum::getProjectId, Function.identity()));
    }
}
