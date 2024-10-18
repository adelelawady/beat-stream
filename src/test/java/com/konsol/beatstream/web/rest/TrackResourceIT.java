package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.domain.TrackAsserts.*;
import static com.konsol.beatstream.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konsol.beatstream.IntegrationTest;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.dto.TrackDTO;
import com.konsol.beatstream.service.mapper.TrackMapper;
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
 * Integration tests for the {@link TrackResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TrackResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Long DEFAULT_DURATION = 1L;
    private static final Long UPDATED_DURATION = 2L;

    private static final Boolean DEFAULT_LIKED = false;
    private static final Boolean UPDATED_LIKED = true;

    private static final String DEFAULT_AUDIO_FILE_ID = "AAAAAAAAAA";
    private static final String UPDATED_AUDIO_FILE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_FILE_ID = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_FILE_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tracks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private TrackMapper trackMapper;

    @Autowired
    private MockMvc restTrackMockMvc;

    private Track track;

    private Track insertedTrack;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Track createEntity() {
        return new Track()
            .title(DEFAULT_TITLE)
            .duration(DEFAULT_DURATION)
            .liked(DEFAULT_LIKED)
            .audioFileId(DEFAULT_AUDIO_FILE_ID)
            .coverImageFileId(DEFAULT_COVER_IMAGE_FILE_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Track createUpdatedEntity() {
        return new Track()
            .title(UPDATED_TITLE)
            .duration(UPDATED_DURATION)
            .liked(UPDATED_LIKED)
            .audioFileId(UPDATED_AUDIO_FILE_ID)
            .coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);
    }

    @BeforeEach
    public void initTest() {
        track = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTrack != null) {
            trackRepository.delete(insertedTrack);
            insertedTrack = null;
        }
    }

    @Test
    void createTrack() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Track
        TrackDTO trackDTO = trackMapper.toDto(track);
        var returnedTrackDTO = om.readValue(
            restTrackMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TrackDTO.class
        );

        // Validate the Track in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrack = trackMapper.toEntity(returnedTrackDTO);
        assertTrackUpdatableFieldsEquals(returnedTrack, getPersistedTrack(returnedTrack));

        insertedTrack = returnedTrack;
    }

    @Test
    void createTrackWithExistingId() throws Exception {
        // Create the Track with an existing ID
        track.setId("existing_id");
        TrackDTO trackDTO = trackMapper.toDto(track);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTrackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Track in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        track.setTitle(null);

        // Create the Track, which fails.
        TrackDTO trackDTO = trackMapper.toDto(track);

        restTrackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTracks() throws Exception {
        // Initialize the database
        insertedTrack = trackRepository.save(track);

        // Get all the trackList
        restTrackMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(track.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.intValue())))
            .andExpect(jsonPath("$.[*].liked").value(hasItem(DEFAULT_LIKED.booleanValue())))
            .andExpect(jsonPath("$.[*].audioFileId").value(hasItem(DEFAULT_AUDIO_FILE_ID)))
            .andExpect(jsonPath("$.[*].coverImageFileId").value(hasItem(DEFAULT_COVER_IMAGE_FILE_ID)));
    }

    @Test
    void getTrack() throws Exception {
        // Initialize the database
        insertedTrack = trackRepository.save(track);

        // Get the track
        restTrackMockMvc
            .perform(get(ENTITY_API_URL_ID, track.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(track.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.intValue()))
            .andExpect(jsonPath("$.liked").value(DEFAULT_LIKED.booleanValue()))
            .andExpect(jsonPath("$.audioFileId").value(DEFAULT_AUDIO_FILE_ID))
            .andExpect(jsonPath("$.coverImageFileId").value(DEFAULT_COVER_IMAGE_FILE_ID));
    }

    @Test
    void getNonExistingTrack() throws Exception {
        // Get the track
        restTrackMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingTrack() throws Exception {
        // Initialize the database
        insertedTrack = trackRepository.save(track);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the track
        Track updatedTrack = trackRepository.findById(track.getId()).orElseThrow();
        updatedTrack
            .title(UPDATED_TITLE)
            .duration(UPDATED_DURATION)
            .liked(UPDATED_LIKED)
            .audioFileId(UPDATED_AUDIO_FILE_ID)
            .coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);
        TrackDTO trackDTO = trackMapper.toDto(updatedTrack);

        restTrackMockMvc
            .perform(
                put(ENTITY_API_URL_ID, trackDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackDTO))
            )
            .andExpect(status().isOk());

        // Validate the Track in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrackToMatchAllProperties(updatedTrack);
    }

    @Test
    void putNonExistingTrack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        track.setId(UUID.randomUUID().toString());

        // Create the Track
        TrackDTO trackDTO = trackMapper.toDto(track);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTrackMockMvc
            .perform(
                put(ENTITY_API_URL_ID, trackDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Track in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTrack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        track.setId(UUID.randomUUID().toString());

        // Create the Track
        TrackDTO trackDTO = trackMapper.toDto(track);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrackMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(trackDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Track in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTrack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        track.setId(UUID.randomUUID().toString());

        // Create the Track
        TrackDTO trackDTO = trackMapper.toDto(track);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrackMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trackDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Track in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTrackWithPatch() throws Exception {
        // Initialize the database
        insertedTrack = trackRepository.save(track);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the track using partial update
        Track partialUpdatedTrack = new Track();
        partialUpdatedTrack.setId(track.getId());

        partialUpdatedTrack
            .title(UPDATED_TITLE)
            .duration(UPDATED_DURATION)
            .audioFileId(UPDATED_AUDIO_FILE_ID)
            .coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);

        restTrackMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrack.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrack))
            )
            .andExpect(status().isOk());

        // Validate the Track in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrackUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTrack, track), getPersistedTrack(track));
    }

    @Test
    void fullUpdateTrackWithPatch() throws Exception {
        // Initialize the database
        insertedTrack = trackRepository.save(track);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the track using partial update
        Track partialUpdatedTrack = new Track();
        partialUpdatedTrack.setId(track.getId());

        partialUpdatedTrack
            .title(UPDATED_TITLE)
            .duration(UPDATED_DURATION)
            .liked(UPDATED_LIKED)
            .audioFileId(UPDATED_AUDIO_FILE_ID)
            .coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);

        restTrackMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrack.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrack))
            )
            .andExpect(status().isOk());

        // Validate the Track in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrackUpdatableFieldsEquals(partialUpdatedTrack, getPersistedTrack(partialUpdatedTrack));
    }

    @Test
    void patchNonExistingTrack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        track.setId(UUID.randomUUID().toString());

        // Create the Track
        TrackDTO trackDTO = trackMapper.toDto(track);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTrackMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, trackDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(trackDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Track in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTrack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        track.setId(UUID.randomUUID().toString());

        // Create the Track
        TrackDTO trackDTO = trackMapper.toDto(track);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrackMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(trackDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Track in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTrack() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        track.setId(UUID.randomUUID().toString());

        // Create the Track
        TrackDTO trackDTO = trackMapper.toDto(track);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrackMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(trackDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Track in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTrack() throws Exception {
        // Initialize the database
        insertedTrack = trackRepository.save(track);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the track
        restTrackMockMvc
            .perform(delete(ENTITY_API_URL_ID, track.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return trackRepository.count();
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

    protected Track getPersistedTrack(Track track) {
        return trackRepository.findById(track.getId()).orElseThrow();
    }

    protected void assertPersistedTrackToMatchAllProperties(Track expectedTrack) {
        assertTrackAllPropertiesEquals(expectedTrack, getPersistedTrack(expectedTrack));
    }

    protected void assertPersistedTrackToMatchUpdatableProperties(Track expectedTrack) {
        assertTrackAllUpdatablePropertiesEquals(expectedTrack, getPersistedTrack(expectedTrack));
    }
}
