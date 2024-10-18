package com.konsol.beatstream.domain;

import static com.konsol.beatstream.domain.PlaylistTestSamples.*;
import static com.konsol.beatstream.domain.TrackTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PlaylistTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Playlist.class);
        Playlist playlist1 = getPlaylistSample1();
        Playlist playlist2 = new Playlist();
        assertThat(playlist1).isNotEqualTo(playlist2);

        playlist2.setId(playlist1.getId());
        assertThat(playlist1).isEqualTo(playlist2);

        playlist2 = getPlaylistSample2();
        assertThat(playlist1).isNotEqualTo(playlist2);
    }

    @Test
    void trackTest() {
        Playlist playlist = getPlaylistRandomSampleGenerator();
        Track trackBack = getTrackRandomSampleGenerator();

        playlist.addTrack(trackBack);
        assertThat(playlist.getTracks()).containsOnly(trackBack);

        playlist.removeTrack(trackBack);
        assertThat(playlist.getTracks()).doesNotContain(trackBack);

        playlist.tracks(new HashSet<>(Set.of(trackBack)));
        assertThat(playlist.getTracks()).containsOnly(trackBack);

        playlist.setTracks(new HashSet<>());
        assertThat(playlist.getTracks()).doesNotContain(trackBack);
    }
}
