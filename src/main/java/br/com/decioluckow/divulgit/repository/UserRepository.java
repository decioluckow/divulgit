package br.com.decioluckow.divulgit.repository;

import br.com.decioluckow.divulgit.model.Project;
import br.com.decioluckow.divulgit.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByExternalUserIdAndOriginId(final String externalUserId, final String originId);
}