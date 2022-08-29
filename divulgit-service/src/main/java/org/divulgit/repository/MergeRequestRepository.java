package org.divulgit.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.divulgit.model.MergeRequest;
import org.divulgit.vo.ProjectIdCommentsSum;
import org.divulgit.vo.ProjectIdMaxDiscussion;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MergeRequestRepository extends MongoRepository<MergeRequest, String> {

    List<MergeRequest> findByProjectIdAndStateAndCreatedAtGreaterThanOrderByExternalIdDesc(String projectId, MergeRequest.State state, LocalDateTime creationDate);

    List<MergeRequest> findByProjectIdAndAndCommentsTotalGreaterThanOrderByExternalIdDesc(String projectId, long commentsGreaterThan);

    @Query(value = "{ " +
            "commentsTotal: {$gt: 0}, " +
            "$or: [ {author: ?0}, { comments: {$elemMatch: {author:?0 } } } ], " +
            "comments: {$elemMatch: {$or:[{discussedOn:{$eq:null}},{discussedOn:{$exists:false}}]}}}",
            sort="{projectId:1, externalId: -1}")
    List<MergeRequest> findUndiscussedRelatedTo(String login);

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

    @Aggregation({
        "{ $match: { projectId: { $in: ?0 } } }",
        "{ $unwind:{ path: '$comments' } }",
        "{ $group: { _id: '$projectId', maxDiscussedOn: { $max: '$comments.discussedOn' } } }"}
    )
    AggregationResults<ProjectIdMaxDiscussion> maxCommentDiscussedOn(List<String> projectIds);
}