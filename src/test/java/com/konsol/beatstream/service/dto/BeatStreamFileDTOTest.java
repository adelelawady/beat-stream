package com.konsol.beatstream.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BeatStreamFileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BeatStreamFileDTO.class);
        BeatStreamFileDTO beatStreamFileDTO1 = new BeatStreamFileDTO();
        beatStreamFileDTO1.setId("id1");
        BeatStreamFileDTO beatStreamFileDTO2 = new BeatStreamFileDTO();
        assertThat(beatStreamFileDTO1).isNotEqualTo(beatStreamFileDTO2);
        beatStreamFileDTO2.setId(beatStreamFileDTO1.getId());
        assertThat(beatStreamFileDTO1).isEqualTo(beatStreamFileDTO2);
        beatStreamFileDTO2.setId("id2");
        assertThat(beatStreamFileDTO1).isNotEqualTo(beatStreamFileDTO2);
        beatStreamFileDTO1.setId(null);
        assertThat(beatStreamFileDTO1).isNotEqualTo(beatStreamFileDTO2);
    }
}
