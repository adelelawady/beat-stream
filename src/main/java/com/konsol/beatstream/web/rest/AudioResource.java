package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.service.audioPlugins.Spotify.SpotifyDownloader.getSpotifyLinkType;
import static com.konsol.beatstream.service.audioPlugins.Spotify.SpotifyDownloader.isSpotifyLink;

import com.konsol.beatstream.config.AppSettingsConfiguration;
import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.User;
import com.konsol.beatstream.domain.enumeration.DownloadType;
import com.konsol.beatstream.domain.enumeration.ReferenceType;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.UserService;
import com.konsol.beatstream.service.api.dto.Status;
import com.konsol.beatstream.service.api.dto.Track;
import com.konsol.beatstream.service.audioPlugins.SoundCloud.SoundCloudDownloader;
import com.konsol.beatstream.service.audioPlugins.youtube.YoutubeDownloader;
import com.konsol.beatstream.service.impl.TaskNodeServiceImpl;
import com.konsol.beatstream.web.api.AudioApi;
import com.konsol.beatstream.web.api.AudioApiDelegate;
import com.konsol.beatstream.web.websocket.TaskService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AudioResource implements AudioApiDelegate {

    @Autowired
    TrackService trackService;

    @Autowired
    private TaskNodeServiceImpl taskNodeService;

    @Autowired
    private UserService userService;

    boolean canDownloadPlaylists = true;

    @Autowired
    private TaskService taskService;

    public AudioResource() {
        try {
            canDownloadPlaylists = Boolean.parseBoolean(
                AppSettingsConfiguration.getSettings().getProperty("beatstream.settings.download.playlist.enabled", "true")
            );
        } catch (Exception e) {
            canDownloadPlaylists = true;
        }
    }

    @Override
    public ResponseEntity<com.konsol.beatstream.service.api.dto.Track> uploadSongFile(
        String title,
        MultipartFile audioFile,
        MultipartFile cover,
        String playlistId
    ) {
        /**
         * TODO apply playlist selected
         */

        com.konsol.beatstream.domain.Track track = trackService.createTrack(title, audioFile, cover, playlistId);

        return ResponseEntity.ok(trackService.TrackIntoTrackDTO(track));
    }

    @Override
    public ResponseEntity<Status> downloadAudio(String refid, String refType, String playlistId) {
        Status status = new Status();
        status.setStatus("Track was added to tasks and download will start soon Enjoy");

        User user = userService.getCurrentUser();
        if (!refType.isBlank() && refType.equalsIgnoreCase("youtube")) {
            try {
                TaskNode taskNode = taskNodeService.createTask(
                    refid,
                    ReferenceType.YOUTUBE,
                    refid,
                    playlistId,
                    null,
                    DownloadType.AUDIO,
                    0,
                    user.getId()
                );
                taskService.sendClientMessage("Track was added to tasks and download will start soon  Enjoy");
                return ResponseEntity.ok(status);
            } catch (Exception e) {
                status.setStatus(e.getMessage());
                return ResponseEntity.ok(status);
            }
        }

        if (!refType.isBlank() && refType.equalsIgnoreCase("soundcloudList")) {
            if (!canDownloadPlaylists) {
                status.setStatus("Playlist Download is disabled change it from settings in settings file");
                taskService.sendClientMessage("Playlist Download is disabled change it from settings in settings file");
                return ResponseEntity.ok(status);
            }
            try {
                TaskNode taskNode = taskNodeService.createTask(
                    refid,
                    ReferenceType.SOUNDCLOUD,
                    refid,
                    playlistId,
                    null,
                    DownloadType.AUDIO_PLAYLIST,
                    0,
                    user.getId()
                );
                taskService.sendClientMessage("Track was added to tasks and download will start soon  Enjoy");
                return ResponseEntity.ok(status);
            } catch (Exception e) {
                status.setStatus(e.getMessage());
                return ResponseEntity.ok(status);
            }
        }

        if (!refType.isBlank() && refType.equalsIgnoreCase("youtubelist")) {
            if (!canDownloadPlaylists) {
                status.setStatus("Playlist Download is disabled change it from settings in settings file");
                taskService.sendClientMessage("Playlist Download is disabled change it from settings in settings file");
                return ResponseEntity.ok(status);
            }
            try {
                TaskNode taskNode = taskNodeService.createTask(
                    refid,
                    ReferenceType.YOUTUBE,
                    refid,
                    playlistId,
                    null,
                    DownloadType.AUDIO_PLAYLIST,
                    0,
                    user.getId()
                );
                taskService.sendClientMessage("Track was added to tasks and download will start soon  Enjoy");
                return ResponseEntity.ok(status);
            } catch (Exception e) {
                status.setStatus(e.getMessage());
                return ResponseEntity.ok(status);
            }
        }

        if (!refType.isBlank() && refType.equalsIgnoreCase("spotify")) {
            try {
                //https://open.spotify.com/track/7z7kvUQGwlC6iOl7vMuAr9?si=2effec7337294894
                switch (getSpotifyLinkType(refid)) {
                    case "Track": {
                        taskService.sendClientMessage("GETTING SPOTIFY TRACK ...");
                        TaskNode taskNode = taskNodeService.createTask(
                            refid,
                            ReferenceType.SPOTIFY,
                            refid,
                            playlistId,
                            null,
                            DownloadType.AUDIO,
                            0,
                            user.getId()
                        );
                        break;
                    }
                    case "Playlist": {
                        if (!canDownloadPlaylists) {
                            status.setStatus("Playlist Download is disabled change it from settings in settings file");
                            taskService.sendClientMessage("Playlist Download is disabled change it from settings in settings file");
                            return ResponseEntity.ok(status);
                        }
                        taskService.sendClientMessage("GETTING SPOTIFY PLAYLIST TRACKS ...");
                        TaskNode taskNode = taskNodeService.createTask(
                            refid,
                            ReferenceType.SPOTIFY,
                            refid,
                            playlistId,
                            null,
                            DownloadType.AUDIO_PLAYLIST,
                            0,
                            user.getId()
                        );
                        break;
                    }
                    default: {
                        taskService.sendClientMessage("SPOTIFY LINK NOT FOUND");
                        status.setStatus("SPOTIFY LINK NOT FOUND");
                        return ResponseEntity.ok(status);
                    }
                }

                return ResponseEntity.ok(status);
            } catch (Exception e) {
                status.setStatus(e.getMessage());
                return ResponseEntity.ok(status);
            }
        }

        if (!refType.isBlank() && refType.equalsIgnoreCase("soundCloud")) {
            TaskNode taskNode = taskNodeService.createTask(
                refid,
                ReferenceType.SOUNDCLOUD,
                refid,
                playlistId,
                null,
                DownloadType.AUDIO,
                0,
                user.getId()
            );
            taskService.sendClientMessage("Track was added to tasks and download will start soon  Enjoy");
            return ResponseEntity.ok(status);
        }
        status.setStatus("Failed To Download Audio File From ");
        return ResponseEntity.ok(status);
    }
}
