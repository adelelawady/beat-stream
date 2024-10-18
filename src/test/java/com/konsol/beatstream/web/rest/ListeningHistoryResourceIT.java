package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.domain.ListeningHistoryAsserts.*;
import static com.konsol.beatstream.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konsol.beatstream.IntegrationTest;
import com.konsol.beatstream.domain.ListeningHistory;
import com.konsol.beatstream.repository.ListeningHistoryRepository;
import com.konsol.beatstream.service.dto.ListeningHistoryDTO;
import com.konsol.beatstream.service.mapper.ListeningHistoryMapper;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link ListeningHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ListeningHistoryResourceIT {

    private static final String DEFAULT_TIMESTAMP = "AAAAAAAAAA";
    private static final String UPDATED_TIMESTAMP = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/listening-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ListeningHistoryRepository listeningHistoryRepository;

    @Autowired
    private ListeningHistoryMapper listeningHistoryMapper;

    @Autowired
    private MockMvc restListeningHistoryMockMvc;

    private ListeningHistory listeningHistory;

    private ListeningHistory insertedListeningHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ListeningHistory createEntity() {
        return new ListeningHistory().timestamp(DEFAULT_TIMESTAMP);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ListeningHistory createUpdatedEntity() {
        return new ListeningHistory().timestamp(UPDATED_TIMESTAMP);
    }

    @BeforeEach
    public void initTest() {
        listeningHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedListeningHistory != null) {
            listeningHistoryRepository.delete(insertedListeningHistory);
            insertedListeningHistory = null;
        }
    }

    @Test
    void createListeningHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ListeningHistory
        ListeningHistoryDTO listeningHistoryDTO = listeningHistoryMapper.toDto(listeningHistory);
        var returnedListeningHistoryDTO = om.readValue(
            restListeningHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(listeningHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ListeningHistoryDTO.class
        );

        // Validate the ListeningHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedListeningHistory = listeningHistoryMapper.toEntity(returnedListeningHistoryDTO);
        assertListeningHistoryUpdatableFieldsEquals(returnedListeningHistory, getPersistedListeningHistory(returnedListeningHistory));

        insertedListeningHistory = returnedListeningHistory;
    }

    @Test
    void createListeningHistoryWithExistingId() throws Exception {
        // Create the ListeningHistory with an existing ID
        listeningHistory.setId("existing_id");
        ListeningHistoryDTO listeningHistoryDTO = listeningHistoryMapper.toDto(listeningHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restListeningHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(listeningHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ListeningHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllListeningHistories() throws Exception {
        // Initialize the database
        insertedListeningHistory = listeningHistoryRepository.save(listeningHistory);

        // Get all the listeningHistoryList
        restListeningHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(listeningHistory.getId())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    void getListeningHistory() throws Exception {
        // Initialize the database
        insertedListeningHistory = listeningHistoryRepository.save(listeningHistory);

        // Get the listeningHistory
        restListeningHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, listeningHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(listeningHistory.getId()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP));
    }

    @Test
    void getNonExistingListeningHistory() throws Exception {
        // Get the listeningHistory
        restListeningHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingListeningHistory() throws Exception {
        // Initialize the database
        insertedListeningHistory = listeningHistoryRepository.save(listeningHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the listeningHistory
        ListeningHistory updatedListeningHistory = listeningHistoryRepository.findById(listeningHistory.getId()).orElseThrow();
        updatedListeningHistory.timestamp(UPDATED_TIMESTAMP);
        ListeningHistoryDTO listeningHistoryDTO = listeningHistoryMapper.toDto(updatedListeningHistory);

        restListeningHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, listeningHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(listeningHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the ListeningHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedListeningHistoryToMatchAllProperties(updatedListeningHistory);
    }

    @Test
    void putNonExistingListeningHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        listeningHistory.setId(UUID.randomUUID().toString());

        // Create the ListeningHistory
        ListeningHistoryDTO listeningHistoryDTO = listeningHistoryMapper.toDto(listeningHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restListeningHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, listeningHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(listeningHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ListeningHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchListeningHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        listeningHistory.setId(UUID.randomUUID().toString());

        // Create the ListeningHistory
        ListeningHistoryDTO listeningHistoryDTO = listeningHistoryMapper.toDto(listeningHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restListeningHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(listeningHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ListeningHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamListeningHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        listeningHistory.setId(UUID.randomUUID().toString());

        // Create the ListeningHistory
        ListeningHistoryDTO listeningHistoryDTO = listeningHistoryMapper.toDto(listeningHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restListeningHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(listeningHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ListeningHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateListeningHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedListeningHistory = listeningHistoryRepository.save(listeningHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the listeningHistory using partial update
        ListeningHistory partialUpdatedListeningHistory = new ListeningHistory();
        partialUpdatedListeningHistory.setId(listeningHistory.getId());

        partialUpdatedListeningHistory.timestamp(UPDATED_TIMESTAMP);

        restListeningHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedListeningHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedListeningHistory))
            )
            .andExpect(status().isOk());

        // Validate the ListeningHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertListeningHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedListeningHistory, listeningHistory),
            getPersistedListeningHistory(listeningHistory)
        );
    }

    @Test
    void fullUpdateListeningHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedListeningHistory = listeningHistoryRepository.save(listeningHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the listeningHistory using partial update
        ListeningHistory partialUpdatedListeningHistory = new ListeningHistory();
        partialUpdatedListeningHistory.setId(listeningHistory.getId());

        partialUpdatedListeningHistory.timestamp(UPDATED_TIMESTAMP);

        restListeningHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedListeningHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedListeningHistory))
            )
            .andExpect(status().isOk());

        // Validate the ListeningHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertListeningHistoryUpdatableFieldsEquals(
            partialUpdatedListeningHistory,
            getPersistedListeningHistory(partialUpdatedListeningHistory)
        );
    }

    @Test
    void patchNonExistingListeningHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        listeningHistory.setId(UUID.randomUUID().toString());

        // Create the ListeningHistory
        ListeningHistoryDTO listeningHistoryDTO = listeningHistoryMapper.toDto(listeningHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restListeningHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, listeningHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(listeningHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ListeningHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchListeningHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        listeningHistory.setId(UUID.randomUUID().toString());

        // Create the ListeningHistory
        ListeningHistoryDTO listeningHistoryDTO = listeningHistoryMapper.toDto(listeningHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restListeningHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(listeningHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ListeningHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamListeningHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        listeningHistory.setId(UUID.randomUUID().toString());

        // Create the ListeningHistory
        ListeningHistoryDTO listeningHistoryDTO = listeningHistoryMapper.toDto(listeningHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restListeningHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(listeningHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ListeningHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteListeningHistory() throws Exception {
        // Initialize the database
        insertedListeningHistory = listeningHistoryRepository.save(listeningHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the listeningHistory
        restListeningHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, listeningHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return listeningHistoryRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ListeningHistory getPersistedListeningHistory(ListeningHistory listeningHistory) {
        return listeningHistoryRepository.findById(listeningHistory.getId()).orElseThrow();
    }

    protected void assertPersistedListeningHistoryToMatchAllProperties(ListeningHistory expectedListeningHistory) {
        assertListeningHistoryAllPropertiesEquals(expectedListeningHistory, getPersistedListeningHistory(expectedListeningHistory));
    }

    protected void assertPersistedListeningHistoryToMatchUpdatableProperties(ListeningHistory expectedListeningHistory) {
        assertListeningHistoryAllUpdatablePropertiesEquals(
            expectedListeningHistory,
            getPersistedListeningHistory(expectedListeningHistory)
        );
    }
}
