package org.divulgit.repository;

import org.divulgit.model.Remote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RemoteRepository extends MongoRepository<Remote, String> {

    Optional<Remote> findByUrl(final String url);
}