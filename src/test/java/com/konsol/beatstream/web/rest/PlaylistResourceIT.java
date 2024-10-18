package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.domain.PlaylistAsserts.*;
import static com.konsol.beatstream.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konsol.beatstream.IntegrationTest;
import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.repository.PlaylistRepository;
import com.konsol.beatstream.service.PlaylistService;
import com.konsol.beatstream.service.dto.PlaylistDTO;
import com.konsol.beatstream.service.mapper.PlaylistMapper;
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
 * Integration tests for the {@link PlaylistResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PlaylistResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/playlists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Mock
    private PlaylistRepository playlistRepositoryMock;

    @Autowired
    private PlaylistMapper playlistMapper;

    @Mock
    private PlaylistService playlistServiceMock;

    @Autowired
    private MockMvc restPlaylistMockMvc;

    private Playlist playlist;

    private Playlist insertedPlaylist;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Playlist createEntity() {
        return new Playlist().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Playlist createUpdatedEntity() {
        return new Playlist().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        playlist = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPlaylist != null) {
            playlistRepository.delete(insertedPlaylist);
            insertedPlaylist = null;
        }
    }

    @Test
    void createPlaylist() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Playlist
        PlaylistDTO playlistDTO = playlistMapper.toDto(playlist);
        var returnedPlaylistDTO = om.readValue(
            restPlaylistMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playlistDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PlaylistDTO.class
        );

        // Validate the Playlist in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlaylist = playlistMapper.toEntity(returnedPlaylistDTO);
        assertPlaylistUpdatableFieldsEquals(returnedPlaylist, getPersistedPlaylist(returnedPlaylist));

        insertedPlaylist = returnedPlaylist;
    }

    @Test
    void createPlaylistWithExistingId() throws Exception {
        // Create the Playlist with an existing ID
        playlist.setId("existing_id");
        PlaylistDTO playlistDTO = playlistMapper.toDto(playlist);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaylistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playlistDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Playlist in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        playlist.setTitle(null);

        // Create the Playlist, which fails.
        PlaylistDTO playlistDTO = playlistMapper.toDto(playlist);

        restPlaylistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playlistDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPlaylists() throws Exception {
        // Initialize the database
        insertedPlaylist = playlistRepository.save(playlist);

        // Get all the playlistList
        restPlaylistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(playlist.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlaylistsWithEagerRelationshipsIsEnabled() throws Exception {
        when(playlistServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlaylistMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(playlistServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlaylistsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(playlistServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlaylistMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(playlistRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getPlaylist() throws Exception {
        // Initialize the database
        insertedPlaylist = playlistRepository.save(playlist);

        // Get the playlist
        restPlaylistMockMvc
            .perform(get(ENTITY_API_URL_ID, playlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(playlist.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingPlaylist() throws Exception {
        // Get the playlist
        restPlaylistMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPlaylist() throws Exception {
        // Initialize the database
        insertedPlaylist = playlistRepository.save(playlist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the playlist
        Playlist updatedPlaylist = playlistRepository.findById(playlist.getId()).orElseThrow();
        updatedPlaylist.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        PlaylistDTO playlistDTO = playlistMapper.toDto(updatedPlaylist);

        restPlaylistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playlistDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(playlistDTO))
            )
            .andExpect(status().isOk());

        // Validate the Playlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlaylistToMatchAllProperties(updatedPlaylist);
    }

    @Test
    void putNonExistingPlaylist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        playlist.setId(UUID.randomUUID().toString());

        // Create the Playlist
        PlaylistDTO playlistDTO = playlistMapper.toDto(playlist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaylistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playlistDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(playlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Playlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPlaylist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        playlist.setId(UUID.randomUUID().toString());

        // Create the Playlist
        PlaylistDTO playlistDTO = playlistMapper.toDto(playlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaylistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(playlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Playlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPlaylist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        playlist.setId(UUID.randomUUID().toString());

        // Create the Playlist
        PlaylistDTO playlistDTO = playlistMapper.toDto(playlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaylistMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(playlistDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Playlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePlaylistWithPatch() throws Exception {
        // Initialize the database
        insertedPlaylist = playlistRepository.save(playlist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the playlist using partial update
        Playlist partialUpdatedPlaylist = new Playlist();
        partialUpdatedPlaylist.setId(playlist.getId());

        partialUpdatedPlaylist.title(UPDATED_TITLE);

        restPlaylistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlaylist.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlaylist))
            )
            .andExpect(status().isOk());

        // Validate the Playlist in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlaylistUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPlaylist, playlist), getPersistedPlaylist(playlist));
    }

    @Test
    void fullUpdatePlaylistWithPatch() throws Exception {
        // Initialize the database
        insertedPlaylist = playlistRepository.save(playlist);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the playlist using partial update
        Playlist partialUpdatedPlaylist = new Playlist();
        partialUpdatedPlaylist.setId(playlist.getId());

        partialUpdatedPlaylist.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restPlaylistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlaylist.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlaylist))
            )
            .andExpect(status().isOk());

        // Validate the Playlist in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlaylistUpdatableFieldsEquals(partialUpdatedPlaylist, getPersistedPlaylist(partialUpdatedPlaylist));
    }

    @Test
    void patchNonExistingPlaylist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        playlist.setId(UUID.randomUUID().toString());

        // Create the Playlist
        PlaylistDTO playlistDTO = playlistMapper.toDto(playlist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaylistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, playlistDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(playlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Playlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPlaylist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        playlist.setId(UUID.randomUUID().toString());

        // Create the Playlist
        PlaylistDTO playlistDTO = playlistMapper.toDto(playlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaylistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(playlistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Playlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPlaylist() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        playlist.setId(UUID.randomUUID().toString());

        // Create the Playlist
        PlaylistDTO playlistDTO = playlistMapper.toDto(playlist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaylistMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(playlistDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Playlist in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePlaylist() throws Exception {
        // Initialize the database
        insertedPlaylist = playlistRepository.save(playlist);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the playlist
        restPlaylistMockMvc
            .perform(delete(ENTITY_API_URL_ID, playlist.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return playlistRepository.count();
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

    protected Playlist getPersistedPlaylist(Playlist playlist) {
        return playlistRepository.findById(playlist.getId()).orElseThrow();
    }

    protected void assertPersistedPlaylistToMatchAllProperties(Playlist expectedPlaylist) {
        assertPlaylistAllPropertiesEquals(expectedPlaylist, getPersistedPlaylist(expectedPlaylist));
    }

    protected void assertPersistedPlaylistToMatchUpdatableProperties(Playlist expectedPlaylist) {
        assertPlaylistAllUpdatablePropertiesEquals(expectedPlaylist, getPersistedPlaylist(expectedPlaylist));
    }
}
