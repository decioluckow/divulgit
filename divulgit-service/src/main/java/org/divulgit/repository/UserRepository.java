package org.divulgit.repository;

import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByExternalUserIdAndRemoteId(final String externalUserId, final String remoteId);
}