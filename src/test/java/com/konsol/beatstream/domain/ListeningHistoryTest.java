package com.konsol.beatstream.domain;

import static com.konsol.beatstream.domain.ListeningHistoryTestSamples.*;
import static com.konsol.beatstream.domain.TrackTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ListeningHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ListeningHistory.class);
        ListeningHistory listeningHistory1 = getListeningHistorySample1();
        ListeningHistory listeningHistory2 = new ListeningHistory();
        assertThat(listeningHistory1).isNotEqualTo(listeningHistory2);

        listeningHistory2.setId(listeningHistory1.getId());
        assertThat(listeningHistory1).isEqualTo(listeningHistory2);

        listeningHistory2 = getListeningHistorySample2();
        assertThat(listeningHistory1).isNotEqualTo(listeningHistory2);
    }

    @Test
    void trackTest() {
        ListeningHistory listeningHistory = getListeningHistoryRandomSampleGenerator();
        Track trackBack = getTrackRandomSampleGenerator();

        listeningHistory.setTrack(trackBack);
        assertThat(listeningHistory.getTrack()).isEqualTo(trackBack);

        listeningHistory.track(null);
        assertThat(listeningHistory.getTrack()).isNull();
    }
}
