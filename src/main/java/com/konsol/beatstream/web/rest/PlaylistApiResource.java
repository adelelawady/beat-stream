package com.konsol.beatstream.web.rest;

import com.konsol.beatstream.service.PlaylistService;
import com.konsol.beatstream.service.api.dto.Playlist;
import com.konsol.beatstream.web.api.PlaylistApi;
import com.konsol.beatstream.web.api.PlaylistApiDelegate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PlaylistApiResource implements PlaylistApiDelegate {

    @Autowired
    PlaylistService playlistService;

    @Override
    public ResponseEntity<Playlist> getPlaylist(String id) {
        return ResponseEntity.ok(playlistService.getPlaylist(id));
    }

    @Override
    public ResponseEntity<List<Playlist>> getPlaylists() {
        return ResponseEntity.ok(playlistService.getMyPlaylists());
    }

    @Override
    public ResponseEntity<Playlist> createPlaylist(Playlist playlist) {
        return ResponseEntity.ok(playlistService.createPlaylist(playlist));
    }

    @Override
    public ResponseEntity<Void> deletePlaylist(String id) {
        playlistService.delete(id);
        return ResponseEntity.ok().build();
    }
}
