package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.Track;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Track entity.
 */
@Repository
public interface TrackRepository extends MongoRepository<Track, String> {
    Optional<Track> findByRefIdAndRefType(String id, String refType);
}
