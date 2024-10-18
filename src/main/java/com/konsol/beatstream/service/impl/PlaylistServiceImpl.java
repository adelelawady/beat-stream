package com.konsol.beatstream.service.impl;

import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.repository.PlaylistRepository;
import com.konsol.beatstream.service.PlaylistService;
import com.konsol.beatstream.service.dto.PlaylistDTO;
import com.konsol.beatstream.service.mapper.PlaylistMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.Playlist}.
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger LOG = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    private final PlaylistRepository playlistRepository;

    private final PlaylistMapper playlistMapper;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, PlaylistMapper playlistMapper) {
        this.playlistRepository = playlistRepository;
        this.playlistMapper = playlistMapper;
    }

    @Override
    public PlaylistDTO save(PlaylistDTO playlistDTO) {
        LOG.debug("Request to save Playlist : {}", playlistDTO);
        Playlist playlist = playlistMapper.toEntity(playlistDTO);
        playlist = playlistRepository.save(playlist);
        return playlistMapper.toDto(playlist);
    }

    @Override
    public PlaylistDTO update(PlaylistDTO playlistDTO) {
        LOG.debug("Request to update Playlist : {}", playlistDTO);
        Playlist playlist = playlistMapper.toEntity(playlistDTO);
        playlist = playlistRepository.save(playlist);
        return playlistMapper.toDto(playlist);
    }

    @Override
    public Optional<PlaylistDTO> partialUpdate(PlaylistDTO playlistDTO) {
        LOG.debug("Request to partially update Playlist : {}", playlistDTO);

        return playlistRepository
            .findById(playlistDTO.getId())
            .map(existingPlaylist -> {
                playlistMapper.partialUpdate(existingPlaylist, playlistDTO);

                return existingPlaylist;
            })
            .map(playlistRepository::save)
            .map(playlistMapper::toDto);
    }

    @Override
    public Page<PlaylistDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Playlists");
        return playlistRepository.findAll(pageable).map(playlistMapper::toDto);
    }

    public Page<PlaylistDTO> findAllWithEagerRelationships(Pageable pageable) {
        return playlistRepository.findAllWithEagerRelationships(pageable).map(playlistMapper::toDto);
    }

    @Override
    public Optional<PlaylistDTO> findOne(String id) {
        LOG.debug("Request to get Playlist : {}", id);
        return playlistRepository.findOneWithEagerRelationships(id).map(playlistMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Playlist : {}", id);
        playlistRepository.deleteById(id);
    }
}
