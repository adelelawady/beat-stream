package com.konsol.beatstream.service;

import com.konsol.beatstream.service.dto.GenreDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.konsol.beatstream.domain.Genre}.
 */
public interface GenreService {
    /**
     * Save a genre.
     *
     * @param genreDTO the entity to save.
     * @return the persisted entity.
     */
    GenreDTO save(GenreDTO genreDTO);

    /**
     * Updates a genre.
     *
     * @param genreDTO the entity to update.
     * @return the persisted entity.
     */
    GenreDTO update(GenreDTO genreDTO);

    /**
     * Partially updates a genre.
     *
     * @param genreDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<GenreDTO> partialUpdate(GenreDTO genreDTO);

    /**
     * Get all the genres.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GenreDTO> findAll(Pageable pageable);

    /**
     * Get all the genres with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GenreDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" genre.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GenreDTO> findOne(String id);

    /**
     * Delete the "id" genre.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
