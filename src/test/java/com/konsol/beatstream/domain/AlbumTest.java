package com.konsol.beatstream.domain;

import static com.konsol.beatstream.domain.AlbumTestSamples.*;
import static com.konsol.beatstream.domain.ArtistTestSamples.*;
import static com.konsol.beatstream.domain.GenreTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AlbumTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Album.class);
        Album album1 = getAlbumSample1();
        Album album2 = new Album();
        assertThat(album1).isNotEqualTo(album2);

        album2.setId(album1.getId());
        assertThat(album1).isEqualTo(album2);

        album2 = getAlbumSample2();
        assertThat(album1).isNotEqualTo(album2);
    }

    @Test
    void artistTest() {
        Album album = getAlbumRandomSampleGenerator();
        Artist artistBack = getArtistRandomSampleGenerator();

        album.setArtist(artistBack);
        assertThat(album.getArtist()).isEqualTo(artistBack);

        album.artist(null);
        assertThat(album.getArtist()).isNull();
    }

    @Test
    void genreTest() {
        Album album = getAlbumRandomSampleGenerator();
        Genre genreBack = getGenreRandomSampleGenerator();

        album.addGenre(genreBack);
        assertThat(album.getGenres()).containsOnly(genreBack);

        album.removeGenre(genreBack);
        assertThat(album.getGenres()).doesNotContain(genreBack);

        album.genres(new HashSet<>(Set.of(genreBack)));
        assertThat(album.getGenres()).containsOnly(genreBack);

        album.setGenres(new HashSet<>());
        assertThat(album.getGenres()).doesNotContain(genreBack);
    }
}
