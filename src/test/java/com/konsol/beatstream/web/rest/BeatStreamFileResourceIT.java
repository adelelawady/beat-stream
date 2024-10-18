package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.domain.BeatStreamFileAsserts.*;
import static com.konsol.beatstream.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konsol.beatstream.IntegrationTest;
import com.konsol.beatstream.domain.BeatStreamFile;
import com.konsol.beatstream.repository.BeatStreamFileRepository;
import com.konsol.beatstream.service.dto.BeatStreamFileDTO;
import com.konsol.beatstream.service.mapper.BeatStreamFileMapper;
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
 * Integration tests for the {@link BeatStreamFileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BeatStreamFileResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_SIZE = 1L;
    private static final Long UPDATED_SIZE = 2L;

    private static final String DEFAULT_BUCKET = "AAAAAAAAAA";
    private static final String UPDATED_BUCKET = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/beat-stream-files";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BeatStreamFileRepository beatStreamFileRepository;

    @Autowired
    private BeatStreamFileMapper beatStreamFileMapper;

    @Autowired
    private MockMvc restBeatStreamFileMockMvc;

    private BeatStreamFile beatStreamFile;

    private BeatStreamFile insertedBeatStreamFile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BeatStreamFile createEntity() {
        return new BeatStreamFile().name(DEFAULT_NAME).size(DEFAULT_SIZE).bucket(DEFAULT_BUCKET).type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BeatStreamFile createUpdatedEntity() {
        return new BeatStreamFile().name(UPDATED_NAME).size(UPDATED_SIZE).bucket(UPDATED_BUCKET).type(UPDATED_TYPE);
    }

    @BeforeEach
    public void initTest() {
        beatStreamFile = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedBeatStreamFile != null) {
            beatStreamFileRepository.delete(insertedBeatStreamFile);
            insertedBeatStreamFile = null;
        }
    }

    @Test
    void createBeatStreamFile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BeatStreamFile
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(beatStreamFile);
        var returnedBeatStreamFileDTO = om.readValue(
            restBeatStreamFileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beatStreamFileDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BeatStreamFileDTO.class
        );

        // Validate the BeatStreamFile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBeatStreamFile = beatStreamFileMapper.toEntity(returnedBeatStreamFileDTO);
        assertBeatStreamFileUpdatableFieldsEquals(returnedBeatStreamFile, getPersistedBeatStreamFile(returnedBeatStreamFile));

        insertedBeatStreamFile = returnedBeatStreamFile;
    }

    @Test
    void createBeatStreamFileWithExistingId() throws Exception {
        // Create the BeatStreamFile with an existing ID
        beatStreamFile.setId("existing_id");
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(beatStreamFile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBeatStreamFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beatStreamFileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BeatStreamFile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        beatStreamFile.setName(null);

        // Create the BeatStreamFile, which fails.
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(beatStreamFile);

        restBeatStreamFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beatStreamFileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllBeatStreamFiles() throws Exception {
        // Initialize the database
        insertedBeatStreamFile = beatStreamFileRepository.save(beatStreamFile);

        // Get all the beatStreamFileList
        restBeatStreamFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(beatStreamFile.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].bucket").value(hasItem(DEFAULT_BUCKET)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @Test
    void getBeatStreamFile() throws Exception {
        // Initialize the database
        insertedBeatStreamFile = beatStreamFileRepository.save(beatStreamFile);

        // Get the beatStreamFile
        restBeatStreamFileMockMvc
            .perform(get(ENTITY_API_URL_ID, beatStreamFile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(beatStreamFile.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.intValue()))
            .andExpect(jsonPath("$.bucket").value(DEFAULT_BUCKET))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    void getNonExistingBeatStreamFile() throws Exception {
        // Get the beatStreamFile
        restBeatStreamFileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingBeatStreamFile() throws Exception {
        // Initialize the database
        insertedBeatStreamFile = beatStreamFileRepository.save(beatStreamFile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the beatStreamFile
        BeatStreamFile updatedBeatStreamFile = beatStreamFileRepository.findById(beatStreamFile.getId()).orElseThrow();
        updatedBeatStreamFile.name(UPDATED_NAME).size(UPDATED_SIZE).bucket(UPDATED_BUCKET).type(UPDATED_TYPE);
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(updatedBeatStreamFile);

        restBeatStreamFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, beatStreamFileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(beatStreamFileDTO))
            )
            .andExpect(status().isOk());

        // Validate the BeatStreamFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBeatStreamFileToMatchAllProperties(updatedBeatStreamFile);
    }

    @Test
    void putNonExistingBeatStreamFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beatStreamFile.setId(UUID.randomUUID().toString());

        // Create the BeatStreamFile
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(beatStreamFile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBeatStreamFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, beatStreamFileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(beatStreamFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BeatStreamFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBeatStreamFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beatStreamFile.setId(UUID.randomUUID().toString());

        // Create the BeatStreamFile
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(beatStreamFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBeatStreamFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(beatStreamFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BeatStreamFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBeatStreamFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beatStreamFile.setId(UUID.randomUUID().toString());

        // Create the BeatStreamFile
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(beatStreamFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBeatStreamFileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beatStreamFileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BeatStreamFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBeatStreamFileWithPatch() throws Exception {
        // Initialize the database
        insertedBeatStreamFile = beatStreamFileRepository.save(beatStreamFile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the beatStreamFile using partial update
        BeatStreamFile partialUpdatedBeatStreamFile = new BeatStreamFile();
        partialUpdatedBeatStreamFile.setId(beatStreamFile.getId());

        partialUpdatedBeatStreamFile.type(UPDATED_TYPE);

        restBeatStreamFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBeatStreamFile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBeatStreamFile))
            )
            .andExpect(status().isOk());

        // Validate the BeatStreamFile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBeatStreamFileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBeatStreamFile, beatStreamFile),
            getPersistedBeatStreamFile(beatStreamFile)
        );
    }

    @Test
    void fullUpdateBeatStreamFileWithPatch() throws Exception {
        // Initialize the database
        insertedBeatStreamFile = beatStreamFileRepository.save(beatStreamFile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the beatStreamFile using partial update
        BeatStreamFile partialUpdatedBeatStreamFile = new BeatStreamFile();
        partialUpdatedBeatStreamFile.setId(beatStreamFile.getId());

        partialUpdatedBeatStreamFile.name(UPDATED_NAME).size(UPDATED_SIZE).bucket(UPDATED_BUCKET).type(UPDATED_TYPE);

        restBeatStreamFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBeatStreamFile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBeatStreamFile))
            )
            .andExpect(status().isOk());

        // Validate the BeatStreamFile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBeatStreamFileUpdatableFieldsEquals(partialUpdatedBeatStreamFile, getPersistedBeatStreamFile(partialUpdatedBeatStreamFile));
    }

    @Test
    void patchNonExistingBeatStreamFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beatStreamFile.setId(UUID.randomUUID().toString());

        // Create the BeatStreamFile
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(beatStreamFile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBeatStreamFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, beatStreamFileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(beatStreamFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BeatStreamFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBeatStreamFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beatStreamFile.setId(UUID.randomUUID().toString());

        // Create the BeatStreamFile
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(beatStreamFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBeatStreamFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(beatStreamFileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BeatStreamFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBeatStreamFile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beatStreamFile.setId(UUID.randomUUID().toString());

        // Create the BeatStreamFile
        BeatStreamFileDTO beatStreamFileDTO = beatStreamFileMapper.toDto(beatStreamFile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBeatStreamFileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(beatStreamFileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BeatStreamFile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBeatStreamFile() throws Exception {
        // Initialize the database
        insertedBeatStreamFile = beatStreamFileRepository.save(beatStreamFile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the beatStreamFile
        restBeatStreamFileMockMvc
            .perform(delete(ENTITY_API_URL_ID, beatStreamFile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return beatStreamFileRepository.count();
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

    protected BeatStreamFile getPersistedBeatStreamFile(BeatStreamFile beatStreamFile) {
        return beatStreamFileRepository.findById(beatStreamFile.getId()).orElseThrow();
    }

    protected void assertPersistedBeatStreamFileToMatchAllProperties(BeatStreamFile expectedBeatStreamFile) {
        assertBeatStreamFileAllPropertiesEquals(expectedBeatStreamFile, getPersistedBeatStreamFile(expectedBeatStreamFile));
    }

    protected void assertPersistedBeatStreamFileToMatchUpdatableProperties(BeatStreamFile expectedBeatStreamFile) {
        assertBeatStreamFileAllUpdatablePropertiesEquals(expectedBeatStreamFile, getPersistedBeatStreamFile(expectedBeatStreamFile));
    }
}
