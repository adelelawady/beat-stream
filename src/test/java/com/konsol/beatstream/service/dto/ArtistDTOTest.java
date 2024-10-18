package com.konsol.beatstream.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ArtistDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ArtistDTO.class);
        ArtistDTO artistDTO1 = new ArtistDTO();
        artistDTO1.setId("id1");
        ArtistDTO artistDTO2 = new ArtistDTO();
        assertThat(artistDTO1).isNotEqualTo(artistDTO2);
        artistDTO2.setId(artistDTO1.getId());
        assertThat(artistDTO1).isEqualTo(artistDTO2);
        artistDTO2.setId("id2");
        assertThat(artistDTO1).isNotEqualTo(artistDTO2);
        artistDTO1.setId(null);
        assertThat(artistDTO1).isNotEqualTo(artistDTO2);
    }
}
