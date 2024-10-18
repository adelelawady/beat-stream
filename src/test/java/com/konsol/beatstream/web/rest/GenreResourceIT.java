package com.konsol.beatstream.web.rest;

import static com.konsol.beatstream.domain.GenreAsserts.*;
import static com.konsol.beatstream.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konsol.beatstream.IntegrationTest;
import com.konsol.beatstream.domain.Genre;
import com.konsol.beatstream.repository.GenreRepository;
import com.konsol.beatstream.service.GenreService;
import com.konsol.beatstream.service.dto.GenreDTO;
import com.konsol.beatstream.service.mapper.GenreMapper;
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
 * Integration tests for the {@link GenreResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GenreResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/genres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GenreRepository genreRepository;

    @Mock
    private GenreRepository genreRepositoryMock;

    @Autowired
    private GenreMapper genreMapper;

    @Mock
    private GenreService genreServiceMock;

    @Autowired
    private MockMvc restGenreMockMvc;

    private Genre genre;

    private Genre insertedGenre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Genre createEntity() {
        return new Genre().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Genre createUpdatedEntity() {
        return new Genre().name(UPDATED_NAME);
    }

    @BeforeEach
    public void initTest() {
        genre = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedGenre != null) {
            genreRepository.delete(insertedGenre);
            insertedGenre = null;
        }
    }

    @Test
    void createGenre() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Genre
        GenreDTO genreDTO = genreMapper.toDto(genre);
        var returnedGenreDTO = om.readValue(
            restGenreMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GenreDTO.class
        );

        // Validate the Genre in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGenre = genreMapper.toEntity(returnedGenreDTO);
        assertGenreUpdatableFieldsEquals(returnedGenre, getPersistedGenre(returnedGenre));

        insertedGenre = returnedGenre;
    }

    @Test
    void createGenreWithExistingId() throws Exception {
        // Create the Genre with an existing ID
        genre.setId("existing_id");
        GenreDTO genreDTO = genreMapper.toDto(genre);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGenreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        genre.setName(null);

        // Create the Genre, which fails.
        GenreDTO genreDTO = genreMapper.toDto(genre);

        restGenreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllGenres() throws Exception {
        // Initialize the database
        insertedGenre = genreRepository.save(genre);

        // Get all the genreList
        restGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(genre.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGenresWithEagerRelationshipsIsEnabled() throws Exception {
        when(genreServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGenreMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(genreServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGenresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(genreServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGenreMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(genreRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getGenre() throws Exception {
        // Initialize the database
        insertedGenre = genreRepository.save(genre);

        // Get the genre
        restGenreMockMvc
            .perform(get(ENTITY_API_URL_ID, genre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(genre.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    void getNonExistingGenre() throws Exception {
        // Get the genre
        restGenreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingGenre() throws Exception {
        // Initialize the database
        insertedGenre = genreRepository.save(genre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the genre
        Genre updatedGenre = genreRepository.findById(genre.getId()).orElseThrow();
        updatedGenre.name(UPDATED_NAME);
        GenreDTO genreDTO = genreMapper.toDto(updatedGenre);

        restGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, genreDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreDTO))
            )
            .andExpect(status().isOk());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGenreToMatchAllProperties(updatedGenre);
    }

    @Test
    void putNonExistingGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genre.setId(UUID.randomUUID().toString());

        // Create the Genre
        GenreDTO genreDTO = genreMapper.toDto(genre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, genreDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genre.setId(UUID.randomUUID().toString());

        // Create the Genre
        GenreDTO genreDTO = genreMapper.toDto(genre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(genreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genre.setId(UUID.randomUUID().toString());

        // Create the Genre
        GenreDTO genreDTO = genreMapper.toDto(genre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateGenreWithPatch() throws Exception {
        // Initialize the database
        insertedGenre = genreRepository.save(genre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the genre using partial update
        Genre partialUpdatedGenre = new Genre();
        partialUpdatedGenre.setId(genre.getId());

        partialUpdatedGenre.name(UPDATED_NAME);

        restGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenre.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGenre))
            )
            .andExpect(status().isOk());

        // Validate the Genre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGenreUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGenre, genre), getPersistedGenre(genre));
    }

    @Test
    void fullUpdateGenreWithPatch() throws Exception {
        // Initialize the database
        insertedGenre = genreRepository.save(genre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the genre using partial update
        Genre partialUpdatedGenre = new Genre();
        partialUpdatedGenre.setId(genre.getId());

        partialUpdatedGenre.name(UPDATED_NAME);

        restGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenre.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGenre))
            )
            .andExpect(status().isOk());

        // Validate the Genre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGenreUpdatableFieldsEquals(partialUpdatedGenre, getPersistedGenre(partialUpdatedGenre));
    }

    @Test
    void patchNonExistingGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genre.setId(UUID.randomUUID().toString());

        // Create the Genre
        GenreDTO genreDTO = genreMapper.toDto(genre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, genreDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(genreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genre.setId(UUID.randomUUID().toString());

        // Create the Genre
        GenreDTO genreDTO = genreMapper.toDto(genre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(genreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genre.setId(UUID.randomUUID().toString());

        // Create the Genre
        GenreDTO genreDTO = genreMapper.toDto(genre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(genreDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteGenre() throws Exception {
        // Initialize the database
        insertedGenre = genreRepository.save(genre);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the genre
        restGenreMockMvc
            .perform(delete(ENTITY_API_URL_ID, genre.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return genreRepository.count();
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

    protected Genre getPersistedGenre(Genre genre) {
        return genreRepository.findById(genre.getId()).orElseThrow();
    }

    protected void assertPersistedGenreToMatchAllProperties(Genre expectedGenre) {
        assertGenreAllPropertiesEquals(expectedGenre, getPersistedGenre(expectedGenre));
    }

    protected void assertPersistedGenreToMatchUpdatableProperties(Genre expectedGenre) {
        assertGenreAllUpdatablePropertiesEquals(expectedGenre, getPersistedGenre(expectedGenre));
    }
}
