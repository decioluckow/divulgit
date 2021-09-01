package br.com.decioluckow.divulgit.repository;

import java.util.List;

import br.com.decioluckow.divulgit.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {
  List<String> findExternalProjectIdByRepositoryId(final String repositoryId);
}