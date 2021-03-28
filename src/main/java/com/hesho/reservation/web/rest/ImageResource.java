package com.hesho.reservation.web.rest;

import com.google.cloud.storage.StorageException;
import com.hesho.reservation.domain.Category;
import com.hesho.reservation.domain.Place;
import com.hesho.reservation.security.AuthoritiesConstants;
import com.hesho.reservation.service.CategoryService;
import com.hesho.reservation.service.ImageService;
import com.hesho.reservation.service.PlaceService;
import com.hesho.reservation.service.dto.PlaceDTO;
import com.hesho.reservation.web.rest.errors.BadRequestAlertException;
import com.hesho.reservation.service.dto.ImageDTO;
import com.hesho.reservation.service.dto.ImageCriteria;
import com.hesho.reservation.service.ImageQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing {@link com.hesho.reservation.domain.Image}.
 */
@RestController
@RequestMapping("/api")
public class ImageResource {

    private final Logger log = LoggerFactory.getLogger(ImageResource.class);

    private static final String ENTITY_NAME = "image";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImageService imageService;

    private final ImageQueryService imageQueryService;

    private final CategoryService categoryService;

    private final PlaceService placeService;

    public ImageResource(ImageService imageService, ImageQueryService imageQueryService,CategoryService categoryService,PlaceService placeService) {
        this.imageService = imageService;
        this.imageQueryService = imageQueryService;
        this.categoryService =categoryService;
        this.placeService=placeService;

    }


    /**
     * {@code POST  /images} : Create a new image.
     *
     * @param imageDTO the imageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new imageDTO, or with status {@code 400 (Bad Request)} if the image has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/images")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ImageDTO> createImage(@Valid @RequestBody ImageDTO imageDTO) throws URISyntaxException {
        log.debug("REST request to save Image : {}", imageDTO);
        if (imageDTO.getId() != null) {
            throw new BadRequestAlertException("A new image cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ImageDTO result = imageService.save(imageDTO);
        return ResponseEntity.created(new URI("/api/images/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /images} : Create a new image.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new imageDTO, or with status {@code 400 (Bad Request)} if the image has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/images/place/{placeId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ImageDTO> addImageForPlace(@RequestParam("data") MultipartFile  image,@PathVariable Long placeId) throws URISyntaxException {

        log.debug("REST request to save Place images");
       ImageDTO result = imageService.saveImagesForPlace(image,placeId);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code POST  /images} : Create a new image.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new imageDTO, or with status {@code 400 (Bad Request)} if the image has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/images/category/{categoryId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ImageDTO> addImageForCategory(@RequestParam("data") MultipartFile  image,@PathVariable Long categoryId) throws URISyntaxException {

        log.debug("REST request to save category images");
        ImageDTO result = imageService.saveImagesForCategory(image,categoryId);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code POST  /images} : set main image for Category.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new imageDTO, or with status {@code 400 (Bad Request)} if the image has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @GetMapping("/images/category/main/{imageId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ImageDTO> setMainImageForCategory(@PathVariable Long imageId) throws URISyntaxException {

        log.debug("REST request to set main image for category ");
        Optional<Category> category=categoryService.findCategoryByImageId(imageId);
        ImageDTO result = imageService.setMainImagesForCategory(imageId,category.get().getId());
        return ResponseEntity.ok().body(result);
    }


    /**
     * {@code POST  /images} : set main image for place.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new imageDTO, or with status {@code 400 (Bad Request)} if the image has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @GetMapping("/images/place/main/{imageId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ImageDTO> setMainImageForPlace(@PathVariable Long imageId) throws URISyntaxException {

        log.debug("REST request to set main image for Place");
        Optional<Place> place=placeService.findPlaceByImageId(imageId);
        ImageDTO result = imageService.setMainImagesForPlace(imageId,place.get().getId());
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code PUT  /images} : Updates an existing image.
     *
     * @param imageDTO the imageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated imageDTO,
     * or with status {@code 400 (Bad Request)} if the imageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the imageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/images")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ImageDTO> updateImage(@Valid @RequestBody ImageDTO imageDTO) throws URISyntaxException {
        log.debug("REST request to update Image : {}", imageDTO);
        if (imageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ImageDTO result = imageService.save(imageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, imageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /images} : get all the images.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of images in body.
     */
    @GetMapping("/images")
    public ResponseEntity<List<ImageDTO>> getAllImages(ImageCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Images by criteria: {}", criteria);
        Page<ImageDTO> page = imageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /images/count} : count all the images.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/images/count")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Long> countImages(ImageCriteria criteria) {
        log.debug("REST request to count Images by criteria: {}", criteria);
        return ResponseEntity.ok().body(imageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /images/:id} : get the "id" image.
     *
     * @param id the id of the imageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the imageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/images/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable Long id) {
        log.debug("REST request to get Image : {}", id);
        Optional<ImageDTO> imageDTO = imageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(imageDTO);
    }

    @GetMapping(value = "/images/load/{imageName}",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getImageByImageName(@PathVariable String imageName) {
        log.debug("REST request to get Main Image By imageName : {}", imageName);
        try {
            return ResponseEntity.ok(imageService.findOneByImageUrl(imageName));
        } catch (StorageException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/images/place/{placeId}",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getMainImageByPlaceId(@PathVariable Long placeId) {
        log.debug("REST request to get Main Image By CategoryId : {}", placeId);
        try {
            return ResponseEntity.ok(imageService.findOneByPlaceIdAndMainIsTrue(placeId));
        } catch (StorageException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value="/images/category/{categoryId}",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getMainImageByCategoryId(@PathVariable Long categoryId) {
        log.debug("REST request to get Main Image By CategoryId : {}", categoryId);
        try {
            return ResponseEntity.ok(imageService.findOneByCategoryIdAndMainIsTrue(categoryId));
        } catch (StorageException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * {@code DELETE  /images/:id} : delete the "id" image.
     *
     * @param id the id of the imageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/images/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        log.debug("REST request to delete Image : {}", id);
        imageService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
