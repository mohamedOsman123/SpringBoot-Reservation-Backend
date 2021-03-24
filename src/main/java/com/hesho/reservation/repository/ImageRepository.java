package com.hesho.reservation.repository;

import com.hesho.reservation.domain.Image;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Spring Data  repository for the Image entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImageRepository extends JpaRepository<Image, Long>, JpaSpecificationExecutor<Image> {

    Optional<Image> findOneByPlaceIdAndMainIsTrue(Long placeId);

    Optional<Image> findOneByCategoryIdAndMainIsTrue(Long categoryId);

    Optional<Image> findOneByImageUrl(String imageUrl);
}
