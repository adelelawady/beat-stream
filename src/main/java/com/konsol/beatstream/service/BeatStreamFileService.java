package com.konsol.beatstream.service;

import com.konsol.beatstream.domain.BeatStreamFile;
import com.konsol.beatstream.service.dto.BeatStreamFileDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Interface for managing {@link com.konsol.beatstream.domain.BeatStreamFile}.
 */
public interface BeatStreamFileService {
    /**
     * Save a beatStreamFile.
     *
     * @param beatStreamFileDTO the entity to save.
     * @return the persisted entity.
     */
    BeatStreamFileDTO save(BeatStreamFileDTO beatStreamFileDTO);

    /**
     * Updates a beatStreamFile.
     *
     * @param beatStreamFileDTO the entity to update.
     * @return the persisted entity.
     */
    BeatStreamFileDTO update(BeatStreamFileDTO beatStreamFileDTO);

    /**
     * Partially updates a beatStreamFile.
     *
     * @param beatStreamFileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BeatStreamFileDTO> partialUpdate(BeatStreamFileDTO beatStreamFileDTO);

    /**
     * Get all the beatStreamFiles.
     *
     * @return the list of entities.
     */
    List<BeatStreamFileDTO> findAll();

    /**
     * Get the "id" beatStreamFile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BeatStreamFileDTO> findOne(String id);

    Optional<BeatStreamFile> findOneDomain(String id);

    /**
     * Delete the "id" beatStreamFile.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    BeatStreamFile uploadAudioFile(MultipartFile resource);
}
