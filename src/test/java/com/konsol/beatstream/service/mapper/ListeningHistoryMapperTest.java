package com.konsol.beatstream.service.mapper;

import static com.konsol.beatstream.domain.ListeningHistoryAsserts.*;
import static com.konsol.beatstream.domain.ListeningHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListeningHistoryMapperTest {

    private ListeningHistoryMapper listeningHistoryMapper;

    @BeforeEach
    void setUp() {
        listeningHistoryMapper = new ListeningHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getListeningHistorySample1();
        var actual = listeningHistoryMapper.toEntity(listeningHistoryMapper.toDto(expected));
        assertListeningHistoryAllPropertiesEquals(expected, actual);
    }
}
