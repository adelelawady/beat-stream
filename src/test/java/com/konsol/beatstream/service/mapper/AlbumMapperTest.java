package com.konsol.beatstream.service.mapper;

import static com.konsol.beatstream.domain.AlbumAsserts.*;
import static com.konsol.beatstream.domain.AlbumTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlbumMapperTest {

    private AlbumMapper albumMapper;

    @BeforeEach
    void setUp() {
        albumMapper = new AlbumMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAlbumSample1();
        var actual = albumMapper.toEntity(albumMapper.toDto(expected));
        assertAlbumAllPropertiesEquals(expected, actual);
    }
}
