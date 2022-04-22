package org.divulgit.repository;

import java.util.Optional;

import org.divulgit.model.Remote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemoteRepository extends MongoRepository<Remote, String> {

    Optional<Remote> findByUrl(final String url);
}