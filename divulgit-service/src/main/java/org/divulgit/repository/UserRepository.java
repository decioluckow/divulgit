package org.divulgit.repository;

import java.util.Optional;

import org.divulgit.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByExternalUserIdAndRemoteId(final String externalUserId, final String remoteId);
}