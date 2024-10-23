package com.konsol.beatstream.service.audioPlugins.ReferanceTrack;

import com.konsol.beatstream.domain.ReferanceDownloadTask;
import com.konsol.beatstream.repository.ReferanceDownloadTaskRepository;
import com.konsol.beatstream.service.ReferanceDownloadTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReferanceTrackHandler {

    @Autowired
    ReferanceDownloadTaskRepository referanceDownloadTaskRepository;

    public ReferanceDownloadTask createDownloadTask(String refId, String refType, String trackId) {
        ReferanceDownloadTask referanceDownloadTask = new ReferanceDownloadTask();
        referanceDownloadTask.setReferanceId(refId);
        referanceDownloadTask.setReferanceType(refType);
        referanceDownloadTask.setReferanceStatus("Initialization");
        referanceDownloadTask.setReferanceScheduleDate(null);
        referanceDownloadTask.setReferanceTrackId(trackId);

        return referanceDownloadTaskRepository.save(referanceDownloadTask);
    }
}
