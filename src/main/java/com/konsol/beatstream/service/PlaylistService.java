package com.konsol.beatstream.service;

import com.konsol.beatstream.service.api.dto.Playlist;
import com.konsol.beatstream.service.dto.PlaylistDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.konsol.beatstream.domain.Playlist}.
 */
public interface PlaylistService {
    /**
     * Save a playlist.
     *
     * @param playlistDTO the entity to save.
     * @return the persisted entity.
     */
    PlaylistDTO save(PlaylistDTO playlistDTO);

    com.konsol.beatstream.domain.Playlist save(com.konsol.beatstream.domain.Playlist playlist);
    /**
     * Updates a playlist.
     *
     * @param playlistDTO the entity to update.
     * @return the persisted entity.
     */
    PlaylistDTO update(PlaylistDTO playlistDTO);

    /**
     * Partially updates a playlist.
     *
     * @param playlistDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PlaylistDTO> partialUpdate(PlaylistDTO playlistDTO);

    /**
     * Get all the playlists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PlaylistDTO> findAll(Pageable pageable);

    /**
     * Get all the playlists with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PlaylistDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" playlist.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<com.konsol.beatstream.domain.Playlist> findOneDomain(String id);

    /**
     * Delete the "id" playlist.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    List<Playlist> getMyPlaylists();

    Playlist createPlaylist(Playlist playlistDTO);

    Playlist getPlaylist(String playlistId);

    void deleteAllPlaylist(String playlistId);
}
