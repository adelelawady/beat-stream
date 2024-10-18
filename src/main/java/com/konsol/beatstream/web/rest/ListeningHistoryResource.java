package com.konsol.beatstream.web.rest;

import com.konsol.beatstream.repository.ListeningHistoryRepository;
import com.konsol.beatstream.service.ListeningHistoryService;
import com.konsol.beatstream.service.dto.ListeningHistoryDTO;
import com.konsol.beatstream.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.konsol.beatstream.domain.ListeningHistory}.
 */
@RestController
@RequestMapping("/api/listening-histories")
public class ListeningHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(ListeningHistoryResource.class);

    private static final String ENTITY_NAME = "listeningHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ListeningHistoryService listeningHistoryService;

    private final ListeningHistoryRepository listeningHistoryRepository;

    public ListeningHistoryResource(
        ListeningHistoryService listeningHistoryService,
        ListeningHistoryRepository listeningHistoryRepository
    ) {
        this.listeningHistoryService = listeningHistoryService;
        this.listeningHistoryRepository = listeningHistoryRepository;
    }

    /**
     * {@code POST  /listening-histories} : Create a new listeningHistory.
     *
     * @param listeningHistoryDTO the listeningHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new listeningHistoryDTO, or with status {@code 400 (Bad Request)} if the listeningHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ListeningHistoryDTO> createListeningHistory(@RequestBody ListeningHistoryDTO listeningHistoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ListeningHistory : {}", listeningHistoryDTO);
        if (listeningHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new listeningHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        listeningHistoryDTO = listeningHistoryService.save(listeningHistoryDTO);
        return ResponseEntity.created(new URI("/api/listening-histories/" + listeningHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, listeningHistoryDTO.getId()))
            .body(listeningHistoryDTO);
    }

    /**
     * {@code PUT  /listening-histories/:id} : Updates an existing listeningHistory.
     *
     * @param id the id of the listeningHistoryDTO to save.
     * @param listeningHistoryDTO the listeningHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated listeningHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the listeningHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the listeningHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ListeningHistoryDTO> updateListeningHistory(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody ListeningHistoryDTO listeningHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ListeningHistory : {}, {}", id, listeningHistoryDTO);
        if (listeningHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, listeningHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!listeningHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        listeningHistoryDTO = listeningHistoryService.update(listeningHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, listeningHistoryDTO.getId()))
            .body(listeningHistoryDTO);
    }

    /**
     * {@code PATCH  /listening-histories/:id} : Partial updates given fields of an existing listeningHistory, field will ignore if it is null
     *
     * @param id the id of the listeningHistoryDTO to save.
     * @param listeningHistoryDTO the listeningHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated listeningHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the listeningHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the listeningHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the listeningHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ListeningHistoryDTO> partialUpdateListeningHistory(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody ListeningHistoryDTO listeningHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ListeningHistory partially : {}, {}", id, listeningHistoryDTO);
        if (listeningHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, listeningHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!listeningHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ListeningHistoryDTO> result = listeningHistoryService.partialUpdate(listeningHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, listeningHistoryDTO.getId())
        );
    }

    /**
     * {@code GET  /listening-histories} : get all the listeningHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of listeningHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ListeningHistoryDTO>> getAllListeningHistories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ListeningHistories");
        Page<ListeningHistoryDTO> page = listeningHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /listening-histories/:id} : get the "id" listeningHistory.
     *
     * @param id the id of the listeningHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the listeningHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ListeningHistoryDTO> getListeningHistory(@PathVariable("id") String id) {
        LOG.debug("REST request to get ListeningHistory : {}", id);
        Optional<ListeningHistoryDTO> listeningHistoryDTO = listeningHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(listeningHistoryDTO);
    }

    /**
     * {@code DELETE  /listening-histories/:id} : delete the "id" listeningHistory.
     *
     * @param id the id of the listeningHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListeningHistory(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ListeningHistory : {}", id);
        listeningHistoryService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
