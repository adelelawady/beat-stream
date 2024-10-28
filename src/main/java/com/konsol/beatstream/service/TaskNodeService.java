package com.konsol.beatstream.service;

import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.domain.enumeration.DownloadType;
import com.konsol.beatstream.domain.enumeration.ReferenceType;
import com.konsol.beatstream.service.dto.TaskNodeDTO;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.konsol.beatstream.domain.TaskNode}.
 */
public interface TaskNodeService {
    /**
     * Save a taskNode.
     *
     * @param taskNodeDTO the entity to save.
     * @return the persisted entity.
     */
    TaskNodeDTO save(TaskNodeDTO taskNodeDTO);

    /**
     * Updates a taskNode.
     *
     * @param taskNodeDTO the entity to update.
     * @return the persisted entity.
     */
    TaskNodeDTO update(TaskNodeDTO taskNodeDTO);

    /**
     * Partially updates a taskNode.
     *
     * @param taskNodeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TaskNodeDTO> partialUpdate(TaskNodeDTO taskNodeDTO);

    /**
     * Get all the taskNodes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TaskNodeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" taskNode.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TaskNodeDTO> findOne(String id);

    /**
     * Delete the "id" taskNode.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    TaskNode createTaskNode(TaskNode taskNode);

    void processTask(TaskNode taskNode);

    void processTaskDownload(TaskNode taskNode) throws Exception;

    void processTaskAudioDownload(TaskNode taskNode) throws Exception;

    void processTaskPlayListAudioDownload(TaskNode taskNode) throws Exception;

    boolean validateReference(TaskNode taskNode);

    Track validateTrack(TaskNode taskNode);

    boolean validatePlaylist(TaskNode taskNode);

    void notifyClient(TaskNode taskNode, String message);

    TaskNode createTask(
        String TaskName,
        ReferenceType referenceType,
        String referenceId,
        String PlayListId,
        Instant scheduledStartTime,
        DownloadType downloadType,
        int MaxRetryCount,
        String OwnerId
    );
}
