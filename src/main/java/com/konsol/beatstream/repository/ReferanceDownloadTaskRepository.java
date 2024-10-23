package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.ReferanceDownloadTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ReferanceDownloadTask entity.
 */
@Repository
public interface ReferanceDownloadTaskRepository extends MongoRepository<ReferanceDownloadTask, String> {}
