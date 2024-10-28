package com.konsol.beatstream.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskNodeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskNodeDTO.class);
        TaskNodeDTO taskNodeDTO1 = new TaskNodeDTO();
        taskNodeDTO1.setId("id1");
        TaskNodeDTO taskNodeDTO2 = new TaskNodeDTO();
        assertThat(taskNodeDTO1).isNotEqualTo(taskNodeDTO2);
        taskNodeDTO2.setId(taskNodeDTO1.getId());
        assertThat(taskNodeDTO1).isEqualTo(taskNodeDTO2);
        taskNodeDTO2.setId("id2");
        assertThat(taskNodeDTO1).isNotEqualTo(taskNodeDTO2);
        taskNodeDTO1.setId(null);
        assertThat(taskNodeDTO1).isNotEqualTo(taskNodeDTO2);
    }
}
