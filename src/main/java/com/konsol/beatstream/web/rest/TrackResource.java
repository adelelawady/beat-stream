package com.konsol.beatstream.web.rest;

import com.konsol.beatstream.repository.TrackRepository;
import com.konsol.beatstream.service.TrackService;
import com.konsol.beatstream.service.dto.TrackDTO;
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
 * REST controller for managing {@link com.konsol.beatstream.domain.Track}.
 */
@RestController
@RequestMapping("/api/tracks")
public class TrackResource {

    private static final Logger LOG = LoggerFactory.getLogger(TrackResource.class);

    private static final String ENTITY_NAME = "track";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrackService trackService;

    private final TrackRepository trackRepository;

    public TrackResource(TrackService trackService, TrackRepository trackRepository) {
        this.trackService = trackService;
        this.trackRepository = trackRepository;
    }

    /**
     * {@code POST  /tracks} : Create a new track.
     *
     * @param trackDTO the trackDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trackDTO, or with status {@code 400 (Bad Request)} if the track has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TrackDTO> createTrack(@Valid @RequestBody TrackDTO trackDTO) throws URISyntaxException {
        LOG.debug("REST request to save Track : {}", trackDTO);
        if (trackDTO.getId() != null) {
            throw new BadRequestAlertException("A new track cannot already have an ID", ENTITY_NAME, "idexists");
        }
        trackDTO = trackService.save(trackDTO);
        return ResponseEntity.created(new URI("/api/tracks/" + trackDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, trackDTO.getId()))
            .body(trackDTO);
    }

    /**
     * {@code PUT  /tracks/:id} : Updates an existing track.
     *
     * @param id the id of the trackDTO to save.
     * @param trackDTO the trackDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackDTO,
     * or with status {@code 400 (Bad Request)} if the trackDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trackDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TrackDTO> updateTrack(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody TrackDTO trackDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Track : {}, {}", id, trackDTO);
        if (trackDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trackDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!trackRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        trackDTO = trackService.update(trackDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackDTO.getId()))
            .body(trackDTO);
    }

    /**
     * {@code PATCH  /tracks/:id} : Partial updates given fields of an existing track, field will ignore if it is null
     *
     * @param id the id of the trackDTO to save.
     * @param trackDTO the trackDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trackDTO,
     * or with status {@code 400 (Bad Request)} if the trackDTO is not valid,
     * or with status {@code 404 (Not Found)} if the trackDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the trackDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TrackDTO> partialUpdateTrack(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody TrackDTO trackDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Track partially : {}, {}", id, trackDTO);
        if (trackDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trackDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!trackRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TrackDTO> result = trackService.partialUpdate(trackDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trackDTO.getId())
        );
    }

    /**
     * {@code GET  /tracks} : get all the tracks.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tracks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TrackDTO>> getAllTracks(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Tracks");
        Page<TrackDTO> page = trackService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tracks/:id} : get the "id" track.
     *
     * @param id the id of the trackDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trackDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrackDTO> getTrack(@PathVariable("id") String id) {
        LOG.debug("REST request to get Track : {}", id);
        Optional<TrackDTO> trackDTO = trackService.findOne(id);
        return ResponseUtil.wrapOrNotFound(trackDTO);
    }

    /**
     * {@code DELETE  /tracks/:id} : delete the "id" track.
     *
     * @param id the id of the trackDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Track : {}", id);
        trackService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
