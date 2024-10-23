package com.konsol.beatstream.service;

import com.konsol.beatstream.domain.ReferanceDownloadTask;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.konsol.beatstream.domain.ReferanceDownloadTask}.
 */
public interface ReferanceDownloadTaskService {
    /**
     * Save a referanceDownloadTask.
     *
     * @param referanceDownloadTask the entity to save.
     * @return the persisted entity.
     */
    ReferanceDownloadTask save(ReferanceDownloadTask referanceDownloadTask);

    /**
     * Updates a referanceDownloadTask.
     *
     * @param referanceDownloadTask the entity to update.
     * @return the persisted entity.
     */
    ReferanceDownloadTask update(ReferanceDownloadTask referanceDownloadTask);

    /**
     * Partially updates a referanceDownloadTask.
     *
     * @param referanceDownloadTask the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReferanceDownloadTask> partialUpdate(ReferanceDownloadTask referanceDownloadTask);

    /**
     * Get all the referanceDownloadTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReferanceDownloadTask> findAll(Pageable pageable);

    /**
     * Get the "id" referanceDownloadTask.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReferanceDownloadTask> findOne(String id);

    /**
     * Delete the "id" referanceDownloadTask.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
