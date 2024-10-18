package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.domain.AlbumAsserts.*;
import static com.konsol.beatstream.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konsol.beatstream.IntegrationTest;
import com.konsol.beatstream.domain.Album;
import com.konsol.beatstream.repository.AlbumRepository;
import com.konsol.beatstream.service.AlbumService;
import com.konsol.beatstream.service.dto.AlbumDTO;
import com.konsol.beatstream.service.mapper.AlbumMapper;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link AlbumResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AlbumResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_RELEASE_DATE = "AAAAAAAAAA";
    private static final String UPDATED_RELEASE_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_FILE_ID = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_FILE_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/albums";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlbumRepository albumRepository;

    @Mock
    private AlbumRepository albumRepositoryMock;

    @Autowired
    private AlbumMapper albumMapper;

    @Mock
    private AlbumService albumServiceMock;

    @Autowired
    private MockMvc restAlbumMockMvc;

    private Album album;

    private Album insertedAlbum;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Album createEntity() {
        return new Album().title(DEFAULT_TITLE).releaseDate(DEFAULT_RELEASE_DATE).coverImageFileId(DEFAULT_COVER_IMAGE_FILE_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Album createUpdatedEntity() {
        return new Album().title(UPDATED_TITLE).releaseDate(UPDATED_RELEASE_DATE).coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);
    }

    @BeforeEach
    public void initTest() {
        album = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAlbum != null) {
            albumRepository.delete(insertedAlbum);
            insertedAlbum = null;
        }
    }

    @Test
    void createAlbum() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Album
        AlbumDTO albumDTO = albumMapper.toDto(album);
        var returnedAlbumDTO = om.readValue(
            restAlbumMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(albumDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AlbumDTO.class
        );

        // Validate the Album in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAlbum = albumMapper.toEntity(returnedAlbumDTO);
        assertAlbumUpdatableFieldsEquals(returnedAlbum, getPersistedAlbum(returnedAlbum));

        insertedAlbum = returnedAlbum;
    }

    @Test
    void createAlbumWithExistingId() throws Exception {
        // Create the Album with an existing ID
        album.setId("existing_id");
        AlbumDTO albumDTO = albumMapper.toDto(album);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlbumMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(albumDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        album.setTitle(null);

        // Create the Album, which fails.
        AlbumDTO albumDTO = albumMapper.toDto(album);

        restAlbumMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(albumDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAlbums() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.save(album);

        // Get all the albumList
        restAlbumMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(album.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(DEFAULT_RELEASE_DATE)))
            .andExpect(jsonPath("$.[*].coverImageFileId").value(hasItem(DEFAULT_COVER_IMAGE_FILE_ID)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlbumsWithEagerRelationshipsIsEnabled() throws Exception {
        when(albumServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAlbumMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(albumServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlbumsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(albumServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAlbumMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(albumRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAlbum() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.save(album);

        // Get the album
        restAlbumMockMvc
            .perform(get(ENTITY_API_URL_ID, album.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(album.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.releaseDate").value(DEFAULT_RELEASE_DATE))
            .andExpect(jsonPath("$.coverImageFileId").value(DEFAULT_COVER_IMAGE_FILE_ID));
    }

    @Test
    void getNonExistingAlbum() throws Exception {
        // Get the album
        restAlbumMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingAlbum() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.save(album);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the album
        Album updatedAlbum = albumRepository.findById(album.getId()).orElseThrow();
        updatedAlbum.title(UPDATED_TITLE).releaseDate(UPDATED_RELEASE_DATE).coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);
        AlbumDTO albumDTO = albumMapper.toDto(updatedAlbum);

        restAlbumMockMvc
            .perform(
                put(ENTITY_API_URL_ID, albumDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(albumDTO))
            )
            .andExpect(status().isOk());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlbumToMatchAllProperties(updatedAlbum);
    }

    @Test
    void putNonExistingAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(UUID.randomUUID().toString());

        // Create the Album
        AlbumDTO albumDTO = albumMapper.toDto(album);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(
                put(ENTITY_API_URL_ID, albumDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(albumDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(UUID.randomUUID().toString());

        // Create the Album
        AlbumDTO albumDTO = albumMapper.toDto(album);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(albumDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(UUID.randomUUID().toString());

        // Create the Album
        AlbumDTO albumDTO = albumMapper.toDto(album);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(albumDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAlbumWithPatch() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.save(album);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the album using partial update
        Album partialUpdatedAlbum = new Album();
        partialUpdatedAlbum.setId(album.getId());

        partialUpdatedAlbum.title(UPDATED_TITLE).releaseDate(UPDATED_RELEASE_DATE).coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);

        restAlbumMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlbum.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlbum))
            )
            .andExpect(status().isOk());

        // Validate the Album in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlbumUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAlbum, album), getPersistedAlbum(album));
    }

    @Test
    void fullUpdateAlbumWithPatch() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.save(album);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the album using partial update
        Album partialUpdatedAlbum = new Album();
        partialUpdatedAlbum.setId(album.getId());

        partialUpdatedAlbum.title(UPDATED_TITLE).releaseDate(UPDATED_RELEASE_DATE).coverImageFileId(UPDATED_COVER_IMAGE_FILE_ID);

        restAlbumMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlbum.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlbum))
            )
            .andExpect(status().isOk());

        // Validate the Album in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlbumUpdatableFieldsEquals(partialUpdatedAlbum, getPersistedAlbum(partialUpdatedAlbum));
    }

    @Test
    void patchNonExistingAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(UUID.randomUUID().toString());

        // Create the Album
        AlbumDTO albumDTO = albumMapper.toDto(album);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, albumDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(albumDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(UUID.randomUUID().toString());

        // Create the Album
        AlbumDTO albumDTO = albumMapper.toDto(album);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(albumDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(UUID.randomUUID().toString());

        // Create the Album
        AlbumDTO albumDTO = albumMapper.toDto(album);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(albumDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAlbum() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.save(album);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the album
        restAlbumMockMvc
            .perform(delete(ENTITY_API_URL_ID, album.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return albumRepository.count();
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

    protected Album getPersistedAlbum(Album album) {
        return albumRepository.findById(album.getId()).orElseThrow();
    }

    protected void assertPersistedAlbumToMatchAllProperties(Album expectedAlbum) {
        assertAlbumAllPropertiesEquals(expectedAlbum, getPersistedAlbum(expectedAlbum));
    }

    protected void assertPersistedAlbumToMatchUpdatableProperties(Album expectedAlbum) {
        assertAlbumAllUpdatablePropertiesEquals(expectedAlbum, getPersistedAlbum(expectedAlbum));
    }
}
