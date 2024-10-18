package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.Album;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Album entity.
 */
@Repository
public interface AlbumRepository extends MongoRepository<Album, String> {
    @Query("{}")
    Page<Album> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Album> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Album> findOneWithEagerRelationships(String id);
}
