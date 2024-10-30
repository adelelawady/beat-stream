package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.domain.TaskNodeAsserts.*;
import static com.konsol.beatstream.web.rest.TestUtil.createUpdateProxyForBean;
import static com.konsol.beatstream.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konsol.beatstream.IntegrationTest;
import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.domain.enumeration.DownloadStatus;
import com.konsol.beatstream.domain.enumeration.DownloadType;
import com.konsol.beatstream.domain.enumeration.ReferenceType;
import com.konsol.beatstream.repository.TaskNodeRepository;
import com.konsol.beatstream.service.dto.TaskNodeDTO;
import com.konsol.beatstream.service.mapper.TaskNodeMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link TaskNodeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskNodeResourceIT {

    private static final ReferenceType UPDATED_REFERENCE_TYPE = ReferenceType.YOUTUBE;

    private static final Long DEFAULT_REFERENCE_ID = 1L;
    private static final Long UPDATED_REFERENCE_ID = 2L;

    private static final String DEFAULT_TASK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TASK_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TASK_LOG = "AAAAAAAAAA";
    private static final String UPDATED_TASK_LOG = "BBBBBBBBBB";

    private static final Long DEFAULT_TRACK_ID = 1L;
    private static final Long UPDATED_TRACK_ID = 2L;

    private static final Instant DEFAULT_SCHEDULED_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SCHEDULED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_START_DELAY_MINUTES = new BigDecimal(1);
    private static final BigDecimal UPDATED_START_DELAY_MINUTES = new BigDecimal(2);

    private static final BigDecimal DEFAULT_START_DELAY_HOURS = new BigDecimal(1);
    private static final BigDecimal UPDATED_START_DELAY_HOURS = new BigDecimal(2);

    private static final BigDecimal DEFAULT_ELAPSED_HOURS = new BigDecimal(1);
    private static final BigDecimal UPDATED_ELAPSED_HOURS = new BigDecimal(2);

    private static final BigDecimal DEFAULT_ELAPSED_MINUTES = new BigDecimal(1);
    private static final BigDecimal UPDATED_ELAPSED_MINUTES = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PROGRESS_PERCENTAGE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PROGRESS_PERCENTAGE = new BigDecimal(2);

    private static final String DEFAULT_DOWNLOAD_FILESIZE = "AAAAAAAAAA";
    private static final String UPDATED_DOWNLOAD_FILESIZE = "BBBBBBBBBB";

    private static final String DEFAULT_DOWNLOAD_SPEED = "AAAAAAAAAA";
    private static final String UPDATED_DOWNLOAD_SPEED = "BBBBBBBBBB";

    private static final String DEFAULT_DOWNLOAD_ETA = "AAAAAAAAAA";
    private static final String UPDATED_DOWNLOAD_ETA = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_NODE_INDEX = new BigDecimal(1);
    private static final BigDecimal UPDATED_NODE_INDEX = new BigDecimal(2);

    private static final DownloadStatus DEFAULT_STATUS = DownloadStatus.CONVERSION;
    private static final DownloadStatus UPDATED_STATUS = DownloadStatus.PENDING;

    private static final DownloadType DEFAULT_TYPE = DownloadType.FILE;
    private static final DownloadType UPDATED_TYPE = DownloadType.AUDIO;

    private static final BigDecimal DEFAULT_FAIL_COUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_FAIL_COUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_RETRY_COUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_RETRY_COUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_MAX_RETRY_COUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MAX_RETRY_COUNT = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/task-nodes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskNodeRepository taskNodeRepository;

    @Autowired
    private TaskNodeMapper taskNodeMapper;

    @Autowired
    private MockMvc restTaskNodeMockMvc;

    private TaskNode taskNode;

    private TaskNode insertedTaskNode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskNode createEntity() {
        return new TaskNode()
            .taskName(DEFAULT_TASK_NAME)
            .taskLog(DEFAULT_TASK_LOG)
            .scheduledStartTime(DEFAULT_SCHEDULED_START_TIME)
            .startDelayMinutes(DEFAULT_START_DELAY_MINUTES)
            .startDelayHours(DEFAULT_START_DELAY_HOURS)
            .elapsedHours(DEFAULT_ELAPSED_HOURS)
            .elapsedMinutes(DEFAULT_ELAPSED_MINUTES)
            .progressPercentage(DEFAULT_PROGRESS_PERCENTAGE)
            .downloadFilesize(DEFAULT_DOWNLOAD_FILESIZE)
            .downloadSpeed(DEFAULT_DOWNLOAD_SPEED)
            .downloadEta(DEFAULT_DOWNLOAD_ETA)
            .nodeIndex(DEFAULT_NODE_INDEX)
            .status(DEFAULT_STATUS)
            .type(DEFAULT_TYPE)
            .retryCount(DEFAULT_RETRY_COUNT)
            .maxRetryCount(DEFAULT_MAX_RETRY_COUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskNode createUpdatedEntity() {
        return new TaskNode()
            .referenceType(UPDATED_REFERENCE_TYPE)
            .taskName(UPDATED_TASK_NAME)
            .taskLog(UPDATED_TASK_LOG)
            .scheduledStartTime(UPDATED_SCHEDULED_START_TIME)
            .startDelayMinutes(UPDATED_START_DELAY_MINUTES)
            .startDelayHours(UPDATED_START_DELAY_HOURS)
            .elapsedHours(UPDATED_ELAPSED_HOURS)
            .elapsedMinutes(UPDATED_ELAPSED_MINUTES)
            .progressPercentage(UPDATED_PROGRESS_PERCENTAGE)
            .downloadFilesize(UPDATED_DOWNLOAD_FILESIZE)
            .downloadSpeed(UPDATED_DOWNLOAD_SPEED)
            .downloadEta(UPDATED_DOWNLOAD_ETA)
            .nodeIndex(UPDATED_NODE_INDEX)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE)
            .retryCount(UPDATED_RETRY_COUNT)
            .maxRetryCount(UPDATED_MAX_RETRY_COUNT);
    }

    @BeforeEach
    public void initTest() {
        taskNode = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTaskNode != null) {
            taskNodeRepository.delete(insertedTaskNode);
            insertedTaskNode = null;
        }
    }

    @Test
    void createTaskNode() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TaskNode
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);
        var returnedTaskNodeDTO = om.readValue(
            restTaskNodeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskNodeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TaskNodeDTO.class
        );

        // Validate the TaskNode in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTaskNode = taskNodeMapper.toEntity(returnedTaskNodeDTO);
        assertTaskNodeUpdatableFieldsEquals(returnedTaskNode, getPersistedTaskNode(returnedTaskNode));

        insertedTaskNode = returnedTaskNode;
    }

    @Test
    void createTaskNodeWithExistingId() throws Exception {
        // Create the TaskNode with an existing ID
        taskNode.setId("existing_id");
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskNodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TaskNode in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskNode.setReferenceType(null);

        // Create the TaskNode, which fails.
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        restTaskNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskNodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkReferenceIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskNode.setReferenceId(null);

        // Create the TaskNode, which fails.
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        restTaskNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskNodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTaskNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskNode.setTaskName(null);

        // Create the TaskNode, which fails.
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        restTaskNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskNodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskNode.setStatus(null);

        // Create the TaskNode, which fails.
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        restTaskNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskNodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskNode.setType(null);

        // Create the TaskNode, which fails.
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        restTaskNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskNodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTaskNodes() throws Exception {
        // Initialize the database
        insertedTaskNode = taskNodeRepository.save(taskNode);

        // Get all the taskNodeList
        restTaskNodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskNode.getId())))
            .andExpect(jsonPath("$.[*].referenceId").value(hasItem(DEFAULT_REFERENCE_ID.intValue())))
            .andExpect(jsonPath("$.[*].taskName").value(hasItem(DEFAULT_TASK_NAME)))
            .andExpect(jsonPath("$.[*].taskLog").value(hasItem(DEFAULT_TASK_LOG)))
            .andExpect(jsonPath("$.[*].trackId").value(hasItem(DEFAULT_TRACK_ID.intValue())))
            .andExpect(jsonPath("$.[*].scheduledStartTime").value(hasItem(DEFAULT_SCHEDULED_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].startDelayMinutes").value(hasItem(sameNumber(DEFAULT_START_DELAY_MINUTES))))
            .andExpect(jsonPath("$.[*].startDelayHours").value(hasItem(sameNumber(DEFAULT_START_DELAY_HOURS))))
            .andExpect(jsonPath("$.[*].elapsedHours").value(hasItem(sameNumber(DEFAULT_ELAPSED_HOURS))))
            .andExpect(jsonPath("$.[*].elapsedMinutes").value(hasItem(sameNumber(DEFAULT_ELAPSED_MINUTES))))
            .andExpect(jsonPath("$.[*].progressPercentage").value(hasItem(sameNumber(DEFAULT_PROGRESS_PERCENTAGE))))
            .andExpect(jsonPath("$.[*].downloadFilesize").value(hasItem(DEFAULT_DOWNLOAD_FILESIZE)))
            .andExpect(jsonPath("$.[*].downloadSpeed").value(hasItem(DEFAULT_DOWNLOAD_SPEED)))
            .andExpect(jsonPath("$.[*].downloadEta").value(hasItem(DEFAULT_DOWNLOAD_ETA)))
            .andExpect(jsonPath("$.[*].nodeIndex").value(hasItem(sameNumber(DEFAULT_NODE_INDEX))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].failCount").value(hasItem(sameNumber(DEFAULT_FAIL_COUNT))))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(sameNumber(DEFAULT_RETRY_COUNT))))
            .andExpect(jsonPath("$.[*].maxRetryCount").value(hasItem(sameNumber(DEFAULT_MAX_RETRY_COUNT))));
    }

    @Test
    void getTaskNode() throws Exception {
        // Initialize the database
        insertedTaskNode = taskNodeRepository.save(taskNode);

        // Get the taskNode
        restTaskNodeMockMvc
            .perform(get(ENTITY_API_URL_ID, taskNode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskNode.getId()))
            .andExpect(jsonPath("$.referenceId").value(DEFAULT_REFERENCE_ID.intValue()))
            .andExpect(jsonPath("$.taskName").value(DEFAULT_TASK_NAME))
            .andExpect(jsonPath("$.taskLog").value(DEFAULT_TASK_LOG))
            .andExpect(jsonPath("$.trackId").value(DEFAULT_TRACK_ID.intValue()))
            .andExpect(jsonPath("$.scheduledStartTime").value(DEFAULT_SCHEDULED_START_TIME.toString()))
            .andExpect(jsonPath("$.startDelayMinutes").value(sameNumber(DEFAULT_START_DELAY_MINUTES)))
            .andExpect(jsonPath("$.startDelayHours").value(sameNumber(DEFAULT_START_DELAY_HOURS)))
            .andExpect(jsonPath("$.elapsedHours").value(sameNumber(DEFAULT_ELAPSED_HOURS)))
            .andExpect(jsonPath("$.elapsedMinutes").value(sameNumber(DEFAULT_ELAPSED_MINUTES)))
            .andExpect(jsonPath("$.progressPercentage").value(sameNumber(DEFAULT_PROGRESS_PERCENTAGE)))
            .andExpect(jsonPath("$.downloadFilesize").value(DEFAULT_DOWNLOAD_FILESIZE))
            .andExpect(jsonPath("$.downloadSpeed").value(DEFAULT_DOWNLOAD_SPEED))
            .andExpect(jsonPath("$.downloadEta").value(DEFAULT_DOWNLOAD_ETA))
            .andExpect(jsonPath("$.nodeIndex").value(sameNumber(DEFAULT_NODE_INDEX)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.failCount").value(sameNumber(DEFAULT_FAIL_COUNT)))
            .andExpect(jsonPath("$.retryCount").value(sameNumber(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.maxRetryCount").value(sameNumber(DEFAULT_MAX_RETRY_COUNT)));
    }

    @Test
    void getNonExistingTaskNode() throws Exception {
        // Get the taskNode
        restTaskNodeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingTaskNode() throws Exception {
        // Initialize the database
        insertedTaskNode = taskNodeRepository.save(taskNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskNode
        TaskNode updatedTaskNode = taskNodeRepository.findById(taskNode.getId()).orElseThrow();
        updatedTaskNode
            .referenceType(UPDATED_REFERENCE_TYPE)
            .taskName(UPDATED_TASK_NAME)
            .taskLog(UPDATED_TASK_LOG)
            .scheduledStartTime(UPDATED_SCHEDULED_START_TIME)
            .startDelayMinutes(UPDATED_START_DELAY_MINUTES)
            .startDelayHours(UPDATED_START_DELAY_HOURS)
            .elapsedHours(UPDATED_ELAPSED_HOURS)
            .elapsedMinutes(UPDATED_ELAPSED_MINUTES)
            .progressPercentage(UPDATED_PROGRESS_PERCENTAGE)
            .downloadFilesize(UPDATED_DOWNLOAD_FILESIZE)
            .downloadSpeed(UPDATED_DOWNLOAD_SPEED)
            .downloadEta(UPDATED_DOWNLOAD_ETA)
            .nodeIndex(UPDATED_NODE_INDEX)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE)
            .retryCount(UPDATED_RETRY_COUNT)
            .maxRetryCount(UPDATED_MAX_RETRY_COUNT);
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(updatedTaskNode);

        restTaskNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskNodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskNodeDTO))
            )
            .andExpect(status().isOk());

        // Validate the TaskNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskNodeToMatchAllProperties(updatedTaskNode);
    }

    @Test
    void putNonExistingTaskNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskNode.setId(UUID.randomUUID().toString());

        // Create the TaskNode
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskNodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTaskNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskNode.setId(UUID.randomUUID().toString());

        // Create the TaskNode
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTaskNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskNode.setId(UUID.randomUUID().toString());

        // Create the TaskNode
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskNodeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskNodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTaskNodeWithPatch() throws Exception {
        // Initialize the database
        insertedTaskNode = taskNodeRepository.save(taskNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskNode using partial update
        TaskNode partialUpdatedTaskNode = new TaskNode();
        partialUpdatedTaskNode.setId(taskNode.getId());

        partialUpdatedTaskNode
            .referenceType(UPDATED_REFERENCE_TYPE)
            .scheduledStartTime(UPDATED_SCHEDULED_START_TIME)
            .startDelayMinutes(UPDATED_START_DELAY_MINUTES)
            .startDelayHours(UPDATED_START_DELAY_HOURS)
            .elapsedMinutes(UPDATED_ELAPSED_MINUTES)
            .downloadSpeed(UPDATED_DOWNLOAD_SPEED)
            .nodeIndex(UPDATED_NODE_INDEX)
            .retryCount(UPDATED_RETRY_COUNT);

        restTaskNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskNode))
            )
            .andExpect(status().isOk());

        // Validate the TaskNode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskNodeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTaskNode, taskNode), getPersistedTaskNode(taskNode));
    }

    @Test
    void fullUpdateTaskNodeWithPatch() throws Exception {
        // Initialize the database
        insertedTaskNode = taskNodeRepository.save(taskNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskNode using partial update
        TaskNode partialUpdatedTaskNode = new TaskNode();
        partialUpdatedTaskNode.setId(taskNode.getId());

        partialUpdatedTaskNode
            .referenceType(UPDATED_REFERENCE_TYPE)
            .taskName(UPDATED_TASK_NAME)
            .taskLog(UPDATED_TASK_LOG)
            .scheduledStartTime(UPDATED_SCHEDULED_START_TIME)
            .startDelayMinutes(UPDATED_START_DELAY_MINUTES)
            .startDelayHours(UPDATED_START_DELAY_HOURS)
            .elapsedHours(UPDATED_ELAPSED_HOURS)
            .elapsedMinutes(UPDATED_ELAPSED_MINUTES)
            .progressPercentage(UPDATED_PROGRESS_PERCENTAGE)
            .downloadFilesize(UPDATED_DOWNLOAD_FILESIZE)
            .downloadSpeed(UPDATED_DOWNLOAD_SPEED)
            .downloadEta(UPDATED_DOWNLOAD_ETA)
            .nodeIndex(UPDATED_NODE_INDEX)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE)
            .retryCount(UPDATED_RETRY_COUNT)
            .maxRetryCount(UPDATED_MAX_RETRY_COUNT);

        restTaskNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskNode))
            )
            .andExpect(status().isOk());

        // Validate the TaskNode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskNodeUpdatableFieldsEquals(partialUpdatedTaskNode, getPersistedTaskNode(partialUpdatedTaskNode));
    }

    @Test
    void patchNonExistingTaskNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskNode.setId(UUID.randomUUID().toString());

        // Create the TaskNode
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskNodeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTaskNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskNode.setId(UUID.randomUUID().toString());

        // Create the TaskNode
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTaskNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskNode.setId(UUID.randomUUID().toString());

        // Create the TaskNode
        TaskNodeDTO taskNodeDTO = taskNodeMapper.toDto(taskNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskNodeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(taskNodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTaskNode() throws Exception {
        // Initialize the database
        insertedTaskNode = taskNodeRepository.save(taskNode);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taskNode
        restTaskNodeMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskNode.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskNodeRepository.count();
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

    protected TaskNode getPersistedTaskNode(TaskNode taskNode) {
        return taskNodeRepository.findById(taskNode.getId()).orElseThrow();
    }

    protected void assertPersistedTaskNodeToMatchAllProperties(TaskNode expectedTaskNode) {
        assertTaskNodeAllPropertiesEquals(expectedTaskNode, getPersistedTaskNode(expectedTaskNode));
    }

    protected void assertPersistedTaskNodeToMatchUpdatableProperties(TaskNode expectedTaskNode) {
        assertTaskNodeAllUpdatablePropertiesEquals(expectedTaskNode, getPersistedTaskNode(expectedTaskNode));
    }
}
