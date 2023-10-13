package it.unibo.avvoltoio.web.rest;

import it.unibo.avvoltoio.domain.Squeal;
import it.unibo.avvoltoio.repository.SquealRepository;
import it.unibo.avvoltoio.service.SquealService;
import it.unibo.avvoltoio.service.dto.SquealDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.unibo.avvoltoio.domain.Squeal}.
 */
@RestController
@RequestMapping("/api")
public class SquealResource {

    @Autowired
    private SquealService squealService;

    private final Logger log = LoggerFactory.getLogger(SquealResource.class);

    private static final String ENTITY_NAME = "squeal";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SquealRepository squealRepository;

    public SquealResource(SquealRepository squealRepository) {
        this.squealRepository = squealRepository;
    }

    @GetMapping("/squeals-destination")
    public ResponseEntity<List<String>> getSquealDestination(@RequestParam("name") String name) {
        log.debug("REST request to get SquealDestination : {}", name);
        List<String> ret = squealService.getSquealDestination(name);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    /**
     * {@code POST  /squeals} : Create a new squeal.
     *
     * @param squeal the squeal to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new squeal, or with status {@code 400 (Bad Request)} if the
     *         squeal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/squeals")
    public ResponseEntity<SquealDTO> createOrUpdateSqueal(@RequestBody SquealDTO squeal) throws URISyntaxException {
        log.debug("REST request to save Squeal : {}", squeal);
        SquealDTO result = squealService.insertOrUpdate(squeal);
        return ResponseEntity
            .created(new URI("/api/squeals/" + result.getSqueal().getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getSqueal().getId()))
            .body(result);
    }

    /**
     * {@code GET  /squeals} : get all the squeals.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of squeals in body.
     */
    @GetMapping("/squeal-list")
    public List<SquealDTO> getSquealList(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        log.debug("REST request to get squeals");
        return squealService.getSquealsList(page, size);
    }

    /**
     * {@code GET  /squeals} : get all the squeals.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of squeals in body.
     */
    @GetMapping("/squeals")
    public List<Squeal> getAllSqueals() {
        log.debug("REST request to get all Squeals");
        return squealRepository.findAll();
    }

    /**
     * {@code GET  /squeals/:id} : get the "id" squeal.
     *
     * @param id the id of the squeal to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the squeal, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/squeals/{id}")
    public ResponseEntity<SquealDTO> getSqueal(@PathVariable String id) {
        log.debug("REST request to get Squeal : {}", id);
        Optional<SquealDTO> squeal = Optional.ofNullable(this.squealService.getSqueal(id));
        return ResponseUtil.wrapOrNotFound(squeal);
    }

    @GetMapping("/direct-squeal-list")
    public ResponseEntity<List<SquealDTO>> getDirectSqueal() {
        log.debug("REST request to get direct Squeal : {}");
        Optional<List<SquealDTO>> squeal = Optional.ofNullable(this.squealService.getDirectSqueal());
        return ResponseUtil.wrapOrNotFound(squeal);
    }

    @GetMapping("/direct-squeal-preview")
    public ResponseEntity<List<SquealDTO>> getDirectSquealPreview() {
        log.debug("REST request to get direct Squeal : {}");
        List<SquealDTO> squeal = this.squealService.getDirectSquealPreview();
        return new ResponseEntity<>(squeal, HttpStatus.OK);
    }

    @GetMapping("/squeal-by-destination/{destination}")
    public ResponseEntity<List<Squeal>> getSquealsByDestination(@PathVariable String destination) {
        log.debug("REST request to get Squeal : {}", destination);

        List<Squeal> ret = squealRepository.findAllByDestinations_DestinationIgnoreCaseOrderByTimestampDesc(destination);

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/squeal-by-user/{userId}")
    public ResponseEntity<List<SquealDTO>> getSquealsSentByUser(@PathVariable String userId) {
        log.debug("REST request to get Squeal : {}", userId);

        List<SquealDTO> ret = squealService.getSquealByUser(userId);

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/squeal-made-by-user")
    public ResponseEntity<List<SquealDTO>> getSquealsMade() {
        log.debug("REST request to get Squeal : {}");

        List<SquealDTO> ret = squealService.getSquealMadeByUser();

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/squeal-by-channel/{channelId}")
    public ResponseEntity<List<SquealDTO>> getSquealsByChannel(
        @PathVariable String channelId,
        @RequestParam("page") Integer page,
        @RequestParam("size") Integer size
    ) {
        log.debug("REST request to get Squeal : {}", channelId);

        List<SquealDTO> ret = squealService.getSquealByChannel(channelId, page, size);

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    /**
     * {@code DELETE  /squeals/:id} : delete the "id" squeal.
     *
     * @param id the id of the squeal to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/squeals/{id}")
    public ResponseEntity<Void> deleteSqueal(@PathVariable String id) {
        log.debug("REST request to delete Squeal : {}", id);
        squealRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
