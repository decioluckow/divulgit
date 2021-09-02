package org.divulgit.repository;

import java.util.List;

import org.divulgit.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {
  List<String> findExternalProjectIdByRemoteId(final String remoteId);
}