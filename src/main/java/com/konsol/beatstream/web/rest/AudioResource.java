package com.konsol.beatstream.web.rest;

import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.api.dto.Status;
import com.konsol.beatstream.service.api.dto.Track;
import com.konsol.beatstream.service.audioPlugins.SoundCloud.SoundCloudDownloader;
import com.konsol.beatstream.service.audioPlugins.youtube.YoutubeDownloader;
import com.konsol.beatstream.web.api.AudioApi;
import com.konsol.beatstream.web.api.AudioApiDelegate;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    YoutubeDownloader youtubeDownloader;

    @Autowired
    SoundCloudDownloader soundCloudDownloader;

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
        status.setStatus("Video Added To Downloading List And Will be downloaded soon");
        if (!refType.isBlank() && refType.equalsIgnoreCase("youtube")) {
            youtubeDownloader.AddYoutubeVideo(refid, playlistId);

            return ResponseEntity.ok(status);
        }

        if (!refType.isBlank() && refType.equalsIgnoreCase("soundCloud")) {
            soundCloudDownloader.addSoundCloudLink(refid, playlistId);

            return ResponseEntity.ok(status);
        }
        status.setStatus("Failed To Download Audio File From ");
        return ResponseEntity.ok(status);
    }
}
