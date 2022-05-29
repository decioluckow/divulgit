package org.divulgit.repository;

import org.divulgit.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByRemoteIdAndRegisteredAtGreaterThanOrderByRegisteredAtDesc(String remoteId, LocalDateTime registeredAt);
}
