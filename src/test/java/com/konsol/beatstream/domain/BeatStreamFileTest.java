package com.konsol.beatstream.domain;

import static com.konsol.beatstream.domain.BeatStreamFileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BeatStreamFileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BeatStreamFile.class);
        BeatStreamFile beatStreamFile1 = getBeatStreamFileSample1();
        BeatStreamFile beatStreamFile2 = new BeatStreamFile();
        assertThat(beatStreamFile1).isNotEqualTo(beatStreamFile2);

        beatStreamFile2.setId(beatStreamFile1.getId());
        assertThat(beatStreamFile1).isEqualTo(beatStreamFile2);

        beatStreamFile2 = getBeatStreamFileSample2();
        assertThat(beatStreamFile1).isNotEqualTo(beatStreamFile2);
    }
}
