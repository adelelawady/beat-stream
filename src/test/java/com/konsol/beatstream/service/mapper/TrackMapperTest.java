package com.konsol.beatstream.service.mapper;

import static com.konsol.beatstream.domain.TrackAsserts.*;
import static com.konsol.beatstream.domain.TrackTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrackMapperTest {

    private TrackMapper trackMapper;

    @BeforeEach
    void setUp() {
        trackMapper = new TrackMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTrackSample1();
        var actual = trackMapper.toEntity(trackMapper.toDto(expected));
        assertTrackAllPropertiesEquals(expected, actual);
    }
}
