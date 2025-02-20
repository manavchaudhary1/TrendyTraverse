package com.manav.productservice.service;


import com.manav.productservice.dto.*;
import com.manav.productservice.model.Product;
import com.manav.productservice.model.ProductFeatures;
import com.manav.productservice.model.ProductImage;
import com.manav.productservice.model.Review;
import com.manav.productservice.repository.ProductFeaturesRepository;
import com.manav.productservice.repository.ProductImageRepository;
import com.manav.productservice.repository.ProductRepository;
import com.manav.productservice.service.client.ReviewRestTemplateClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductFeaturesRepository productFeaturesRepository;
    private final ReviewRestTemplateClient reviewRestTemplateClient;

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, ProductFeaturesRepository productFeaturesRepository, ReviewRestTemplateClient reviewRestTemplateClient) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productFeaturesRepository = productFeaturesRepository;
        this.reviewRestTemplateClient = reviewRestTemplateClient;
    }

    public ProductResponseDTO getProductById(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            String.format("Product with ID %d not found", productId)
                    ));
            return convertToDTO(product);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving product", e);
        }
    }

    private ProductResponseDTO convertToDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setFullDescription(product.getFullDescription());
        dto.setPricing(product.getPricing());
        dto.setListPrice(product.getListPrice());
        dto.setAvailabilityStatus(product.getAvailabilityStatus());
        dto.setProductCategory(product.getProductCategory());
        dto.setProductDimensions(product.getProductDimensions());
        dto.setDateFirstAvailable(product.getDateFirstAvailable());
        dto.setManufacturer(product.getManufacturer());
        dto.setCountryOfOrigin(product.getCountryOfOrigin());
        dto.setAverageRating(product.getAverageRating());
        dto.setTotalReviews(product.getTotalReviews());
        dto.setFiveStarReviews(product.getFiveStarReviews());
        dto.setFourStarReviews(product.getFourStarReviews());
        dto.setThreeStarReviews(product.getThreeStarReviews());
        dto.setTwoStarReviews(product.getTwoStarReviews());
        dto.setOneStarReviews(product.getOneStarReviews());

        // Convert images
        if (product.getProductImages() != null) {
            dto.setProductImages(product.getProductImages().stream()
                    .map(this::convertToImageDTO)
                    .toList());
        }

        // Convert features
        if (product.getFeatureBullets() != null) {
            dto.setFeatureBullets(product.getFeatureBullets().stream()
                    .map(this::convertToFeatureDTO)
                    .toList());
        }

        // Fetch reviews using ReviewRestTemplateClient and convert them into DTOs
        List<Review> reviews = reviewRestTemplateClient.getAllReviews(product.getProductId());
        if (reviews != null && !reviews.isEmpty()) {
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(this::convertToReviewDTO)
                    .toList();
            dto.setReviews(reviewDTOs);
        }

        return dto;
    }

    private ProductImageDTO convertToImageDTO(ProductImage image) {
        ProductImageDTO dto = new ProductImageDTO();
        dto.setImageId(Long.valueOf(image.getImageId()));
        dto.setImageUrl(image.getImageUrl());
        return dto;
    }

    private ProductFeatureDTO convertToFeatureDTO(ProductFeatures feature) {
        ProductFeatureDTO dto = new ProductFeatureDTO();
        dto.setFeatureId(Long.valueOf(feature.getFeatureId()));
        dto.setBullet(feature.getBullet());
        return dto;
    }

    private ReviewDTO convertToReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setProductId(review.getProductId());
        dto.setStars(review.getStars());
        dto.setReviewDate(review.getReviewDate());
        dto.setVerifiedPurchase(review.getVerifiedPurchase());
        dto.setManufacturerReplied(review.getManufacturerReplied());
        dto.setUserId(review.getUserId());
        dto.setTitle(review.getTitle());
        dto.setReviewText(review.getReviewText());
        dto.setTotalFoundHelpful(review.getTotalFoundHelpful());
        dto.setImages(review.getImages());
        return dto;
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO createDTO) {
        try {
            Product product = new Product();
            BeanUtils.copyProperties(createDTO, product);
            product = productRepository.save(product);

            if (createDTO.getImageUrls() != null) {
                Product finalProduct = product;
                List<ProductImage> images = createDTO.getImageUrls().stream()
                        .map(url -> {
                            ProductImage image = new ProductImage();
                            image.setProduct(finalProduct);
                            image.setImageUrl(url);
                            return image;
                        })
                        .toList();
                productImageRepository.saveAll(images);
            }

            if (createDTO.getFeatureBullets() != null) {
                Product finalProduct1 = product;
                List<ProductFeatures> features = createDTO.getFeatureBullets().stream()
                        .map(bullet -> {
                            ProductFeatures feature = new ProductFeatures();
                            feature.setProduct(finalProduct1);
                            feature.setBullet(bullet);
                            return feature;
                        })
                        .toList();
                productFeaturesRepository.saveAll(features);
            }

            Product savedProduct = productRepository.findById(product.getProductId())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve saved product"));

            return convertToDTO(savedProduct);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating product", e);
        }
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductUpdateDTO updateDTO) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            if (updateDTO.getName() != null) product.setName(updateDTO.getName());
            if (updateDTO.getBrand() != null) product.setBrand(updateDTO.getBrand());
            if (updateDTO.getFullDescription() != null) product.setFullDescription(updateDTO.getFullDescription());
            if (updateDTO.getPricing() != null) product.setPricing(updateDTO.getPricing());
            if (updateDTO.getListPrice() != null) product.setListPrice(updateDTO.getListPrice());
            if (updateDTO.getAvailabilityStatus() != null) product.setAvailabilityStatus(updateDTO.getAvailabilityStatus());
            if (updateDTO.getProductCategory() != null) product.setProductCategory(updateDTO.getProductCategory());
            if (updateDTO.getProductDimensions() != null) product.setProductDimensions(updateDTO.getProductDimensions());
            if (updateDTO.getDateFirstAvailable() != null) product.setDateFirstAvailable(updateDTO.getDateFirstAvailable());
            if (updateDTO.getManufacturer() != null) product.setManufacturer(updateDTO.getManufacturer());
            if (updateDTO.getCountryOfOrigin() != null) product.setCountryOfOrigin(updateDTO.getCountryOfOrigin());

            if (updateDTO.getImageUrls() != null) {
                productImageRepository.deleteByProduct(product);

                Product finalProduct1 = product;
                List<ProductImage> newImages = updateDTO.getImageUrls().stream()
                        .map(url -> {
                            ProductImage image = new ProductImage();
                            image.setProduct(finalProduct1);
                            image.setImageUrl(url);
                            return image;
                        })
                        .toList();
                productImageRepository.saveAll(newImages);
            }

            if (updateDTO.getFeatureBullets() != null) {
                productFeaturesRepository.deleteByProduct(product);

                Product finalProduct = product;
                List<ProductFeatures> newFeatures = updateDTO.getFeatureBullets().stream()
                        .map(bullet -> {
                            ProductFeatures feature = new ProductFeatures();
                            feature.setProduct(finalProduct);
                            feature.setBullet(bullet);
                            return feature;
                        })
                        .toList();
                productFeaturesRepository.saveAll(newFeatures);
            }

            product = productRepository.save(product);
            return convertToDTO(product);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating product", e);
        }
    }

    @Transactional
    public ProductDeletionResponseDTO deleteProduct(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            int imagesCount = productImageRepository.countByProduct(product);
            int featuresCount = productFeaturesRepository.countByProduct(product);
            int reviewsCount = reviewRestTemplateClient.getReviewCount(productId);

            productImageRepository.deleteByProduct(product);
            productFeaturesRepository.deleteByProduct(product);
            reviewRestTemplateClient.deleteAllReviews(productId);

            productRepository.delete(product);

            ProductDeletionResponseDTO response = new ProductDeletionResponseDTO();
            response.setProductId(productId);
            response.setProductName(product.getName());
            response.setImagesDeleted(imagesCount);
            response.setFeaturesDeleted(featuresCount);
            response.setReviewsDeleted(reviewsCount);
            response.setDeletionTimestamp(LocalDateTime.now());
            response.setMessage("Product and related data successfully deleted");

            return response;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting product", e);
        }
    }
}

