package com.konsol.beatstream.service.impl;

import com.konsol.beatstream.domain.ListeningHistory;
import com.konsol.beatstream.repository.ListeningHistoryRepository;
import com.konsol.beatstream.service.ListeningHistoryService;
import com.konsol.beatstream.service.dto.ListeningHistoryDTO;
import com.konsol.beatstream.service.mapper.ListeningHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.ListeningHistory}.
 */
@Service
public class ListeningHistoryServiceImpl implements ListeningHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ListeningHistoryServiceImpl.class);

    private final ListeningHistoryRepository listeningHistoryRepository;

    private final ListeningHistoryMapper listeningHistoryMapper;

    public ListeningHistoryServiceImpl(
        ListeningHistoryRepository listeningHistoryRepository,
        ListeningHistoryMapper listeningHistoryMapper
    ) {
        this.listeningHistoryRepository = listeningHistoryRepository;
        this.listeningHistoryMapper = listeningHistoryMapper;
    }

    @Override
    public ListeningHistoryDTO save(ListeningHistoryDTO listeningHistoryDTO) {
        LOG.debug("Request to save ListeningHistory : {}", listeningHistoryDTO);
        ListeningHistory listeningHistory = listeningHistoryMapper.toEntity(listeningHistoryDTO);
        listeningHistory = listeningHistoryRepository.save(listeningHistory);
        return listeningHistoryMapper.toDto(listeningHistory);
    }

    @Override
    public ListeningHistoryDTO update(ListeningHistoryDTO listeningHistoryDTO) {
        LOG.debug("Request to update ListeningHistory : {}", listeningHistoryDTO);
        ListeningHistory listeningHistory = listeningHistoryMapper.toEntity(listeningHistoryDTO);
        listeningHistory = listeningHistoryRepository.save(listeningHistory);
        return listeningHistoryMapper.toDto(listeningHistory);
    }

    @Override
    public Optional<ListeningHistoryDTO> partialUpdate(ListeningHistoryDTO listeningHistoryDTO) {
        LOG.debug("Request to partially update ListeningHistory : {}", listeningHistoryDTO);

        return listeningHistoryRepository
            .findById(listeningHistoryDTO.getId())
            .map(existingListeningHistory -> {
                listeningHistoryMapper.partialUpdate(existingListeningHistory, listeningHistoryDTO);

                return existingListeningHistory;
            })
            .map(listeningHistoryRepository::save)
            .map(listeningHistoryMapper::toDto);
    }

    @Override
    public Page<ListeningHistoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ListeningHistories");
        return listeningHistoryRepository.findAll(pageable).map(listeningHistoryMapper::toDto);
    }

    @Override
    public Optional<ListeningHistoryDTO> findOne(String id) {
        LOG.debug("Request to get ListeningHistory : {}", id);
        return listeningHistoryRepository.findById(id).map(listeningHistoryMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ListeningHistory : {}", id);
        listeningHistoryRepository.deleteById(id);
    }
}
