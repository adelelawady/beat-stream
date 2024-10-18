package com.konsol.beatstream.service.impl;

import com.konsol.beatstream.domain.BeatStreamFile;
import com.konsol.beatstream.repository.BeatStreamFileRepository;
import com.konsol.beatstream.service.BeatStreamFileService;
import com.konsol.beatstream.service.dto.BeatStreamFileDTO;
import com.konsol.beatstream.service.mapper.BeatStreamFileMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.BeatStreamFile}.
 */
@Service
public class BeatStreamFileServiceImpl implements BeatStreamFileService {

    private static final Logger LOG = LoggerFactory.getLogger(BeatStreamFileServiceImpl.class);

    private final BeatStreamFileRepository beatStreamFileRepository;

    private final BeatStreamFileMapper beatStreamFileMapper;

    public BeatStreamFileServiceImpl(BeatStreamFileRepository beatStreamFileRepository, BeatStreamFileMapper beatStreamFileMapper) {
        this.beatStreamFileRepository = beatStreamFileRepository;
        this.beatStreamFileMapper = beatStreamFileMapper;
    }

    @Override
    public BeatStreamFileDTO save(BeatStreamFileDTO beatStreamFileDTO) {
        LOG.debug("Request to save BeatStreamFile : {}", beatStreamFileDTO);
        BeatStreamFile beatStreamFile = beatStreamFileMapper.toEntity(beatStreamFileDTO);
        beatStreamFile = beatStreamFileRepository.save(beatStreamFile);
        return beatStreamFileMapper.toDto(beatStreamFile);
    }

    @Override
    public BeatStreamFileDTO update(BeatStreamFileDTO beatStreamFileDTO) {
        LOG.debug("Request to update BeatStreamFile : {}", beatStreamFileDTO);
        BeatStreamFile beatStreamFile = beatStreamFileMapper.toEntity(beatStreamFileDTO);
        beatStreamFile = beatStreamFileRepository.save(beatStreamFile);
        return beatStreamFileMapper.toDto(beatStreamFile);
    }

    @Override
    public Optional<BeatStreamFileDTO> partialUpdate(BeatStreamFileDTO beatStreamFileDTO) {
        LOG.debug("Request to partially update BeatStreamFile : {}", beatStreamFileDTO);

        return beatStreamFileRepository
            .findById(beatStreamFileDTO.getId())
            .map(existingBeatStreamFile -> {
                beatStreamFileMapper.partialUpdate(existingBeatStreamFile, beatStreamFileDTO);

                return existingBeatStreamFile;
            })
            .map(beatStreamFileRepository::save)
            .map(beatStreamFileMapper::toDto);
    }

    @Override
    public List<BeatStreamFileDTO> findAll() {
        LOG.debug("Request to get all BeatStreamFiles");
        return beatStreamFileRepository
            .findAll()
            .stream()
            .map(beatStreamFileMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<BeatStreamFileDTO> findOne(String id) {
        LOG.debug("Request to get BeatStreamFile : {}", id);
        return beatStreamFileRepository.findById(id).map(beatStreamFileMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete BeatStreamFile : {}", id);
        beatStreamFileRepository.deleteById(id);
    }
}
