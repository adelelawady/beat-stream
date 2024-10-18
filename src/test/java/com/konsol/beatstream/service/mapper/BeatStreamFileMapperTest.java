package com.konsol.beatstream.service.mapper;

import static com.konsol.beatstream.domain.BeatStreamFileAsserts.*;
import static com.konsol.beatstream.domain.BeatStreamFileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeatStreamFileMapperTest {

    private BeatStreamFileMapper beatStreamFileMapper;

    @BeforeEach
    void setUp() {
        beatStreamFileMapper = new BeatStreamFileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBeatStreamFileSample1();
        var actual = beatStreamFileMapper.toEntity(beatStreamFileMapper.toDto(expected));
        assertBeatStreamFileAllPropertiesEquals(expected, actual);
    }
}
