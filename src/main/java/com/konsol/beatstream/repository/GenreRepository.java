package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.Genre;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Genre entity.
 */
@Repository
public interface GenreRepository extends MongoRepository<Genre, String> {
    @Query("{}")
    Page<Genre> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Genre> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Genre> findOneWithEagerRelationships(String id);
}
