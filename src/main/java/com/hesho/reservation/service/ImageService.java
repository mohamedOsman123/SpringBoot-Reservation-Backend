package com.hesho.reservation.service;

import com.hesho.reservation.domain.Category;
import com.hesho.reservation.domain.Place;
import com.hesho.reservation.service.dto.ImageDTO;

import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service Interface for managing {@link com.hesho.reservation.domain.Image}.
 */
public interface ImageService {

    /**
     * Save a image.
     *
     * @param imageDTO the entity to save.
     * @return the persisted entity.
     */
    ImageDTO save(ImageDTO imageDTO);

    /**
     * Get all the images.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ImageDTO> findAll(Pageable pageable);


    /**
     * Get the "id" image.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ImageDTO> findOne(Long id);

    /**
     * Get the "id" image By Place.
     *
     * @param imageName the id of the entity.
     * @return the entity.
     */
    public UrlResource findOneByImageUrl(String imageName);

    /**
     * Get the "id" image By Place.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public UrlResource findOneByPlaceIdAndMainIsTrue(Long id);

    /**
     * Get the "id" image By Category.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public UrlResource findOneByCategoryIdAndMainIsTrue(Long id);


   public ImageDTO saveImagesForPlace(MultipartFile image, Long placeId);


    public ImageDTO saveImagesForCategory(MultipartFile image, Long categoryId);

    public ImageDTO setMainImagesForCategory(Long imageId,Long categoryId);
    /**
     * Delete the "id" image.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    ImageDTO setMainImagesForPlace(Long imageId, Long place);

    Optional<Category> findCategoryById(Long id);

    Optional<Place> findPlaceById(Long id);
}
