package com.konsol.beatstream.web.rest;

import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.api.dto.AudioUpload;
import com.konsol.beatstream.service.api.dto.Track;
import com.konsol.beatstream.web.api.AudioApi;
import com.konsol.beatstream.web.api.AudioApiDelegate;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AudioResource implements AudioApiDelegate {

    @Autowired
    TrackService trackService;

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
}
