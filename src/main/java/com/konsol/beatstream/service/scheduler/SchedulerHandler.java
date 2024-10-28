package com.konsol.beatstream.service.scheduler;

import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.domain.enumeration.DownloadType;
import com.konsol.beatstream.repository.TaskNodeRepository;
import com.konsol.beatstream.service.TaskNodeService;
import com.konsol.beatstream.service.UserService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerHandler {

    private final TaskNodeRepository downloadTaskRepository;
    private final TaskNodeService downloadTaskService;
    private final TaskNodeRepository taskNodeRepository;
    private final UserService userService;

    @Autowired
    public SchedulerHandler(
        TaskNodeRepository downloadTaskRepository,
        TaskNodeService downloadTaskService,
        TaskNodeRepository taskNodeRepository,
        UserService userService
    ) {
        this.downloadTaskRepository = downloadTaskRepository;
        this.downloadTaskService = downloadTaskService;
        this.taskNodeRepository = taskNodeRepository;
        this.userService = userService;
    }

    @Scheduled(fixedRate = 4000) // Runs every 1 seconds
    public void checkAndProcessPendingTasks() {
        List<TaskNode> tasks = downloadTaskRepository.findByStatusIn(List.of(DownloadStatus.PENDING, DownloadStatus.SCHEDULED));
        for (TaskNode task : tasks) {
            if (task.getType().equals(DownloadType.AUDIO_PLAYLIST)) { // is parent
                task.markInProgress();
                task = taskNodeRepository.save(task);
                try {
                    downloadTaskService.processTaskPlayListAudioDownload(task);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            } else {
                if (task.isCanRetry()) {
                    if (task.getStatus() == DownloadStatus.SCHEDULED && task.getScheduledStartTime().isAfter(Instant.now())) {
                        continue;
                    } else {
                        downloadTaskService.processTask(task);
                        break;
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 30000) // A IN_PROGRESS task took to long 15 minute kill it
    public void checkAndKillProcessInProgressLongTasks() {
        List<TaskNode> tasks = downloadTaskRepository.findByStatusIn(List.of(DownloadStatus.IN_PROGRESS));
        for (TaskNode task : tasks) {
            if (hasTaskExceededTime(task) && !task.getType().equals(DownloadType.AUDIO_PLAYLIST)) {
                if (task.isCanRetry()) {
                    task.setStatus(DownloadStatus.PENDING);
                    task.setRetryCount(task.getRetryCount().add(new BigDecimal(1)));
                    task.setCanRetry(true);
                } else {
                    task.setStatus(DownloadStatus.FAILED);
                }
                downloadTaskRepository.save(task);
            }
        }
    }

    public boolean hasTaskExceededTime(TaskNode task) {
        Instant scheduledStartTime = task.getCreatedDate();
        Instant fifteenMinutesAgo = Instant.now().minus(Duration.ofMinutes(20));
        return scheduledStartTime.isBefore(fifteenMinutesAgo);
    }
}
