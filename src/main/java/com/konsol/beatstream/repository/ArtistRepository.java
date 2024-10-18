package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.Artist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Artist entity.
 */
@Repository
public interface ArtistRepository extends MongoRepository<Artist, String> {}
