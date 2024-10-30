package com.konsol.beatstream.repository;

import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the TaskNode entity.
 */
@Repository
public interface TaskNodeRepository extends MongoRepository<TaskNode, String> {
    List<TaskNode> findByStatusInAndOwnerId(List<DownloadStatus> statuses, String ownerId);

    List<TaskNode> findByStatusIn(List<DownloadStatus> statuses);

    List<TaskNode> findByStatusInAndCreatedDateAfter(List<DownloadStatus> statuses, Instant createdDateIsAfter);

    List<TaskNode> findByStatusInAndCreatedDateAfterAndOwnerId(List<DownloadStatus> statuses, Instant createdDateIsAfter, String ownerId);

    List<TaskNode> findByStatusInAndCreatedDateBefore(List<DownloadStatus> statuses, Instant createdDateIsAfter);

    Page<TaskNode> findAllByOrderByCreatedDateDesc(Pageable pageable);
}
