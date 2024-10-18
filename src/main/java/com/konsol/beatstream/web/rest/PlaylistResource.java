package com.konsol.beatstream.web.rest;

import com.konsol.beatstream.repository.PlaylistRepository;
import com.konsol.beatstream.service.PlaylistService;
import com.konsol.beatstream.service.dto.PlaylistDTO;
import com.konsol.beatstream.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.konsol.beatstream.domain.Playlist}.
 */
@RestController
@RequestMapping("/api/playlists")
public class PlaylistResource {

    private static final Logger LOG = LoggerFactory.getLogger(PlaylistResource.class);

    private static final String ENTITY_NAME = "playlist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlaylistService playlistService;

    private final PlaylistRepository playlistRepository;

    public PlaylistResource(PlaylistService playlistService, PlaylistRepository playlistRepository) {
        this.playlistService = playlistService;
        this.playlistRepository = playlistRepository;
    }

    /**
     * {@code POST  /playlists} : Create a new playlist.
     *
     * @param playlistDTO the playlistDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new playlistDTO, or with status {@code 400 (Bad Request)} if the playlist has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PlaylistDTO> createPlaylist(@Valid @RequestBody PlaylistDTO playlistDTO) throws URISyntaxException {
        LOG.debug("REST request to save Playlist : {}", playlistDTO);
        if (playlistDTO.getId() != null) {
            throw new BadRequestAlertException("A new playlist cannot already have an ID", ENTITY_NAME, "idexists");
        }
        playlistDTO = playlistService.save(playlistDTO);
        return ResponseEntity.created(new URI("/api/playlists/" + playlistDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, playlistDTO.getId()))
            .body(playlistDTO);
    }

    /**
     * {@code PUT  /playlists/:id} : Updates an existing playlist.
     *
     * @param id the id of the playlistDTO to save.
     * @param playlistDTO the playlistDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playlistDTO,
     * or with status {@code 400 (Bad Request)} if the playlistDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the playlistDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PlaylistDTO playlistDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Playlist : {}, {}", id, playlistDTO);
        if (playlistDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playlistDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playlistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        playlistDTO = playlistService.update(playlistDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, playlistDTO.getId()))
            .body(playlistDTO);
    }

    /**
     * {@code PATCH  /playlists/:id} : Partial updates given fields of an existing playlist, field will ignore if it is null
     *
     * @param id the id of the playlistDTO to save.
     * @param playlistDTO the playlistDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playlistDTO,
     * or with status {@code 400 (Bad Request)} if the playlistDTO is not valid,
     * or with status {@code 404 (Not Found)} if the playlistDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the playlistDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PlaylistDTO> partialUpdatePlaylist(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PlaylistDTO playlistDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Playlist partially : {}, {}", id, playlistDTO);
        if (playlistDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playlistDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playlistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PlaylistDTO> result = playlistService.partialUpdate(playlistDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, playlistDTO.getId())
        );
    }

    /**
     * {@code GET  /playlists} : get all the playlists.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of playlists in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PlaylistDTO>> getAllPlaylists(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Playlists");
        Page<PlaylistDTO> page;
        if (eagerload) {
            page = playlistService.findAllWithEagerRelationships(pageable);
        } else {
            page = playlistService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /playlists/:id} : get the "id" playlist.
     *
     * @param id the id of the playlistDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the playlistDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylist(@PathVariable("id") String id) {
        LOG.debug("REST request to get Playlist : {}", id);
        Optional<PlaylistDTO> playlistDTO = playlistService.findOne(id);
        return ResponseUtil.wrapOrNotFound(playlistDTO);
    }

    /**
     * {@code DELETE  /playlists/:id} : delete the "id" playlist.
     *
     * @param id the id of the playlistDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Playlist : {}", id);
        playlistService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
