package com.konsol.beatstream.web.rest;

import com.konsol.beatstream.domain.ReferanceDownloadTask;
import com.konsol.beatstream.repository.ReferanceDownloadTaskRepository;
import com.konsol.beatstream.service.ReferanceDownloadTaskService;
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
 * REST controller for managing {@link com.konsol.beatstream.domain.ReferanceDownloadTask}.
 */
@RestController
@RequestMapping("/api/referance-download-tasks")
public class ReferanceDownloadTaskResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReferanceDownloadTaskResource.class);

    private static final String ENTITY_NAME = "referanceDownloadTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReferanceDownloadTaskService referanceDownloadTaskService;

    private final ReferanceDownloadTaskRepository referanceDownloadTaskRepository;

    public ReferanceDownloadTaskResource(
        ReferanceDownloadTaskService referanceDownloadTaskService,
        ReferanceDownloadTaskRepository referanceDownloadTaskRepository
    ) {
        this.referanceDownloadTaskService = referanceDownloadTaskService;
        this.referanceDownloadTaskRepository = referanceDownloadTaskRepository;
    }

    /**
     * {@code POST  /referance-download-tasks} : Create a new referanceDownloadTask.
     *
     * @param referanceDownloadTask the referanceDownloadTask to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new referanceDownloadTask, or with status {@code 400 (Bad Request)} if the referanceDownloadTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReferanceDownloadTask> createReferanceDownloadTask(@RequestBody ReferanceDownloadTask referanceDownloadTask)
        throws URISyntaxException {
        LOG.debug("REST request to save ReferanceDownloadTask : {}", referanceDownloadTask);
        if (referanceDownloadTask.getId() != null) {
            throw new BadRequestAlertException("A new referanceDownloadTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        referanceDownloadTask = referanceDownloadTaskService.save(referanceDownloadTask);
        return ResponseEntity.created(new URI("/api/referance-download-tasks/" + referanceDownloadTask.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, referanceDownloadTask.getId()))
            .body(referanceDownloadTask);
    }

    /**
     * {@code PUT  /referance-download-tasks/:id} : Updates an existing referanceDownloadTask.
     *
     * @param id the id of the referanceDownloadTask to save.
     * @param referanceDownloadTask the referanceDownloadTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated referanceDownloadTask,
     * or with status {@code 400 (Bad Request)} if the referanceDownloadTask is not valid,
     * or with status {@code 500 (Internal Server Error)} if the referanceDownloadTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReferanceDownloadTask> updateReferanceDownloadTask(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody ReferanceDownloadTask referanceDownloadTask
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReferanceDownloadTask : {}, {}", id, referanceDownloadTask);
        if (referanceDownloadTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, referanceDownloadTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referanceDownloadTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        referanceDownloadTask = referanceDownloadTaskService.update(referanceDownloadTask);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, referanceDownloadTask.getId()))
            .body(referanceDownloadTask);
    }

    /**
     * {@code PATCH  /referance-download-tasks/:id} : Partial updates given fields of an existing referanceDownloadTask, field will ignore if it is null
     *
     * @param id the id of the referanceDownloadTask to save.
     * @param referanceDownloadTask the referanceDownloadTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated referanceDownloadTask,
     * or with status {@code 400 (Bad Request)} if the referanceDownloadTask is not valid,
     * or with status {@code 404 (Not Found)} if the referanceDownloadTask is not found,
     * or with status {@code 500 (Internal Server Error)} if the referanceDownloadTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReferanceDownloadTask> partialUpdateReferanceDownloadTask(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody ReferanceDownloadTask referanceDownloadTask
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReferanceDownloadTask partially : {}, {}", id, referanceDownloadTask);
        if (referanceDownloadTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, referanceDownloadTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referanceDownloadTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReferanceDownloadTask> result = referanceDownloadTaskService.partialUpdate(referanceDownloadTask);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, referanceDownloadTask.getId())
        );
    }

    /**
     * {@code GET  /referance-download-tasks} : get all the referanceDownloadTasks.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of referanceDownloadTasks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReferanceDownloadTask>> getAllReferanceDownloadTasks(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ReferanceDownloadTasks");
        Page<ReferanceDownloadTask> page = referanceDownloadTaskService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /referance-download-tasks/:id} : get the "id" referanceDownloadTask.
     *
     * @param id the id of the referanceDownloadTask to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the referanceDownloadTask, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReferanceDownloadTask> getReferanceDownloadTask(@PathVariable("id") String id) {
        LOG.debug("REST request to get ReferanceDownloadTask : {}", id);
        Optional<ReferanceDownloadTask> referanceDownloadTask = referanceDownloadTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(referanceDownloadTask);
    }

    /**
     * {@code DELETE  /referance-download-tasks/:id} : delete the "id" referanceDownloadTask.
     *
     * @param id the id of the referanceDownloadTask to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReferanceDownloadTask(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ReferanceDownloadTask : {}", id);
        referanceDownloadTaskService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
