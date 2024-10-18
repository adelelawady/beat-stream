package com.konsol.beatstream.service;

import com.konsol.beatstream.service.dto.ListeningHistoryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.konsol.beatstream.domain.ListeningHistory}.
 */
public interface ListeningHistoryService {
    /**
     * Save a listeningHistory.
     *
     * @param listeningHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    ListeningHistoryDTO save(ListeningHistoryDTO listeningHistoryDTO);

    /**
     * Updates a listeningHistory.
     *
     * @param listeningHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    ListeningHistoryDTO update(ListeningHistoryDTO listeningHistoryDTO);

    /**
     * Partially updates a listeningHistory.
     *
     * @param listeningHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ListeningHistoryDTO> partialUpdate(ListeningHistoryDTO listeningHistoryDTO);

    /**
     * Get all the listeningHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ListeningHistoryDTO> findAll(Pageable pageable);

    /**
     * Get the "id" listeningHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ListeningHistoryDTO> findOne(String id);

    /**
     * Delete the "id" listeningHistory.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
