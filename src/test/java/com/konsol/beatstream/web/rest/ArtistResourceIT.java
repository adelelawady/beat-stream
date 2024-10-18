package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.domain.ArtistAsserts.*;
import static com.konsol.beatstream.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konsol.beatstream.IntegrationTest;
import com.konsol.beatstream.domain.Artist;
import com.konsol.beatstream.repository.ArtistRepository;
import com.konsol.beatstream.service.dto.ArtistDTO;
import com.konsol.beatstream.service.mapper.ArtistMapper;
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
 * Integration tests for the {@link ArtistResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ArtistResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_FILE_ID = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_FILE_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/artists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private MockMvc restArtistMockMvc;

    private Artist artist;

    private Artist insertedArtist;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artist createEntity() {
        return new Artist().name(DEFAULT_NAME).bio(DEFAULT_BIO).coverImageFileId(DEFAULT_COVER_IMAGE_FILE_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artist createUpdatedEntity() {
        return new Artist().name(UPDATED_NAME).bio(UPDATED_BIO).coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);
    }

    @BeforeEach
    public void initTest() {
        artist = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedArtist != null) {
            artistRepository.delete(insertedArtist);
            insertedArtist = null;
        }
    }

    @Test
    void createArtist() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Artist
        ArtistDTO artistDTO = artistMapper.toDto(artist);
        var returnedArtistDTO = om.readValue(
            restArtistMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artistDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ArtistDTO.class
        );

        // Validate the Artist in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedArtist = artistMapper.toEntity(returnedArtistDTO);
        assertArtistUpdatableFieldsEquals(returnedArtist, getPersistedArtist(returnedArtist));

        insertedArtist = returnedArtist;
    }

    @Test
    void createArtistWithExistingId() throws Exception {
        // Create the Artist with an existing ID
        artist.setId("existing_id");
        ArtistDTO artistDTO = artistMapper.toDto(artist);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restArtistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Artist in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        artist.setName(null);

        // Create the Artist, which fails.
        ArtistDTO artistDTO = artistMapper.toDto(artist);

        restArtistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artistDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllArtists() throws Exception {
        // Initialize the database
        insertedArtist = artistRepository.save(artist);

        // Get all the artistList
        restArtistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artist.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].coverImageFileId").value(hasItem(DEFAULT_COVER_IMAGE_FILE_ID)));
    }

    @Test
    void getArtist() throws Exception {
        // Initialize the database
        insertedArtist = artistRepository.save(artist);

        // Get the artist
        restArtistMockMvc
            .perform(get(ENTITY_API_URL_ID, artist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(artist.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.bio").value(DEFAULT_BIO))
            .andExpect(jsonPath("$.coverImageFileId").value(DEFAULT_COVER_IMAGE_FILE_ID));
    }

    @Test
    void getNonExistingArtist() throws Exception {
        // Get the artist
        restArtistMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingArtist() throws Exception {
        // Initialize the database
        insertedArtist = artistRepository.save(artist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the artist
        Artist updatedArtist = artistRepository.findById(artist.getId()).orElseThrow();
        updatedArtist.name(UPDATED_NAME).bio(UPDATED_BIO).coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);
        ArtistDTO artistDTO = artistMapper.toDto(updatedArtist);

        restArtistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, artistDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artistDTO))
            )
            .andExpect(status().isOk());

        // Validate the Artist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedArtistToMatchAllProperties(updatedArtist);
    }

    @Test
    void putNonExistingArtist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artist.setId(UUID.randomUUID().toString());

        // Create the Artist
        ArtistDTO artistDTO = artistMapper.toDto(artist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, artistDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchArtist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artist.setId(UUID.randomUUID().toString());

        // Create the Artist
        ArtistDTO artistDTO = artistMapper.toDto(artist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(artistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamArtist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artist.setId(UUID.randomUUID().toString());

        // Create the Artist
        ArtistDTO artistDTO = artistMapper.toDto(artist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtistMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(artistDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Artist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateArtistWithPatch() throws Exception {
        // Initialize the database
        insertedArtist = artistRepository.save(artist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the artist using partial update
        Artist partialUpdatedArtist = new Artist();
        partialUpdatedArtist.setId(artist.getId());

        partialUpdatedArtist.coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);

        restArtistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArtist.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArtist))
            )
            .andExpect(status().isOk());

        // Validate the Artist in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArtistUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedArtist, artist), getPersistedArtist(artist));
    }

    @Test
    void fullUpdateArtistWithPatch() throws Exception {
        // Initialize the database
        insertedArtist = artistRepository.save(artist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the artist using partial update
        Artist partialUpdatedArtist = new Artist();
        partialUpdatedArtist.setId(artist.getId());

        partialUpdatedArtist.name(UPDATED_NAME).bio(UPDATED_BIO).coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);

        restArtistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArtist.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArtist))
            )
            .andExpect(status().isOk());

        // Validate the Artist in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArtistUpdatableFieldsEquals(partialUpdatedArtist, getPersistedArtist(partialUpdatedArtist));
    }

    @Test
    void patchNonExistingArtist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artist.setId(UUID.randomUUID().toString());

        // Create the Artist
        ArtistDTO artistDTO = artistMapper.toDto(artist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, artistDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(artistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchArtist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artist.setId(UUID.randomUUID().toString());

        // Create the Artist
        ArtistDTO artistDTO = artistMapper.toDto(artist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(artistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamArtist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        artist.setId(UUID.randomUUID().toString());

        // Create the Artist
        ArtistDTO artistDTO = artistMapper.toDto(artist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtistMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(artistDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Artist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteArtist() throws Exception {
        // Initialize the database
        insertedArtist = artistRepository.save(artist);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the artist
        restArtistMockMvc
            .perform(delete(ENTITY_API_URL_ID, artist.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return artistRepository.count();
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

    protected Artist getPersistedArtist(Artist artist) {
        return artistRepository.findById(artist.getId()).orElseThrow();
    }

    protected void assertPersistedArtistToMatchAllProperties(Artist expectedArtist) {
        assertArtistAllPropertiesEquals(expectedArtist, getPersistedArtist(expectedArtist));
    }

    protected void assertPersistedArtistToMatchUpdatableProperties(Artist expectedArtist) {
        assertArtistAllUpdatablePropertiesEquals(expectedArtist, getPersistedArtist(expectedArtist));
    }
}
