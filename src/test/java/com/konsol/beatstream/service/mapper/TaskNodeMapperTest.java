package com.konsol.beatstream.service.mapper;

import static com.konsol.beatstream.domain.TaskNodeAsserts.*;
import static com.konsol.beatstream.domain.TaskNodeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskNodeMapperTest {

    private TaskNodeMapper taskNodeMapper;

    @BeforeEach
    void setUp() {
        taskNodeMapper = new TaskNodeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTaskNodeSample1();
        var actual = taskNodeMapper.toEntity(taskNodeMapper.toDto(expected));
        assertTaskNodeAllPropertiesEquals(expected, actual);
    }
}
