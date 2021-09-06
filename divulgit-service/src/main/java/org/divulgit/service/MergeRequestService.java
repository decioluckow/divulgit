package org.divulgit.service;

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

    public int findLastExternalId(Project project) {
        Optional<MergeRequest> mergeRequest = mergeRequestRepository.findFirstByProjectIdOrderByExternalIdDesc(project.getId());
        int lastExternalId = 0;
        if (mergeRequest.isPresent())
            lastExternalId = mergeRequest.get().getExternalId();
        return lastExternalId;
    }
}
