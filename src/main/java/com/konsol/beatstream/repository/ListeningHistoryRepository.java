package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.ListeningHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ListeningHistory entity.
 */
@Repository
public interface ListeningHistoryRepository extends MongoRepository<ListeningHistory, String> {}
