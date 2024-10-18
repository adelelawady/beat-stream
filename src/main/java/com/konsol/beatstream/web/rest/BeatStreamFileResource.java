package com.konsol.beatstream.web.rest;

import com.konsol.beatstream.repository.BeatStreamFileRepository;
import com.konsol.beatstream.service.BeatStreamFileService;
import com.konsol.beatstream.service.dto.BeatStreamFileDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.konsol.beatstream.domain.BeatStreamFile}.
 */
@RestController
@RequestMapping("/api/beat-stream-files")
public class BeatStreamFileResource {

    private static final Logger LOG = LoggerFactory.getLogger(BeatStreamFileResource.class);

    private static final String ENTITY_NAME = "beatStreamFile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BeatStreamFileService beatStreamFileService;

    private final BeatStreamFileRepository beatStreamFileRepository;

    public BeatStreamFileResource(BeatStreamFileService beatStreamFileService, BeatStreamFileRepository beatStreamFileRepository) {
        this.beatStreamFileService = beatStreamFileService;
        this.beatStreamFileRepository = beatStreamFileRepository;
    }

    /**
     * {@code POST  /beat-stream-files} : Create a new beatStreamFile.
     *
     * @param beatStreamFileDTO the beatStreamFileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new beatStreamFileDTO, or with status {@code 400 (Bad Request)} if the beatStreamFile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BeatStreamFileDTO> createBeatStreamFile(@Valid @RequestBody BeatStreamFileDTO beatStreamFileDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save BeatStreamFile : {}", beatStreamFileDTO);
        if (beatStreamFileDTO.getId() != null) {
            throw new BadRequestAlertException("A new beatStreamFile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        beatStreamFileDTO = beatStreamFileService.save(beatStreamFileDTO);
        return ResponseEntity.created(new URI("/api/beat-stream-files/" + beatStreamFileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, beatStreamFileDTO.getId()))
            .body(beatStreamFileDTO);
    }

    /**
     * {@code PUT  /beat-stream-files/:id} : Updates an existing beatStreamFile.
     *
     * @param id the id of the beatStreamFileDTO to save.
     * @param beatStreamFileDTO the beatStreamFileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated beatStreamFileDTO,
     * or with status {@code 400 (Bad Request)} if the beatStreamFileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the beatStreamFileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BeatStreamFileDTO> updateBeatStreamFile(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody BeatStreamFileDTO beatStreamFileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BeatStreamFile : {}, {}", id, beatStreamFileDTO);
        if (beatStreamFileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, beatStreamFileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!beatStreamFileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        beatStreamFileDTO = beatStreamFileService.update(beatStreamFileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, beatStreamFileDTO.getId()))
            .body(beatStreamFileDTO);
    }

    /**
     * {@code PATCH  /beat-stream-files/:id} : Partial updates given fields of an existing beatStreamFile, field will ignore if it is null
     *
     * @param id the id of the beatStreamFileDTO to save.
     * @param beatStreamFileDTO the beatStreamFileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated beatStreamFileDTO,
     * or with status {@code 400 (Bad Request)} if the beatStreamFileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the beatStreamFileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the beatStreamFileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BeatStreamFileDTO> partialUpdateBeatStreamFile(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody BeatStreamFileDTO beatStreamFileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BeatStreamFile partially : {}, {}", id, beatStreamFileDTO);
        if (beatStreamFileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, beatStreamFileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!beatStreamFileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BeatStreamFileDTO> result = beatStreamFileService.partialUpdate(beatStreamFileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, beatStreamFileDTO.getId())
        );
    }

    /**
     * {@code GET  /beat-stream-files} : get all the beatStreamFiles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of beatStreamFiles in body.
     */
    @GetMapping("")
    public List<BeatStreamFileDTO> getAllBeatStreamFiles() {
        LOG.debug("REST request to get all BeatStreamFiles");
        return beatStreamFileService.findAll();
    }

    /**
     * {@code GET  /beat-stream-files/:id} : get the "id" beatStreamFile.
     *
     * @param id the id of the beatStreamFileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the beatStreamFileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BeatStreamFileDTO> getBeatStreamFile(@PathVariable("id") String id) {
        LOG.debug("REST request to get BeatStreamFile : {}", id);
        Optional<BeatStreamFileDTO> beatStreamFileDTO = beatStreamFileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(beatStreamFileDTO);
    }

    /**
     * {@code DELETE  /beat-stream-files/:id} : delete the "id" beatStreamFile.
     *
     * @param id the id of the beatStreamFileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeatStreamFile(@PathVariable("id") String id) {
        LOG.debug("REST request to delete BeatStreamFile : {}", id);
        beatStreamFileService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
