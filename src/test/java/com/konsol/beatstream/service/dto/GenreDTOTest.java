package com.konsol.beatstream.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GenreDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GenreDTO.class);
        GenreDTO genreDTO1 = new GenreDTO();
        genreDTO1.setId("id1");
        GenreDTO genreDTO2 = new GenreDTO();
        assertThat(genreDTO1).isNotEqualTo(genreDTO2);
        genreDTO2.setId(genreDTO1.getId());
        assertThat(genreDTO1).isEqualTo(genreDTO2);
        genreDTO2.setId("id2");
        assertThat(genreDTO1).isNotEqualTo(genreDTO2);
        genreDTO1.setId(null);
        assertThat(genreDTO1).isNotEqualTo(genreDTO2);
    }
}
