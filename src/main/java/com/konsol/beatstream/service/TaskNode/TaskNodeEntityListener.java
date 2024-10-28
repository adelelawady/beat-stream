package com.konsol.beatstream.service.TaskNode;

import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.web.websocket.ActivityService;
import jakarta.persistence.PostPersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskNodeEntityListener {

    private static final Logger LOG = LoggerFactory.getLogger(TaskNodeEntityListener.class);

    @PostPersist
    public void afterSave(TaskNode myEntity) {
        if (
            myEntity.isCanRetry() &&
            (myEntity.getStatus().equals(DownloadStatus.IN_PROGRESS) || myEntity.getStatus().equals(DownloadStatus.PENDING))
        ) {
            LOG.debug("After saving: " + myEntity);
        }
        // Your custom logic after saving the entity
    }
}
