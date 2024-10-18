package com.konsol.beatstream.domain;

import static com.konsol.beatstream.domain.AlbumTestSamples.*;
import static com.konsol.beatstream.domain.ArtistTestSamples.*;
import static com.konsol.beatstream.domain.GenreTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GenreTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Genre.class);
        Genre genre1 = getGenreSample1();
        Genre genre2 = new Genre();
        assertThat(genre1).isNotEqualTo(genre2);

        genre2.setId(genre1.getId());
        assertThat(genre1).isEqualTo(genre2);

        genre2 = getGenreSample2();
        assertThat(genre1).isNotEqualTo(genre2);
    }

    @Test
    void artistTest() {
        Genre genre = getGenreRandomSampleGenerator();
        Artist artistBack = getArtistRandomSampleGenerator();

        genre.addArtist(artistBack);
        assertThat(genre.getArtists()).containsOnly(artistBack);

        genre.removeArtist(artistBack);
        assertThat(genre.getArtists()).doesNotContain(artistBack);

        genre.artists(new HashSet<>(Set.of(artistBack)));
        assertThat(genre.getArtists()).containsOnly(artistBack);

        genre.setArtists(new HashSet<>());
        assertThat(genre.getArtists()).doesNotContain(artistBack);
    }

    @Test
    void albumTest() {
        Genre genre = getGenreRandomSampleGenerator();
        Album albumBack = getAlbumRandomSampleGenerator();

        genre.addAlbum(albumBack);
        assertThat(genre.getAlbums()).containsOnly(albumBack);
        assertThat(albumBack.getGenres()).containsOnly(genre);

        genre.removeAlbum(albumBack);
        assertThat(genre.getAlbums()).doesNotContain(albumBack);
        assertThat(albumBack.getGenres()).doesNotContain(genre);

        genre.albums(new HashSet<>(Set.of(albumBack)));
        assertThat(genre.getAlbums()).containsOnly(albumBack);
        assertThat(albumBack.getGenres()).containsOnly(genre);

        genre.setAlbums(new HashSet<>());
        assertThat(genre.getAlbums()).doesNotContain(albumBack);
        assertThat(albumBack.getGenres()).doesNotContain(genre);
    }
}
