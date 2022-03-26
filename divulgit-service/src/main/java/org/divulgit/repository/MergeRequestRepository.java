package org.divulgit.repository;

import org.divulgit.model.MergeRequest;
import org.divulgit.vo.ProjectIdCommentsSum;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MergeRequestRepository extends MongoRepository<MergeRequest, String> {

    List<MergeRequest> findByProjectIdAndAndCommentsTotalGreaterThanOrderByExternalIdDesc(String projectId, long commentsGreaterThan);

    Optional<MergeRequest> findFirstByProjectIdOrderByExternalIdDesc(String projectId);

    @Aggregation({
        "{" +
        "    $match: {" +
        "        projectId: {" +
        "            $in: ?0\n" +
        "        }" +
        "    }" +
        "}",
        "{    $group: {" +
        "        _id: \"$projectId\"," +
        "        commentsTotal: {" +
        "            $sum: \"$commentsTotal\"" +
        "        }," +
        "        commentsDiscussed: {" +
        "            $sum: \"$commentsDiscussed\"" +
        "        }" +
        "    }" +
        "}"}
    )
    AggregationResults<ProjectIdCommentsSum> sumProjectComments(List<String> projectIds);
}