package com.konsol.beatstream.service.impl;

import com.konsol.beatstream.domain.ReferanceDownloadTask;
import com.konsol.beatstream.repository.ReferanceDownloadTaskRepository;
import com.konsol.beatstream.service.ReferanceDownloadTaskService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.ReferanceDownloadTask}.
 */
@Service
public class ReferanceDownloadTaskServiceImpl implements ReferanceDownloadTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(ReferanceDownloadTaskServiceImpl.class);

    private final ReferanceDownloadTaskRepository referanceDownloadTaskRepository;

    public ReferanceDownloadTaskServiceImpl(ReferanceDownloadTaskRepository referanceDownloadTaskRepository) {
        this.referanceDownloadTaskRepository = referanceDownloadTaskRepository;
    }

    @Override
    public ReferanceDownloadTask save(ReferanceDownloadTask referanceDownloadTask) {
        LOG.debug("Request to save ReferanceDownloadTask : {}", referanceDownloadTask);
        return referanceDownloadTaskRepository.save(referanceDownloadTask);
    }

    @Override
    public ReferanceDownloadTask update(ReferanceDownloadTask referanceDownloadTask) {
        LOG.debug("Request to update ReferanceDownloadTask : {}", referanceDownloadTask);
        return referanceDownloadTaskRepository.save(referanceDownloadTask);
    }

    @Override
    public Optional<ReferanceDownloadTask> partialUpdate(ReferanceDownloadTask referanceDownloadTask) {
        LOG.debug("Request to partially update ReferanceDownloadTask : {}", referanceDownloadTask);

        return referanceDownloadTaskRepository
            .findById(referanceDownloadTask.getId())
            .map(existingReferanceDownloadTask -> {
                if (referanceDownloadTask.getReferanceId() != null) {
                    existingReferanceDownloadTask.setReferanceId(referanceDownloadTask.getReferanceId());
                }
                if (referanceDownloadTask.getReferanceType() != null) {
                    existingReferanceDownloadTask.setReferanceType(referanceDownloadTask.getReferanceType());
                }
                if (referanceDownloadTask.getReferanceTrackId() != null) {
                    existingReferanceDownloadTask.setReferanceTrackId(referanceDownloadTask.getReferanceTrackId());
                }
                if (referanceDownloadTask.getReferanceStatus() != null) {
                    existingReferanceDownloadTask.setReferanceStatus(referanceDownloadTask.getReferanceStatus());
                }
                if (referanceDownloadTask.getReferanceScheduleDate() != null) {
                    existingReferanceDownloadTask.setReferanceScheduleDate(referanceDownloadTask.getReferanceScheduleDate());
                }
                if (referanceDownloadTask.getReferanceLog() != null) {
                    existingReferanceDownloadTask.setReferanceLog(referanceDownloadTask.getReferanceLog());
                }

                return existingReferanceDownloadTask;
            })
            .map(referanceDownloadTaskRepository::save);
    }

    @Override
    public Page<ReferanceDownloadTask> findAll(Pageable pageable) {
        LOG.debug("Request to get all ReferanceDownloadTasks");
        return referanceDownloadTaskRepository.findAll(pageable);
    }

    @Override
    public Optional<ReferanceDownloadTask> findOne(String id) {
        LOG.debug("Request to get ReferanceDownloadTask : {}", id);
        return referanceDownloadTaskRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ReferanceDownloadTask : {}", id);
        referanceDownloadTaskRepository.deleteById(id);
    }
}
