package com.konsol.beatstream.domain;

import static com.konsol.beatstream.domain.ReferanceDownloadTaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReferanceDownloadTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReferanceDownloadTask.class);
        ReferanceDownloadTask referanceDownloadTask1 = getReferanceDownloadTaskSample1();
        ReferanceDownloadTask referanceDownloadTask2 = new ReferanceDownloadTask();
        assertThat(referanceDownloadTask1).isNotEqualTo(referanceDownloadTask2);

        referanceDownloadTask2.setId(referanceDownloadTask1.getId());
        assertThat(referanceDownloadTask1).isEqualTo(referanceDownloadTask2);

        referanceDownloadTask2 = getReferanceDownloadTaskSample2();
        assertThat(referanceDownloadTask1).isNotEqualTo(referanceDownloadTask2);
    }
}
