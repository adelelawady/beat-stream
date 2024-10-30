package com.konsol.beatstream.web.websocket;

import com.konsol.beatstream.config.AppSettingsConfiguration;
import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.repository.TaskNodeRepository;
import com.konsol.beatstream.service.TaskNodeService;
import com.konsol.beatstream.service.UserService;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskService {

    private final SimpMessageSendingOperations messagingTemplate;

    private final TaskNodeRepository taskNodeRepository;
    private final UserService userService;

    boolean enableClientMessage;
    boolean enableTaskSocketLog;

    public TaskService(SimpMessageSendingOperations messagingTemplate, TaskNodeRepository taskNodeRepository, UserService userService) {
        try {
            enableClientMessage = Boolean.parseBoolean(
                AppSettingsConfiguration.getSettings().getProperty("beatstream.settings.socket.task.notify")
            );
            enableTaskSocketLog = Boolean.parseBoolean(
                AppSettingsConfiguration.getSettings().getProperty("beatstream.settings.socket.task.log")
            );
        } catch (Exception e) {
            enableClientMessage = true;
            enableTaskSocketLog = true;
        }

        this.messagingTemplate = messagingTemplate;
        this.taskNodeRepository = taskNodeRepository;
        this.userService = userService;
    }

    @Scheduled(fixedRate = 10000) // Runs every 5 seconds
    public void sendTaskNodesChecker() {
        this.sendTaskNodes();
    }

    public synchronized void sendTaskNodes() {
        if (!enableTaskSocketLog) {
            return;
        }
        List<TaskNode> taskNodeList = taskNodeRepository.findByStatusIn(
            List.of(DownloadStatus.IN_PROGRESS, DownloadStatus.PENDING, DownloadStatus.CONVERSION)
        );

        Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(1));

        List<TaskNode> taskNodeListCompleted = taskNodeRepository.findByStatusInAndCreatedDateAfter(
            List.of(DownloadStatus.COMPLETED),
            fiveMinutesAgo
        );

        taskNodeList.addAll(taskNodeListCompleted);

        messagingTemplate.convertAndSend("/topic/tasks", taskNodeList);
    }

    public void sendClientMessage(String message) {
        if (!enableClientMessage) {
            return;
        }
        messagingTemplate.convertAndSend("/topic/tasks", "[Message]" + message);
    }
}
