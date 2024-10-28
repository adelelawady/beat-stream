package com.konsol.beatstream.service.impl;

import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.domain.enumeration.DownloadType;
import com.konsol.beatstream.domain.enumeration.ReferenceType;
import com.konsol.beatstream.repository.TaskNodeRepository;
import com.konsol.beatstream.service.PlaylistService;
import com.konsol.beatstream.service.TaskNodeService;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.UserService;
import com.konsol.beatstream.service.audioPlugins.SoundCloud.SoundCloudDownloader;
import com.konsol.beatstream.service.audioPlugins.SoundCloud.SoundCloudPlaylistInfo;
import com.konsol.beatstream.service.audioPlugins.Spotify.SpotifyDownloader;
import com.konsol.beatstream.service.audioPlugins.youtube.YouTubePlaylistInfo;
import com.konsol.beatstream.service.audioPlugins.youtube.YoutubeDownloader;
import com.konsol.beatstream.service.dto.TaskNodeDTO;
import com.konsol.beatstream.service.mapper.TaskNodeMapper;
import com.konsol.beatstream.web.websocket.TaskService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.konsol.beatstream.domain.TaskNode}.
 */
@Service
public class TaskNodeServiceImpl implements TaskNodeService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskNodeServiceImpl.class);

    private final TaskNodeRepository taskNodeRepository;

    private final TaskNodeMapper taskNodeMapper;

    private final TrackService trackService;

    private final PlaylistService playlistService;

    private final YoutubeDownloader youtubeDownloader;

    private final SoundCloudDownloader soundCloudDownloader;

    private final TaskService taskService;
    private final UserService userService;
    private final SpotifyDownloader spotifyDownloader;

    public TaskNodeServiceImpl(
        TaskNodeRepository taskNodeRepository,
        TaskNodeMapper taskNodeMapper,
        TrackService trackService,
        PlaylistService playlistService,
        YoutubeDownloader youtubeDownloader,
        SoundCloudDownloader soundCloudDownloader,
        TaskService taskService,
        UserService userService,
        SpotifyDownloader spotifyDownloader
    ) {
        this.taskNodeRepository = taskNodeRepository;
        this.taskNodeMapper = taskNodeMapper;
        this.trackService = trackService;
        this.playlistService = playlistService;
        this.youtubeDownloader = youtubeDownloader;
        this.soundCloudDownloader = soundCloudDownloader;
        this.taskService = taskService;
        this.userService = userService;
        this.spotifyDownloader = spotifyDownloader;
    }

    @Override
    public TaskNodeDTO save(TaskNodeDTO taskNodeDTO) {
        LOG.debug("Request to save TaskNode : {}", taskNodeDTO);
        TaskNode taskNode = taskNodeMapper.toEntity(taskNodeDTO);
        taskNode = taskNodeRepository.save(taskNode);
        return taskNodeMapper.toDto(taskNode);
    }

    @Override
    public TaskNodeDTO update(TaskNodeDTO taskNodeDTO) {
        LOG.debug("Request to update TaskNode : {}", taskNodeDTO);
        TaskNode taskNode = taskNodeMapper.toEntity(taskNodeDTO);
        taskNode = taskNodeRepository.save(taskNode);
        return taskNodeMapper.toDto(taskNode);
    }

    @Override
    public Optional<TaskNodeDTO> partialUpdate(TaskNodeDTO taskNodeDTO) {
        LOG.debug("Request to partially update TaskNode : {}", taskNodeDTO);

        return taskNodeRepository
            .findById(taskNodeDTO.getId())
            .map(existingTaskNode -> {
                taskNodeMapper.partialUpdate(existingTaskNode, taskNodeDTO);

                return existingTaskNode;
            })
            .map(taskNodeRepository::save)
            .map(taskNodeMapper::toDto);
    }

    @Override
    public Page<TaskNodeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TaskNodes");
        return taskNodeRepository.findAll(pageable).map(taskNodeMapper::toDto);
    }

    @Override
    public Optional<TaskNodeDTO> findOne(String id) {
        LOG.debug("Request to get TaskNode : {}", id);
        return taskNodeRepository.findById(id).map(taskNodeMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete TaskNode : {}", id);
        taskNodeRepository.deleteById(id);
    }

    /**
     * @param taskNode
     * @return
     */
    @Override
    public TaskNode createTaskNode(TaskNode taskNode) {
        return null;
    }

    public void checkTaskState(TaskNode taskNode) {
        if (taskNode.getChildTasks().isEmpty()) {
            taskNode.setStatus(DownloadStatus.COMPLETED);
        } else {
            boolean ChildrenWorking = taskNode
                .getChildTasks()
                .stream()
                .allMatch(
                    child ->
                        child.getStatus() == DownloadStatus.PENDING ||
                        child.getStatus() == DownloadStatus.IN_PROGRESS ||
                        child.getStatus() == DownloadStatus.CONVERSION ||
                        child.getStatus() == DownloadStatus.SCHEDULED
                );
            if (ChildrenWorking) {
                taskNode.setStatus(DownloadStatus.IN_PROGRESS);
            } else {
                taskNode.setStatus(DownloadStatus.COMPLETED);
            }

            try {
                BigDecimal completedCount = BigDecimal.valueOf(
                    taskNode.getChildTasks().stream().filter(child -> child.getStatus() == DownloadStatus.COMPLETED).count()
                );

                int totalChildren = taskNode.getChildTasks().size();
                BigDecimal completedPercentage = completedCount
                    .divide(BigDecimal.valueOf(totalChildren), 4, RoundingMode.HALF_UP) // Calculate the fraction
                    .multiply(BigDecimal.valueOf(100));
                taskNode.setProgressPercentage(completedPercentage);
            } catch (Exception e) {
                taskNode.setProgressPercentage(new BigDecimal(0));
            }
        }
        taskNodeRepository.save(taskNode);
    }

    /**
     * @param taskNode
     */
    @Override
    public synchronized void processTask(TaskNode taskNode) {
        boolean isParentNode = !taskNode.getChildTasks().isEmpty();
        boolean isChildNode = taskNode.getParentTask() != null;
        boolean isStandAloneNode = taskNode.getChildTasks().isEmpty() && taskNode.getParentTask() == null;

        if (isParentNode) {
            // will do nothing
        }

        switch (taskNode.getStatus()) {
            case COMPLETED -> {
                notifyClient(taskNode, "Download completed successfully.");
            }
            case FAILED -> {
                if (taskNode.isCanRetry()) {
                    taskNode.markInProgress();
                    taskNode = taskNodeRepository.save(taskNode);
                    notifyClient(taskNode, "Retrying download. Attempt " + taskNode.getRetryCount());
                } else {
                    notifyClient(taskNode, "Download failed after max retries.");
                    return;
                }
            }
            case PAUSED, PENDING -> {
                taskNode.markInProgress();
                taskNode = taskNodeRepository.save(taskNode);
            }
            case SCHEDULED -> {
                Instant now = Instant.now();
                if (taskNode.getScheduledStartTime().isBefore(now)) {
                    taskNode.markInProgress();
                    taskNode = taskNodeRepository.save(taskNode);
                } else {
                    // task time not started yet
                    return;
                }
            }
            case CONVERSION -> {
                notifyClient(taskNode, "FILE IS IN CONVERSION");
            }
            case IN_PROGRESS -> {
                notifyClient(taskNode, "FILE IS BEING DOWNLOADED");
            }
        }

        try {
            notifyClient(taskNode, taskNode.getType().toString() + " File Download Started");

            if (isChildNode) {
                TaskNode parentTaskNode = taskNode.getParentTask();
                parentTaskNode.setStatus(DownloadStatus.IN_PROGRESS);
                taskNodeRepository.save(parentTaskNode);
            }

            processTaskDownload(taskNode); // Custom method to simulate downloading logic

            if (!isParentNode) {
                taskNode.markCompleted();
            }

            if (isChildNode) {
                TaskNode parentTaskNode = taskNode.getParentTask();
                checkTaskState(parentTaskNode);
            }
            taskService.sendTaskNodes();
            notifyClient(taskNode, " [completed] Download completed successfully.");
        } catch (Exception e) {
            LOG.debug(e.getMessage());
            taskNode.incrementRetryCount();
            taskNode.setCanRetry(taskNode.getRetryCount().compareTo(taskNode.getMaxRetryCount()) < 0);
            if (taskNode.isCanRetry()) {
                taskNode.setScheduledStartTime(Instant.now().plusSeconds(10));
                taskNode.setStatus(DownloadStatus.SCHEDULED);
                taskNodeRepository.save(taskNode);
                taskService.sendTaskNodes();
                notifyClient(taskNode, "Retrying download. Attempt " + taskNode.getRetryCount());
            } else {
                taskNode.markAsFailed();
                notifyClient(taskNode, "Download failed after max retries.");
            }
        } finally {
            taskNodeRepository.save(taskNode);
            taskService.sendTaskNodes();
        }
    }

    @Override
    public void processTaskDownload(TaskNode taskNode) throws Exception {
        taskNode.markInProgress();
        taskNodeRepository.save(taskNode);

        switch (taskNode.getType()) {
            case FILE -> {
                // get file from refId Path and upload it
            }
            case AUDIO -> {
                // get video from yt soundcloud etc
                taskNode.setTaskName("AUDIO FILE DOWNLOAD " + taskNode.getId());
                taskNode = taskNodeRepository.save(taskNode);
                processTaskAudioDownload(taskNode);
            }
            case VIDEO -> {
                // get audio from yt
            }
            case FILE_PLAYLIST -> {}
            case AUDIO_PLAYLIST -> {
                // get video from yt soundcloud etc
                taskNode.setTaskName("[PLAYLIST] " + taskNode.getReferenceId());
                taskNode = taskNodeRepository.save(taskNode);
                processTaskPlayListAudioDownload(taskNode);
            }
            case VIDEO_PLAYLIST -> {
                // create node and list tasks connect and starts them
                // get video list from yt soundcloud etc
            }
        }
    }

    @Override
    public void processTaskAudioDownload(TaskNode taskNode) throws Exception {
        if (!validateReference(taskNode)) {
            throw new Exception("[Validate Reference] Video Id or Url Not Found");
        }

        if (!validatePlaylist(taskNode)) {
            throw new Exception("[Validate Playlist] Playlist not found");
        }

        // Track track=validateTrack(taskNode);
        // if (track==null){
        // Track track1= trackService.createTrack(taskNode.getReferenceId(),taskNode.getReferenceType().toString().toString(),taskNode.getPlaylistId());
        // taskNode.trackId(track1.getId());
        // taskNode= taskNodeRepository.save(taskNode);
        // }

        switch (taskNode.getReferenceType()) {
            case UPLOAD -> {
                //skip just create and validate track
            }
            case SPOTIFY -> {
                // not implemented yet
                spotifyDownloader.AddSpotifySong(taskNode.getReferenceId(), taskNode.getPlaylistId(), taskNode, taskNode.getOwnerId());
            }
            case YOUTUBE -> {
                youtubeDownloader.AddYoutubeVideo(taskNode.getReferenceId(), taskNode.getPlaylistId(), taskNode, taskNode.getOwnerId());
            }
            case SOUNDCLOUD -> {
                soundCloudDownloader.addSoundCloudLink(taskNode.getReferenceId(), taskNode.getPlaylistId(), taskNode);
            }
        }
    }

    @Override
    public void processTaskPlayListAudioDownload(TaskNode taskNode) throws Exception {
        if (!validateReference(taskNode)) {
            throw new Exception("[Validate Reference] Video Id or Url Not Found");
        }

        if (!validatePlaylist(taskNode)) {
            throw new Exception("[Validate Playlist] Playlist not found");
        }

        switch (taskNode.getReferenceType()) {
            case UPLOAD -> {
                //skip just create and validate track
            }
            case SPOTIFY -> {
                // not implemented yet
            }
            case YOUTUBE -> {
                String playlistUrl = taskNode.getReferenceId(); // Replace with your YouTube playlist URL
                YouTubePlaylistInfo youTubePlaylistInfo = new YouTubePlaylistInfo();
                YouTubePlaylistInfo.PlaylistDetails details = youTubePlaylistInfo.getPlaylistDetails(playlistUrl);
                taskNode.setTaskName("[PLAYLIST_YOUTUBE] " + details.getTitle());
                System.out.println(details.toString());
                for (String trackId : details.getTrackUrls()) {
                    TaskNode taskNodeChild = createTask(
                        trackId,
                        ReferenceType.YOUTUBE,
                        trackId,
                        taskNode.getPlaylistId(),
                        null,
                        DownloadType.AUDIO,
                        0,
                        taskNode.getOwnerId()
                    );
                    taskNodeChild.setParentTask(taskNode);
                    taskNodeChild = taskNodeRepository.save(taskNodeChild);
                    taskNode.getChildTasks().add(taskNodeChild);
                    taskNodeRepository.save(taskNode);
                }
            }
            case SOUNDCLOUD -> {
                String playlistUrl = taskNode.getReferenceId(); // Replace with your YouTube playlist URL
                SoundCloudPlaylistInfo SoundCloudPlaylistInfo = new SoundCloudPlaylistInfo();
                SoundCloudPlaylistInfo.PlaylistDetails details = SoundCloudPlaylistInfo.getPlaylistDetails(playlistUrl);
                taskNode.setTaskName("[PLAYLIST_SOUNDCLOUD] " + details.getTitle());
                System.out.println(details.toString());
                for (String trackId : details.getTrackUrls()) {
                    TaskNode taskNodeChild = createTask(
                        trackId,
                        ReferenceType.SOUNDCLOUD,
                        trackId,
                        taskNode.getPlaylistId(),
                        null,
                        DownloadType.AUDIO,
                        0,
                        taskNode.getOwnerId()
                    );
                    taskNodeChild.setParentTask(taskNode);
                    taskNodeChild = taskNodeRepository.save(taskNodeChild);
                    taskNode.getChildTasks().add(taskNodeChild);
                    taskNodeRepository.save(taskNode);
                }
            }
        }
    }

    @Override
    public boolean validateReference(TaskNode taskNode) {
        return (
            taskNode.getReferenceType() != null &&
            !taskNode.getReferenceId().toString().isEmpty() &&
            !taskNode.getReferenceId().toString().isBlank()
        );
    }

    @Override
    public Track validateTrack(TaskNode taskNode) {
        return trackService.findOneDomain(taskNode.getId()).orElseGet(null);
    }

    @Override
    public boolean validatePlaylist(TaskNode taskNode) {
        return playlistService.findOneDomain(taskNode.getPlaylistId()).isPresent();
    }

    //playlistService

    @Override
    public void notifyClient(TaskNode taskNode, String message) {
        LOG.debug(message, taskNode.getId());
        taskService.sendClientMessage(taskNode.getTaskName() + " " + message);
    }

    @Override
    public TaskNode createTask(
        String TaskName,
        ReferenceType referenceType,
        String referenceId,
        String PlayListId,
        Instant scheduledStartTime,
        DownloadType downloadType,
        int MaxRetryCount,
        String OwnerId
    ) {
        TaskNode taskNode = new TaskNode();
        taskNode.setStatus(DownloadStatus.PENDING);
        taskNode.setOwnerId(OwnerId);
        taskNode.setTaskName(TaskName);
        taskNode.setReferenceType(referenceType);
        taskNode.setReferenceId(referenceId);
        taskNode.setPlaylistId(PlayListId);
        if (scheduledStartTime != null) {
            if (scheduledStartTime.isBefore(Instant.now())) {
                taskNode.setScheduledStartTime(scheduledStartTime);
                taskNode.setStatus(DownloadStatus.SCHEDULED);
            } else {
                taskNode.setScheduledStartTime(Instant.now());
                taskNode.setStatus(DownloadStatus.PENDING);
            }
        }
        taskNode.setType(downloadType);
        if (MaxRetryCount > 0) {
            taskNode.setMaxRetryCount(BigDecimal.valueOf(MaxRetryCount));
        } else {
            taskNode.setMaxRetryCount(BigDecimal.valueOf(3));
        }
        return taskNodeRepository.save(taskNode);
    }

    public String extractVideoIdFromUrl(String trackUrl) {
        String videoId = null;

        // Match the video ID from the track URL
        String regex = "((?<=v=)|(?<=/videos/)|(?<=watch\\?v=))[a-zA-Z0-9_-]{11}";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(trackUrl);

        if (matcher.find()) {
            videoId = matcher.group();
        }

        return videoId;
    }
}
