package org.divulgit.service;

import com.google.common.collect.ImmutableList;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.repository.MergeRequestRepository;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class MergeRequestService {

    private MergeRequestRepository mergeRequestRepository;

    @Autowired
    private MergeRequestService(MergeRequestRepository mergeRequestRepository) {
        this.mergeRequestRepository = mergeRequestRepository;
    }

    public Optional<Integer> findLastExternalId(Project project) {
        Optional<MergeRequest> mergeRequest = mergeRequestRepository.findFirstByProjectIdOrderByExternalIdDesc(project.getId());
        Optional<Integer> LastExternalId = Optional.empty();
        if (mergeRequest.isPresent())
            LastExternalId = Optional.of(mergeRequest.get().getExternalId());
        return LastExternalId;
    }

    public List<MergeRequest> findAllByIds(List<String> mergeRequestIds) {
        return ImmutableList.copyOf(mergeRequestRepository.findAllById(mergeRequestIds));
    }

    public Optional<MergeRequest> findById(String mergeRequestId) {
        return mergeRequestRepository.findById(mergeRequestId);
    }

    public List<MergeRequest> findAllWithHashTaggedCommentsByProjectId(Project project) {
        return mergeRequestRepository.findByProjectIdAndAndCommentsTotalGreaterThanOrderByExternalIdDesc(project.getId(), 0);
    }

    public MergeRequest save(MergeRequest mergeRequest) {
        return mergeRequestRepository.save(mergeRequest);
    }

    public List<MergeRequest> saveAll(List<MergeRequest> mergeRequests) {
        return mergeRequestRepository.saveAll(mergeRequests);
    }
}
