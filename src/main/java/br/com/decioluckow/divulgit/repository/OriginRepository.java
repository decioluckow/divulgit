package br.com.decioluckow.divulgit.repository;

import br.com.decioluckow.divulgit.model.Origin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OriginRepository extends MongoRepository<Origin, String> {

    Optional<Origin> findByUrl(final String url);
}