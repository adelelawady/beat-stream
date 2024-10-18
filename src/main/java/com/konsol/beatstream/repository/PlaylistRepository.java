package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.Playlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Playlist entity.
 */
@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, String> {
    @Query("{}")
    Page<Playlist> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Playlist> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Playlist> findOneWithEagerRelationships(String id);
}
