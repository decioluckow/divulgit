package org.divulgit.repository;

import java.util.List;

import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.type.ProjectState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends MongoRepository<Project, String> {

  List<Project> findByRemoteId(final String remoteId);

  @Query(value="{ 'remoteId' : ?0 }", fields="{_id : 1}")
  List<Project> findIdByRemoteId(String remoteId);

  @Query(value="{ 'remoteId' : ?0 }", fields="{externalId : 1, _id : 0}")
  List<Project> findExternalIdByRemoteId(String remoteId);

  List<Project> findByIdInAndStateInOrderByName(@Param("id") List<String> ids, @Param("state") List<ProjectState> states);
}