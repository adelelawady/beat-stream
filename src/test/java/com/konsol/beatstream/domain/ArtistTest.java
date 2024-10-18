package com.konsol.beatstream.domain;

import static com.konsol.beatstream.domain.ArtistTestSamples.*;
import static com.konsol.beatstream.domain.GenreTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ArtistTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Artist.class);
        Artist artist1 = getArtistSample1();
        Artist artist2 = new Artist();
        assertThat(artist1).isNotEqualTo(artist2);

        artist2.setId(artist1.getId());
        assertThat(artist1).isEqualTo(artist2);

        artist2 = getArtistSample2();
        assertThat(artist1).isNotEqualTo(artist2);
    }

    @Test
    void genreTest() {
        Artist artist = getArtistRandomSampleGenerator();
        Genre genreBack = getGenreRandomSampleGenerator();

        artist.addGenre(genreBack);
        assertThat(artist.getGenres()).containsOnly(genreBack);
        assertThat(genreBack.getArtists()).containsOnly(artist);

        artist.removeGenre(genreBack);
        assertThat(artist.getGenres()).doesNotContain(genreBack);
        assertThat(genreBack.getArtists()).doesNotContain(artist);

        artist.genres(new HashSet<>(Set.of(genreBack)));
        assertThat(artist.getGenres()).containsOnly(genreBack);
        assertThat(genreBack.getArtists()).containsOnly(artist);

        artist.setGenres(new HashSet<>());
        assertThat(artist.getGenres()).doesNotContain(genreBack);
        assertThat(genreBack.getArtists()).doesNotContain(artist);
    }
}
