package com.konsol.beatstream.service;

import com.konsol.beatstream.domain.BeatStreamFile;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.service.dto.TrackDTO;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Interface for managing {@link com.konsol.beatstream.domain.Track}.
 */
public interface TrackService {
    /**
     * Save a track.
     *
     * @param trackDTO the entity to save.
     * @return the persisted entity.
     */
    TrackDTO save(TrackDTO trackDTO);

    /**
     * Updates a track.
     *
     * @param trackDTO the entity to update.
     * @return the persisted entity.
     */
    TrackDTO update(TrackDTO trackDTO);

    /**
     * Partially updates a track.
     *
     * @param trackDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TrackDTO> partialUpdate(TrackDTO trackDTO);

    /**
     * Get all the tracks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TrackDTO> findAll(Pageable pageable);

    /**
     * Get the "id" track.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TrackDTO> findOne(String id);

    Optional<Track> findOneDomain(String id);
    /**
     * Delete the "id" track.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    boolean validateMp3Track(MultipartFile file);

    Track extractTrackMetadata(MultipartFile file);

    Track extractTrackCover(MultipartFile file);

    Track createTrack(String name, MultipartFile audioFile, MultipartFile cover, String playlistId);

    /**
     * create using download method like download from youtube , soundcloud , spotify
     * @param refId youtube , soundcloud , spotify id
     * @param refType youtube , soundcloud , spotify name
     * @param playlistId selected playlist
     * @return track created
     */
    Track createTrack(String refId, String refType, String playlistId, String ownerId);

    Track connectTrackToAudioFile(String trackId, String filePth);

    com.konsol.beatstream.service.api.dto.Track TrackIntoTrackDTO(com.konsol.beatstream.domain.Track track);
}
