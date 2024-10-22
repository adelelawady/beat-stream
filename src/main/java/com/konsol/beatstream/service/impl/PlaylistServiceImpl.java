package com.konsol.beatstream.service.impl;

import static com.konsol.beatstream.service.bucket.BucketManager.rootPath;

import com.konsol.beatstream.domain.BeatStreamFile;
import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.repository.PlaylistRepository;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.BeatStreamFileService;
import com.konsol.beatstream.service.PlaylistService;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.UserService;
import com.konsol.beatstream.service.dto.BeatStreamFileDTO;
import com.konsol.beatstream.service.dto.PlaylistDTO;
import com.konsol.beatstream.service.mapper.PlaylistMapper;
import com.konsol.beatstream.service.mapper.TrackMapper;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final UserService userService;

    private final TrackMapper trackMapper;

    private final TrackRepository trackRepository;

    private final BeatStreamFileService beatStreamFileService;

    public PlaylistServiceImpl(
        PlaylistRepository playlistRepository,
        PlaylistMapper playlistMapper,
        UserService userService,
        TrackMapper trackMapper,
        TrackRepository trackRepository,
        BeatStreamFileService beatStreamFileService
    ) {
        this.playlistRepository = playlistRepository;
        this.playlistMapper = playlistMapper;
        this.userService = userService;
        this.trackMapper = trackMapper;
        this.trackRepository = trackRepository;
        this.beatStreamFileService = beatStreamFileService;
    }

    @Override
    public PlaylistDTO save(PlaylistDTO playlistDTO) {
        LOG.debug("Request to save Playlist : {}", playlistDTO);
        Playlist playlist = playlistMapper.toEntity(playlistDTO);
        playlist = playlistRepository.save(playlist);
        return playlistMapper.toDto(playlist);
    }

    @Override
    public Playlist save(Playlist playlist) {
        return playlistRepository.save(playlist);
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
    public Optional<Playlist> findOneDomain(String id) {
        LOG.debug("Request to get Playlist : {}", id);
        return Optional.of(playlistRepository.findOneWithEagerRelationships(id).get());
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Playlist : {}", id);

        Optional<Playlist> playlist = playlistRepository.findById(id);
        if (playlist.isEmpty()) {
            return;
        }

        playlist
            .get()
            .getTracks()
            .forEach(track -> {
                Optional<BeatStreamFile> beatStreamFileDTO = beatStreamFileService.findOneDomain(track.getAudioFileId());

                if (beatStreamFileDTO.isPresent()) {
                    File file = new File(
                        rootPath.resolve(playlist.get().getOwnerId() + "\\" + "audioFiles" + "\\" + track.getAudioFileId()).toUri()
                    );
                    file.delete();
                    beatStreamFileService.delete(track.getAudioFileId());
                }
                trackRepository.deleteById(track.getId());
            });
        playlistRepository.deleteById(id);
    }

    @Override
    public List<com.konsol.beatstream.service.api.dto.Playlist> getMyPlaylists() {
        return playlistRepository
            .findAllByOwnerId(userService.getCurrentUser().getId())
            .stream()
            .map(this::toPlayListDto)
            .collect(Collectors.toList());
    }

    @Override
    public com.konsol.beatstream.service.api.dto.Playlist createPlaylist(com.konsol.beatstream.service.api.dto.Playlist playlistDTO) {
        return toPlayListDto(playlistRepository.save(toPlayListEntity(playlistDTO)));
    }

    @Override
    public com.konsol.beatstream.service.api.dto.Playlist getPlaylist(String playlistId) {
        return toPlayListDto(playlistRepository.findById(playlistId).get());
    }

    Playlist toPlayListEntity(com.konsol.beatstream.service.api.dto.Playlist playlist) {
        Playlist playlistEntity = new Playlist();
        playlistEntity.setId(playlist.getId());
        playlistEntity.setTitle(playlist.getTitle());
        playlistEntity.setDescription(playlist.getDesc());
        playlistEntity.setOwnerId(userService.getCurrentUser().getId());

        return playlistEntity;
    }

    com.konsol.beatstream.service.api.dto.Playlist toPlayListDto(Playlist playlist) {
        com.konsol.beatstream.service.api.dto.Playlist playlistDTO = new com.konsol.beatstream.service.api.dto.Playlist();
        playlistDTO.setId(playlist.getId());
        playlistDTO.setTitle(playlist.getTitle());
        playlistDTO.setDesc(playlist.getDescription());
        playlistDTO.setTracks(playlist.getTracks().stream().map(trackMapper::TrackIntoTrackDTO).collect(Collectors.toList()));
        return playlistDTO;
    }

    @Override
    public void deleteAllPlaylist(String playlistId) {
        playlistRepository.deleteById(playlistId);
    }
}
