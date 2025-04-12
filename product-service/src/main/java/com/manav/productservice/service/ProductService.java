package com.manav.productservice.service;


import com.manav.productservice.dto.*;
import com.manav.productservice.exception.CustomException;
import com.manav.productservice.model.Product;
import com.manav.productservice.model.ProductFeatures;
import com.manav.productservice.model.ProductImage;
import com.manav.productservice.model.Review;
import com.manav.productservice.repository.ProductFeaturesRepository;
import com.manav.productservice.repository.ProductImageRepository;
import com.manav.productservice.repository.ProductRepository;
import com.manav.productservice.repository.ProductRedisRepository;
import com.manav.productservice.service.client.ReviewRestTemplateClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductFeaturesRepository productFeaturesRepository;
    private final ReviewRestTemplateClient reviewRestTemplateClient;
    private final ProductRedisRepository productRedisRepository;
    private final Random random = new Random();

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, ProductFeaturesRepository productFeaturesRepository, ReviewRestTemplateClient reviewRestTemplateClient, ProductRedisRepository productRedisRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productFeaturesRepository = productFeaturesRepository;
        this.reviewRestTemplateClient = reviewRestTemplateClient;
        this.productRedisRepository = productRedisRepository;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "buildFallBackProduct")
    @RateLimiter(name = "productService", fallbackMethod = "buildFallBackProduct")
    @Retry(name = "retryProductService", fallbackMethod = "buildFallBackProduct")
    @Bulkhead(name = "bulkheadProductService",type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "buildFallBackProduct")
    public ProductResponseDTO getProductById(Long productId) {
        try {
            Product cachedproduct = checkRedisCache(productId);
            if (cachedproduct != null) {
                return convertToDTO(cachedproduct);
            } else {
                log.debug("Product {} not found in Redis cache", productId);
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new CustomException(
                                String.format("Product with ID %d not found", productId)
                        ));
                log.debug("Product {} cached in Redis", productId);
                cacheProductObject(product);

                return convertToDTO(product);
            }
        } catch (CustomException e) {
            throw new CustomException("Error retrieving product: " + e.getMessage());
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
        List <Review> reviews = getReviews(product.getProductId());
        if (reviews != null && !reviews.isEmpty()) {
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(this::convertToReviewDTO)
                    .toList();
            dto.setReviews(reviewDTOs);
        }

        return dto;
    }

    private List<Review> getReviews(Long productId) {
        return reviewRestTemplateClient.getAllReviews(productId);
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
                    .orElseThrow(() -> new CustomException("Product not found"));

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

            // Fetch a fresh copy of the entity after saving
            Product freshProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new CustomException("Product not found after update"));

            removeFromCache(productId);
            cacheProductObject(freshProduct); // Use the fresh entity
            return convertToDTO(freshProduct);
        } catch (Exception e) {
            log.error("Error updating product " + productId + ": " + e.getMessage(), e);
            throw new CustomException("Error updating product: " + e.getMessage());
        }
    }

    @Transactional
    public ProductDeletionResponseDTO deleteProduct(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new CustomException("Product not found"));

            // Count related entities
            int imagesCount = productImageRepository.countByProduct(product);
            int featuresCount = productFeaturesRepository.countByProduct(product);
            int reviewsCount;

            try {
                reviewsCount = reviewRestTemplateClient.getReviewCount(productId);
            } catch (Exception e) {
                log.error("Error getting review count: " + e.getMessage(), e);
                reviewsCount = 0; // Default if can't get count
            }

            // Delete related entities
            try {
                productImageRepository.deleteByProduct(product);
            } catch (Exception e) {
                log.error("Error deleting product images: " + e.getMessage(), e);
                throw e;
            }

            try {
                productFeaturesRepository.deleteByProduct(product);
            } catch (Exception e) {
                log.error("Error deleting product features: " + e.getMessage(), e);
                throw e;
            }

            try {
                reviewRestTemplateClient.deleteAllReviews(productId);
            } catch (Exception e) {
                log.error("Error deleting reviews: " + e.getMessage(), e);
                // Decide if you want to continue or throw the exception
            }

            // Delete from DB first
            productRepository.delete(product);

            // Then remove from cache after successful DB deletion
            removeFromCache(productId);

            // Create response
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
            log.error("Error deleting product " + productId + ": " + e.getMessage(), e);
            throw new CustomException("Error deleting product: " + e.getMessage());
        }
    }

    public List<ProductSearchResultDTO> searchProductsByKeyword(String keyword) {
        try {
            List<Object[]> results = productRepository.searchProductsByKeyword(keyword);

            List<ProductSearchResultDTO> dtos = new ArrayList<>();
            for (Object[] result : results)
                try {
                    ProductSearchResultDTO dto = new ProductSearchResultDTO();
                    dto.setProductId(((Number) result[0]).longValue());
                    dto.setName((String) result[1]);
                    dto.setPricing((BigDecimal) result[2]);  // Check this cast particularly
                    dto.setFirstImage((String) result[3]);
                    dtos.add(dto);
                } catch (Exception e) {
                    log.error("Error mapping result: {}", Arrays.toString(result), e);
                    // Either skip this item or throw to abort the whole operation
                }
            return dtos;
        } catch (Exception e) {
            log.error("Error in searchProductsByKeyword: {}", keyword, e);
            throw e; // Re-throw to see the original error in logs
        }
    }

    private Product checkRedisCache(Long productId) {
        try{
            return productRedisRepository.findById(productId).orElse(null);
        } catch (Exception exception) {
            log.warn("Error retrieving product {} from Redis cache: {}",productId, exception.getMessage());
            return null;
        }
    }

    private void cacheProductObject(Product product){
        try{
            productRedisRepository.save(product);
        }catch (Exception exception) {
            log.warn("Error caching product {} in Redis: {}", product.getProductId(), exception.getMessage());
        }
    }

    private void removeFromCache(Long productId) {
        try {
            productRedisRepository.deleteById(productId);
            log.debug("Product {} removed from Redis cache", productId);
        } catch (Exception exception) {
            log.warn("Error removing product {} from Redis cache: {}", productId, exception.getMessage());
        }
    }


    @SuppressWarnings("unused")
    private void randomlyRunLong() throws InterruptedException, TimeoutException {
        int randomNum = random.nextInt(3) + 1;
        if (randomNum == 3) sleep();
    }
    private void sleep() throws InterruptedException, TimeoutException {
        Thread.sleep(5000);
        throw new TimeoutException();
    }

    @SuppressWarnings("unused")
    private ProductResponseDTO buildFallBackProduct(Long productId, Throwable t) {
        ProductResponseDTO fallback = new ProductResponseDTO();
        fallback.setProductId(0L);
        fallback.setName("N/A");
        fallback.setBrand("N/A");
        fallback.setProductImages(List.of());
        fallback.setFullDescription("N/A");
        fallback.setFeatureBullets(List.of());
        fallback.setPricing(BigDecimal.valueOf(0.0));
        fallback.setListPrice(BigDecimal.valueOf(0.0));
        fallback.setAvailabilityStatus("N/A");
        fallback.setProductCategory("N/A");
        fallback.setProductDimensions("N/A");
        fallback.setDateFirstAvailable(null);
        fallback.setManufacturer("N/A");
        fallback.setCountryOfOrigin("N/A");
        fallback.setAverageRating(0.0);
        fallback.setTotalReviews(0);
        fallback.setFiveStarReviews(0);
        fallback.setFourStarReviews(0);
        fallback.setThreeStarReviews(0);
        fallback.setTwoStarReviews(0);
        fallback.setOneStarReviews(0);
        fallback.setReviews(List.of());
        return fallback;
    }
}
