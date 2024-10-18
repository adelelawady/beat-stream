package com.konsol.beatstream.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.konsol.beatstream.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ListeningHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ListeningHistoryDTO.class);
        ListeningHistoryDTO listeningHistoryDTO1 = new ListeningHistoryDTO();
        listeningHistoryDTO1.setId("id1");
        ListeningHistoryDTO listeningHistoryDTO2 = new ListeningHistoryDTO();
        assertThat(listeningHistoryDTO1).isNotEqualTo(listeningHistoryDTO2);
        listeningHistoryDTO2.setId(listeningHistoryDTO1.getId());
        assertThat(listeningHistoryDTO1).isEqualTo(listeningHistoryDTO2);
        listeningHistoryDTO2.setId("id2");
        assertThat(listeningHistoryDTO1).isNotEqualTo(listeningHistoryDTO2);
        listeningHistoryDTO1.setId(null);
        assertThat(listeningHistoryDTO1).isNotEqualTo(listeningHistoryDTO2);
    }
}
