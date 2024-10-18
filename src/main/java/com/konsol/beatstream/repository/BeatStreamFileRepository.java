package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.BeatStreamFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the BeatStreamFile entity.
 */
@Repository
public interface BeatStreamFileRepository extends MongoRepository<BeatStreamFile, String> {}
