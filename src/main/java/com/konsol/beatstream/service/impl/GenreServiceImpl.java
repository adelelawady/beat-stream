package com.konsol.beatstream.service.impl;

import com.konsol.beatstream.domain.Genre;
import com.konsol.beatstream.repository.GenreRepository;
import com.konsol.beatstream.service.GenreService;
import com.konsol.beatstream.service.dto.GenreDTO;
import com.konsol.beatstream.service.mapper.GenreMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.Genre}.
 */
@Service
public class GenreServiceImpl implements GenreService {

    private static final Logger LOG = LoggerFactory.getLogger(GenreServiceImpl.class);

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    public GenreServiceImpl(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Override
    public GenreDTO save(GenreDTO genreDTO) {
        LOG.debug("Request to save Genre : {}", genreDTO);
        Genre genre = genreMapper.toEntity(genreDTO);
        genre = genreRepository.save(genre);
        return genreMapper.toDto(genre);
    }

    @Override
    public GenreDTO update(GenreDTO genreDTO) {
        LOG.debug("Request to update Genre : {}", genreDTO);
        Genre genre = genreMapper.toEntity(genreDTO);
        genre = genreRepository.save(genre);
        return genreMapper.toDto(genre);
    }

    @Override
    public Optional<GenreDTO> partialUpdate(GenreDTO genreDTO) {
        LOG.debug("Request to partially update Genre : {}", genreDTO);

        return genreRepository
            .findById(genreDTO.getId())
            .map(existingGenre -> {
                genreMapper.partialUpdate(existingGenre, genreDTO);

                return existingGenre;
            })
            .map(genreRepository::save)
            .map(genreMapper::toDto);
    }

    @Override
    public Page<GenreDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Genres");
        return genreRepository.findAll(pageable).map(genreMapper::toDto);
    }

    public Page<GenreDTO> findAllWithEagerRelationships(Pageable pageable) {
        return genreRepository.findAllWithEagerRelationships(pageable).map(genreMapper::toDto);
    }

    @Override
    public Optional<GenreDTO> findOne(String id) {
        LOG.debug("Request to get Genre : {}", id);
        return genreRepository.findOneWithEagerRelationships(id).map(genreMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Genre : {}", id);
        genreRepository.deleteById(id);
    }
}
