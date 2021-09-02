package org.divulgit.repository;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MergeRequestRepository extends MongoRepository<MergeRequest, String> {
}