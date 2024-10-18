package com.konsol.beatstream.domain;

import static com.konsol.beatstream.domain.AlbumTestSamples.*;
import static com.konsol.beatstream.domain.ArtistTestSamples.*;
import static com.konsol.beatstream.domain.PlaylistTestSamples.*;
import static com.konsol.beatstream.domain.TrackTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TrackTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Track.class);
        Track track1 = getTrackSample1();
        Track track2 = new Track();
        assertThat(track1).isNotEqualTo(track2);

        track2.setId(track1.getId());
        assertThat(track1).isEqualTo(track2);

        track2 = getTrackSample2();
        assertThat(track1).isNotEqualTo(track2);
    }

    @Test
    void artistTest() {
        Track track = getTrackRandomSampleGenerator();
        Artist artistBack = getArtistRandomSampleGenerator();

        track.setArtist(artistBack);
        assertThat(track.getArtist()).isEqualTo(artistBack);

        track.artist(null);
        assertThat(track.getArtist()).isNull();
    }

    @Test
    void albumTest() {
        Track track = getTrackRandomSampleGenerator();
        Album albumBack = getAlbumRandomSampleGenerator();

        track.setAlbum(albumBack);
        assertThat(track.getAlbum()).isEqualTo(albumBack);

        track.album(null);
        assertThat(track.getAlbum()).isNull();
    }

    @Test
    void playlistTest() {
        Track track = getTrackRandomSampleGenerator();
        Playlist playlistBack = getPlaylistRandomSampleGenerator();

        track.addPlaylist(playlistBack);
        assertThat(track.getPlaylists()).containsOnly(playlistBack);
        assertThat(playlistBack.getTracks()).containsOnly(track);

        track.removePlaylist(playlistBack);
        assertThat(track.getPlaylists()).doesNotContain(playlistBack);
        assertThat(playlistBack.getTracks()).doesNotContain(track);

        track.playlists(new HashSet<>(Set.of(playlistBack)));
        assertThat(track.getPlaylists()).containsOnly(playlistBack);
        assertThat(playlistBack.getTracks()).containsOnly(track);

        track.setPlaylists(new HashSet<>());
        assertThat(track.getPlaylists()).doesNotContain(playlistBack);
        assertThat(playlistBack.getTracks()).doesNotContain(track);
    }
}
