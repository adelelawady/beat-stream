package com.konsol.beatstream.web.rest;

import com.konsol.beatstream.config.AppSettingsConfiguration;
import com.konsol.beatstream.repository.TaskNodeRepository;
import com.konsol.beatstream.service.TaskNodeService;
import com.konsol.beatstream.service.dto.TaskNodeDTO;
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
 * REST controller for managing {@link com.konsol.beatstream.domain.TaskNode}.
 */
@RestController
@RequestMapping("/api/task-nodes")
public class TaskNodeResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskNodeResource.class);

    private static final String ENTITY_NAME = "taskNode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskNodeService taskNodeService;

    private final TaskNodeRepository taskNodeRepository;

    public TaskNodeResource(TaskNodeService taskNodeService, TaskNodeRepository taskNodeRepository) {
        this.taskNodeService = taskNodeService;
        this.taskNodeRepository = taskNodeRepository;
    }

    /**
     * {@code POST  /task-nodes} : Create a new taskNode.
     *
     * @param taskNodeDTO the taskNodeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskNodeDTO, or with status {@code 400 (Bad Request)} if the taskNode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TaskNodeDTO> createTaskNode(@Valid @RequestBody TaskNodeDTO taskNodeDTO) throws URISyntaxException {
        LOG.debug("REST request to save TaskNode : {}", taskNodeDTO);
        if (taskNodeDTO.getId() != null) {
            throw new BadRequestAlertException("A new taskNode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        taskNodeDTO = taskNodeService.save(taskNodeDTO);
        return ResponseEntity.created(new URI("/api/task-nodes/" + taskNodeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, taskNodeDTO.getId()))
            .body(taskNodeDTO);
    }

    /**
     * {@code PUT  /task-nodes/:id} : Updates an existing taskNode.
     *
     * @param id the id of the taskNodeDTO to save.
     * @param taskNodeDTO the taskNodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskNodeDTO,
     * or with status {@code 400 (Bad Request)} if the taskNodeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskNodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskNodeDTO> updateTaskNode(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody TaskNodeDTO taskNodeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TaskNode : {}, {}", id, taskNodeDTO);
        if (taskNodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskNodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskNodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        taskNodeDTO = taskNodeService.update(taskNodeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskNodeDTO.getId()))
            .body(taskNodeDTO);
    }

    /**
     * {@code PATCH  /task-nodes/:id} : Partial updates given fields of an existing taskNode, field will ignore if it is null
     *
     * @param id the id of the taskNodeDTO to save.
     * @param taskNodeDTO the taskNodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskNodeDTO,
     * or with status {@code 400 (Bad Request)} if the taskNodeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taskNodeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskNodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskNodeDTO> partialUpdateTaskNode(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody TaskNodeDTO taskNodeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TaskNode partially : {}, {}", id, taskNodeDTO);
        if (taskNodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskNodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskNodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        taskNodeDTO.setCanRetry(true);
        Optional<TaskNodeDTO> result = taskNodeService.partialUpdate(taskNodeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskNodeDTO.getId())
        );
    }

    /**
     * {@code GET  /task-nodes} : get all the taskNodes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskNodes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TaskNodeDTO>> getAllTaskNodes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of TaskNodes");
        Page<TaskNodeDTO> page = taskNodeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-nodes/:id} : get the "id" taskNode.
     *
     * @param id the id of the taskNodeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskNodeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskNodeDTO> getTaskNode(@PathVariable("id") String id) {
        LOG.debug("REST request to get TaskNode : {}", id);
        Optional<TaskNodeDTO> taskNodeDTO = taskNodeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskNodeDTO);
    }

    /**
     * {@code DELETE  /task-nodes/:id} : delete the "id" taskNode.
     *
     * @param id the id of the taskNodeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskNode(@PathVariable("id") String id) {
        LOG.debug("REST request to delete TaskNode : {}", id);
        taskNodeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
