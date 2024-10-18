package com.konsol.beatstream.service.impl;

import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.dto.TrackDTO;
import com.konsol.beatstream.service.mapper.TrackMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.Track}.
 */
@Service
public class TrackServiceImpl implements TrackService {

    private static final Logger LOG = LoggerFactory.getLogger(TrackServiceImpl.class);

    private final TrackRepository trackRepository;

    private final TrackMapper trackMapper;

    public TrackServiceImpl(TrackRepository trackRepository, TrackMapper trackMapper) {
        this.trackRepository = trackRepository;
        this.trackMapper = trackMapper;
    }

    @Override
    public TrackDTO save(TrackDTO trackDTO) {
        LOG.debug("Request to save Track : {}", trackDTO);
        Track track = trackMapper.toEntity(trackDTO);
        track = trackRepository.save(track);
        return trackMapper.toDto(track);
    }

    @Override
    public TrackDTO update(TrackDTO trackDTO) {
        LOG.debug("Request to update Track : {}", trackDTO);
        Track track = trackMapper.toEntity(trackDTO);
        track = trackRepository.save(track);
        return trackMapper.toDto(track);
    }

    @Override
    public Optional<TrackDTO> partialUpdate(TrackDTO trackDTO) {
        LOG.debug("Request to partially update Track : {}", trackDTO);

        return trackRepository
            .findById(trackDTO.getId())
            .map(existingTrack -> {
                trackMapper.partialUpdate(existingTrack, trackDTO);

                return existingTrack;
            })
            .map(trackRepository::save)
            .map(trackMapper::toDto);
    }

    @Override
    public Page<TrackDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Tracks");
        return trackRepository.findAll(pageable).map(trackMapper::toDto);
    }

    @Override
    public Optional<TrackDTO> findOne(String id) {
        LOG.debug("Request to get Track : {}", id);
        return trackRepository.findById(id).map(trackMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Track : {}", id);
        trackRepository.deleteById(id);
    }
}
