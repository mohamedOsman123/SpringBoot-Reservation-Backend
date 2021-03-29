package com.hesho.reservation.service.impl;
import com.hesho.reservation.domain.Category;
import com.hesho.reservation.domain.Place;
import com.hesho.reservation.repository.CategoryRepository;
import com.hesho.reservation.repository.PlaceRepository;
import com.hesho.reservation.security.ImageException;
import com.hesho.reservation.security.StorageException;
import com.hesho.reservation.service.ImageService;
import com.hesho.reservation.domain.Image;
import com.hesho.reservation.repository.ImageRepository;
import com.hesho.reservation.service.dto.ImageDTO;
import com.hesho.reservation.service.dto.PlaceDTO;
import com.hesho.reservation.service.mapper.ImageMapper;
import io.github.jhipster.security.RandomUtil;
import liquibase.pro.packaged.I;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import org.apache.commons.io.FilenameUtils;

/**
 * Service Implementation for managing {@link Image}.
 */
@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;

    private final PlaceRepository placeRepository;

    private final ImageMapper imageMapper;

    private final CategoryRepository categoryRepository;

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(env.getProperty("image.place.dir")));
            Files.createDirectories(Paths.get(env.getProperty("image.category.dir")));
        } catch (Exception e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    public ImageServiceImpl(ImageRepository imageRepository, ImageMapper imageMapper,PlaceRepository placeRepository,CategoryRepository categoryRepository) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.placeRepository=placeRepository;
        this.categoryRepository=categoryRepository;
    }

    @Override
    public ImageDTO save(ImageDTO imageDTO) {
        log.debug("Request to save Image : {}", imageDTO);
        Image image = imageMapper.toEntity(imageDTO);
        image = imageRepository.save(image);
        return imageMapper.toDto(image);
    }

    @Override
    public ImageDTO saveImagesForPlace(MultipartFile image, Long placeId){
        // find place by placeId
       Optional<Place> place=placeRepository.findById(placeId);
       if (place.isPresent()) {
           Place placeEntity=place.get();
                   Image imageEntity=new Image();
                   String fileName = StringUtils.cleanPath(RandomUtil.generateRandomAlphanumericString() + "." + FilenameUtils.getExtension(image.getOriginalFilename()));
                   try {
                   if (fileName.isEmpty()) {
                       throw new StorageException("Failed to store empty file " + fileName);
                   }
                   if (fileName.contains("..")) {
                       throw new StorageException("Cannot store file with relative path outside current directory " + fileName);
                   }
                   try (InputStream inputStream = image.getInputStream()) {
                       Files.copy(inputStream, Paths.get(env.getProperty("image.place.dir")).resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                     imageEntity.setImageUrl(fileName);
                     imageEntity.setPlace(placeEntity);
                     imageRepository.save(imageEntity);
                     return imageMapper.toDto(imageEntity);
                   }
               }
                   catch(IOException e){
                       throw new StorageException("Failed to store file " + fileName, e);
                   }
           }
       else {
           throw new StorageException("Could not read file: " + image);
       }
    }
    @Override
    public ImageDTO saveImagesForCategory(MultipartFile image, Long categoryId){
        // find place by placeId
        Optional<Category> category=categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            Category categoryEntity=category.get();
            // create new image entity to save images for place
            Image imageEntity=new Image();
            String fileName = StringUtils.cleanPath(RandomUtil.generateRandomAlphanumericString() + "." + FilenameUtils.getExtension(image.getOriginalFilename()));
            try {
                if (fileName.isEmpty()) {
                    throw new StorageException("Failed to store empty file " + fileName);
                }
                if (fileName.contains("..")) {
                    throw new StorageException("Cannot store file with relative path outside current directory " + fileName);
                }
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(env.getProperty("image.category.dir")).resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                    imageEntity.setImageUrl(fileName);
                    imageEntity.setCategory(categoryEntity);
                    imageRepository.save(imageEntity);
                    return imageMapper.toDto(imageEntity);
                }
            }
            catch(IOException e){
                throw new StorageException("Failed to store file " + fileName, e);
            }
        }
        else {
            throw new StorageException("Could not read file: " + image);
        }
    }

    @Override
    public ImageDTO setMainImagesForCategory(Long imageId,Long categoryId){

            Optional<Image> mainImage=imageRepository.findOneByCategoryIdAndMainIsTrue(categoryId);
            if (mainImage.isPresent()){
                mainImage.get().setMain(false);
                imageRepository.save(mainImage.get());
            }
           Optional<Image> image=imageRepository.findById(imageId);
            image.get().setMain(true);
            return imageMapper.toDto(image.get());
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ImageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Images");
        return imageRepository.findAll(pageable)
            .map(imageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ImageDTO> findOne(Long id) {
        log.debug("Request to get Image : {}", id);
        return imageRepository.findById(id)
            .map(imageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UrlResource findOneByImageUrl(String imageName){
        log.debug("Request to get Image : {}", imageName);
        String fileName=null;
        try {
            Optional<Image> image = imageRepository.findOneByImageUrl(imageName);
            if (image.isPresent()) {
                Image imageEntity = image.get();
                fileName=imageEntity.getImageUrl();
                Path file = Paths.get(env.getProperty("image.place.dir")).resolve(fileName);
                UrlResource resource = new UrlResource(file.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    file = Paths.get(env.getProperty("image.category.dir")).resolve(fileName);
                    resource = new UrlResource(file.toUri());
                    if (resource.exists() || resource.isReadable()) {
                        return resource;
                    } else {
                        throw new StorageException("Could not read file: " + fileName);
                    }
                }
            }
            else {
                throw new ImageException("No Image Found");
            }
        } catch (MalformedURLException e) {
            throw new ImageException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UrlResource findOneByPlaceIdAndMainIsTrue(Long placeId){
            log.debug("Request to get Image : {}", placeId);
            String fileName=null;
        try {
            Optional<Image> image = imageRepository.findOneByPlaceIdAndMainIsTrue(placeId);
            if (image.isPresent()) {
                Image imageEntity = image.get();
                fileName=imageEntity.getImageUrl();
                Path file = Paths.get(env.getProperty("image.place.dir")).resolve(fileName);
                UrlResource resource = new UrlResource(file.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    throw new StorageException("Could not read file: " + fileName);
                }
            }
            else {
                throw new ImageException("No Image Found");
            }
        } catch (MalformedURLException e) {
            throw new ImageException(e);
        }
    }



    @Override
    @Transactional(readOnly = true)
    public UrlResource findOneByCategoryIdAndMainIsTrue(Long categoryId){
        log.debug("Request to get Image : {}", categoryId);
        String fileName=null;
        try {
            Optional<Image> image = imageRepository.findOneByCategoryIdAndMainIsTrue(categoryId);
            if (image.isPresent()) {
                Image imageEntity = image.get();
                fileName=imageEntity.getImageUrl();
                Path file = Paths.get(env.getProperty("image.category.dir")).resolve(fileName);
                UrlResource resource = new UrlResource(file.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    throw new StorageException("Could not read file: " + fileName);
                }
            }
            else {
                throw new ImageException("No Image Found");
            }
        } catch (MalformedURLException e) {
            throw new ImageException(e);
        }
    }


    @Override
    public void delete(Long id) {
        log.debug("Request to delete Image : {}", id);
        imageRepository.deleteById(id);
    }

    @Override
    public ImageDTO setMainImagesForPlace(Long imageId, Long placeId) {
        Optional<Image> mainImage=imageRepository.findOneByPlaceIdAndMainIsTrue(placeId);
        if (mainImage.isPresent()){
            mainImage.get().setMain(false);
            imageRepository.save(mainImage.get());
        }
        Optional<Image> image=imageRepository.findById(imageId);
        image.get().setMain(true);
        return imageMapper.toDto(image.get());
    }
    @Override
    public Optional<Category> findCategoryById(Long imageId) {
        return imageRepository.findCategoryById(imageId);
    }
    @Override
    public Optional<Place> findPlaceById(Long id) {
        return imageRepository.findPlaceById(id);
    }

}
