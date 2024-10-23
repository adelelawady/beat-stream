package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.domain.ReferanceDownloadTaskAsserts.*;
import static com.konsol.beatstream.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konsol.beatstream.IntegrationTest;
import com.konsol.beatstream.domain.ReferanceDownloadTask;
import com.konsol.beatstream.repository.ReferanceDownloadTaskRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ReferanceDownloadTaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReferanceDownloadTaskResourceIT {

    private static final String DEFAULT_REFERANCE_ID = "AAAAAAAAAA";
    private static final String UPDATED_REFERANCE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_REFERANCE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_REFERANCE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_REFERANCE_TRACK_ID = "AAAAAAAAAA";
    private static final String UPDATED_REFERANCE_TRACK_ID = "BBBBBBBBBB";

    private static final String DEFAULT_REFERANCE_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_REFERANCE_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_REFERANCE_SCHEDULE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REFERANCE_SCHEDULE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REFERANCE_LOG = "AAAAAAAAAA";
    private static final String UPDATED_REFERANCE_LOG = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/referance-download-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReferanceDownloadTaskRepository referanceDownloadTaskRepository;

    @Autowired
    private MockMvc restReferanceDownloadTaskMockMvc;

    private ReferanceDownloadTask referanceDownloadTask;

    private ReferanceDownloadTask insertedReferanceDownloadTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReferanceDownloadTask createEntity() {
        return new ReferanceDownloadTask()
            .referanceId(DEFAULT_REFERANCE_ID)
            .referanceType(DEFAULT_REFERANCE_TYPE)
            .referanceTrackId(DEFAULT_REFERANCE_TRACK_ID)
            .referanceStatus(DEFAULT_REFERANCE_STATUS)
            .referanceScheduleDate(DEFAULT_REFERANCE_SCHEDULE_DATE)
            .referanceLog(DEFAULT_REFERANCE_LOG);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReferanceDownloadTask createUpdatedEntity() {
        return new ReferanceDownloadTask()
            .referanceId(UPDATED_REFERANCE_ID)
            .referanceType(UPDATED_REFERANCE_TYPE)
            .referanceTrackId(UPDATED_REFERANCE_TRACK_ID)
            .referanceStatus(UPDATED_REFERANCE_STATUS)
            .referanceScheduleDate(UPDATED_REFERANCE_SCHEDULE_DATE)
            .referanceLog(UPDATED_REFERANCE_LOG);
    }

    @BeforeEach
    public void initTest() {
        referanceDownloadTask = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReferanceDownloadTask != null) {
            referanceDownloadTaskRepository.delete(insertedReferanceDownloadTask);
            insertedReferanceDownloadTask = null;
        }
    }

    @Test
    void createReferanceDownloadTask() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReferanceDownloadTask
        var returnedReferanceDownloadTask = om.readValue(
            restReferanceDownloadTaskMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referanceDownloadTask)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReferanceDownloadTask.class
        );

        // Validate the ReferanceDownloadTask in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertReferanceDownloadTaskUpdatableFieldsEquals(
            returnedReferanceDownloadTask,
            getPersistedReferanceDownloadTask(returnedReferanceDownloadTask)
        );

        insertedReferanceDownloadTask = returnedReferanceDownloadTask;
    }

    @Test
    void createReferanceDownloadTaskWithExistingId() throws Exception {
        // Create the ReferanceDownloadTask with an existing ID
        referanceDownloadTask.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReferanceDownloadTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referanceDownloadTask)))
            .andExpect(status().isBadRequest());

        // Validate the ReferanceDownloadTask in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllReferanceDownloadTasks() throws Exception {
        // Initialize the database
        insertedReferanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);

        // Get all the referanceDownloadTaskList
        restReferanceDownloadTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(referanceDownloadTask.getId())))
            .andExpect(jsonPath("$.[*].referanceId").value(hasItem(DEFAULT_REFERANCE_ID)))
            .andExpect(jsonPath("$.[*].referanceType").value(hasItem(DEFAULT_REFERANCE_TYPE)))
            .andExpect(jsonPath("$.[*].referanceTrackId").value(hasItem(DEFAULT_REFERANCE_TRACK_ID)))
            .andExpect(jsonPath("$.[*].referanceStatus").value(hasItem(DEFAULT_REFERANCE_STATUS)))
            .andExpect(jsonPath("$.[*].referanceScheduleDate").value(hasItem(DEFAULT_REFERANCE_SCHEDULE_DATE.toString())))
            .andExpect(jsonPath("$.[*].referanceLog").value(hasItem(DEFAULT_REFERANCE_LOG)));
    }

    @Test
    void getReferanceDownloadTask() throws Exception {
        // Initialize the database
        insertedReferanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);

        // Get the referanceDownloadTask
        restReferanceDownloadTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, referanceDownloadTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(referanceDownloadTask.getId()))
            .andExpect(jsonPath("$.referanceId").value(DEFAULT_REFERANCE_ID))
            .andExpect(jsonPath("$.referanceType").value(DEFAULT_REFERANCE_TYPE))
            .andExpect(jsonPath("$.referanceTrackId").value(DEFAULT_REFERANCE_TRACK_ID))
            .andExpect(jsonPath("$.referanceStatus").value(DEFAULT_REFERANCE_STATUS))
            .andExpect(jsonPath("$.referanceScheduleDate").value(DEFAULT_REFERANCE_SCHEDULE_DATE.toString()))
            .andExpect(jsonPath("$.referanceLog").value(DEFAULT_REFERANCE_LOG));
    }

    @Test
    void getNonExistingReferanceDownloadTask() throws Exception {
        // Get the referanceDownloadTask
        restReferanceDownloadTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingReferanceDownloadTask() throws Exception {
        // Initialize the database
        insertedReferanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the referanceDownloadTask
        ReferanceDownloadTask updatedReferanceDownloadTask = referanceDownloadTaskRepository
            .findById(referanceDownloadTask.getId())
            .orElseThrow();
        updatedReferanceDownloadTask
            .referanceId(UPDATED_REFERANCE_ID)
            .referanceType(UPDATED_REFERANCE_TYPE)
            .referanceTrackId(UPDATED_REFERANCE_TRACK_ID)
            .referanceStatus(UPDATED_REFERANCE_STATUS)
            .referanceScheduleDate(UPDATED_REFERANCE_SCHEDULE_DATE)
            .referanceLog(UPDATED_REFERANCE_LOG);

        restReferanceDownloadTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReferanceDownloadTask.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedReferanceDownloadTask))
            )
            .andExpect(status().isOk());

        // Validate the ReferanceDownloadTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReferanceDownloadTaskToMatchAllProperties(updatedReferanceDownloadTask);
    }

    @Test
    void putNonExistingReferanceDownloadTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referanceDownloadTask.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferanceDownloadTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, referanceDownloadTask.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(referanceDownloadTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReferanceDownloadTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchReferanceDownloadTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referanceDownloadTask.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferanceDownloadTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(referanceDownloadTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReferanceDownloadTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamReferanceDownloadTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referanceDownloadTask.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferanceDownloadTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referanceDownloadTask)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReferanceDownloadTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateReferanceDownloadTaskWithPatch() throws Exception {
        // Initialize the database
        insertedReferanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the referanceDownloadTask using partial update
        ReferanceDownloadTask partialUpdatedReferanceDownloadTask = new ReferanceDownloadTask();
        partialUpdatedReferanceDownloadTask.setId(referanceDownloadTask.getId());

        partialUpdatedReferanceDownloadTask.referanceType(UPDATED_REFERANCE_TYPE).referanceTrackId(UPDATED_REFERANCE_TRACK_ID);

        restReferanceDownloadTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReferanceDownloadTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReferanceDownloadTask))
            )
            .andExpect(status().isOk());

        // Validate the ReferanceDownloadTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReferanceDownloadTaskUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReferanceDownloadTask, referanceDownloadTask),
            getPersistedReferanceDownloadTask(referanceDownloadTask)
        );
    }

    @Test
    void fullUpdateReferanceDownloadTaskWithPatch() throws Exception {
        // Initialize the database
        insertedReferanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the referanceDownloadTask using partial update
        ReferanceDownloadTask partialUpdatedReferanceDownloadTask = new ReferanceDownloadTask();
        partialUpdatedReferanceDownloadTask.setId(referanceDownloadTask.getId());

        partialUpdatedReferanceDownloadTask
            .referanceId(UPDATED_REFERANCE_ID)
            .referanceType(UPDATED_REFERANCE_TYPE)
            .referanceTrackId(UPDATED_REFERANCE_TRACK_ID)
            .referanceStatus(UPDATED_REFERANCE_STATUS)
            .referanceScheduleDate(UPDATED_REFERANCE_SCHEDULE_DATE)
            .referanceLog(UPDATED_REFERANCE_LOG);

        restReferanceDownloadTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReferanceDownloadTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReferanceDownloadTask))
            )
            .andExpect(status().isOk());

        // Validate the ReferanceDownloadTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReferanceDownloadTaskUpdatableFieldsEquals(
            partialUpdatedReferanceDownloadTask,
            getPersistedReferanceDownloadTask(partialUpdatedReferanceDownloadTask)
        );
    }

    @Test
    void patchNonExistingReferanceDownloadTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referanceDownloadTask.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferanceDownloadTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, referanceDownloadTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(referanceDownloadTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReferanceDownloadTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchReferanceDownloadTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referanceDownloadTask.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferanceDownloadTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(referanceDownloadTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReferanceDownloadTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamReferanceDownloadTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referanceDownloadTask.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferanceDownloadTaskMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(referanceDownloadTask)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReferanceDownloadTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteReferanceDownloadTask() throws Exception {
        // Initialize the database
        insertedReferanceDownloadTask = referanceDownloadTaskRepository.save(referanceDownloadTask);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the referanceDownloadTask
        restReferanceDownloadTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, referanceDownloadTask.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return referanceDownloadTaskRepository.count();
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

    protected ReferanceDownloadTask getPersistedReferanceDownloadTask(ReferanceDownloadTask referanceDownloadTask) {
        return referanceDownloadTaskRepository.findById(referanceDownloadTask.getId()).orElseThrow();
    }

    protected void assertPersistedReferanceDownloadTaskToMatchAllProperties(ReferanceDownloadTask expectedReferanceDownloadTask) {
        assertReferanceDownloadTaskAllPropertiesEquals(
            expectedReferanceDownloadTask,
            getPersistedReferanceDownloadTask(expectedReferanceDownloadTask)
        );
    }

    protected void assertPersistedReferanceDownloadTaskToMatchUpdatableProperties(ReferanceDownloadTask expectedReferanceDownloadTask) {
        assertReferanceDownloadTaskAllUpdatablePropertiesEquals(
            expectedReferanceDownloadTask,
            getPersistedReferanceDownloadTask(expectedReferanceDownloadTask)
        );
    }
}
