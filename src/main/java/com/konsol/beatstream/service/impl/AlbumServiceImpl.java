package com.konsol.beatstream.service.impl;

import com.konsol.beatstream.domain.Album;
import com.konsol.beatstream.repository.AlbumRepository;
import com.konsol.beatstream.service.AlbumService;
import com.konsol.beatstream.service.dto.AlbumDTO;
import com.konsol.beatstream.service.mapper.AlbumMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.Album}.
 */
@Service
public class AlbumServiceImpl implements AlbumService {

    private static final Logger LOG = LoggerFactory.getLogger(AlbumServiceImpl.class);

    private final AlbumRepository albumRepository;

    private final AlbumMapper albumMapper;

    public AlbumServiceImpl(AlbumRepository albumRepository, AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
    }

    @Override
    public AlbumDTO save(AlbumDTO albumDTO) {
        LOG.debug("Request to save Album : {}", albumDTO);
        Album album = albumMapper.toEntity(albumDTO);
        album = albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    @Override
    public AlbumDTO update(AlbumDTO albumDTO) {
        LOG.debug("Request to update Album : {}", albumDTO);
        Album album = albumMapper.toEntity(albumDTO);
        album = albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    @Override
    public Optional<AlbumDTO> partialUpdate(AlbumDTO albumDTO) {
        LOG.debug("Request to partially update Album : {}", albumDTO);

        return albumRepository
            .findById(albumDTO.getId())
            .map(existingAlbum -> {
                albumMapper.partialUpdate(existingAlbum, albumDTO);

                return existingAlbum;
            })
            .map(albumRepository::save)
            .map(albumMapper::toDto);
    }

    @Override
    public Page<AlbumDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Albums");
        return albumRepository.findAll(pageable).map(albumMapper::toDto);
    }

    public Page<AlbumDTO> findAllWithEagerRelationships(Pageable pageable) {
        return albumRepository.findAllWithEagerRelationships(pageable).map(albumMapper::toDto);
    }

    @Override
    public Optional<AlbumDTO> findOne(String id) {
        LOG.debug("Request to get Album : {}", id);
        return albumRepository.findOneWithEagerRelationships(id).map(albumMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Album : {}", id);
        albumRepository.deleteById(id);
    }
}
