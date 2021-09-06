package org.divulgit.repository;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MergeRequestRepository extends MongoRepository<MergeRequest, String> {

    Optional<MergeRequest> findFirstByProjectIdOrderByExternalIdDesc(String projectId);
}