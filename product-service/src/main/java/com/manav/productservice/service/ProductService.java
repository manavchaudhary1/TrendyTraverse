package com.manav.productservice.service;

import com.manav.productservice.dto.*;
import com.manav.productservice.exception.CustomException;
import com.manav.productservice.mapper.ProductMapper;
import com.manav.productservice.mapper.ReviewMapper;
import com.manav.productservice.model.Product;
import com.manav.productservice.model.ProductFeatures;
import com.manav.productservice.model.ProductImage;
import com.manav.productservice.model.Review;
import com.manav.productservice.repository.ProductFeaturesRepository;
import com.manav.productservice.repository.ProductImageRepository;
import com.manav.productservice.repository.ProductRedisRepository;
import com.manav.productservice.repository.ProductRepository;
import com.manav.productservice.service.client.ReviewRestTemplateClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductFeaturesRepository productFeaturesRepository;
    private final ReviewRestTemplateClient reviewRestTemplateClient;
    private final ProductRedisRepository productRedisRepository;
    private final ProductMapper productMapper;
    private final ReviewMapper reviewMapper;
    private final Random random = new Random();

    @CircuitBreaker(name = "productService", fallbackMethod = "buildFallBackProduct")
    @RateLimiter(name = "productService", fallbackMethod = "buildFallBackProduct")
    @Retry(name = "retryProductService", fallbackMethod = "buildFallBackProduct")
    @Bulkhead(name = "bulkheadProductService", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "buildFallBackProduct")
    public ProductResponseDTO getProductById(Long productId) {
        try {
            Product cachedProduct = checkRedisCache(productId);
            if (cachedProduct != null) {
                return buildFullResponseDTO(cachedProduct);
            } else {
                log.debug("Product {} not found in Redis cache", productId);
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new CustomException(
                                String.format("Product with ID %d not found", productId)
                        ));
                log.debug("Product {} cached in Redis", productId);
                cacheProductObject(product);

                return buildFullResponseDTO(product);
            }
        } catch (CustomException e) {
            throw new CustomException("Error retrieving product: " + e.getMessage());
        }
    }

    private ProductResponseDTO buildFullResponseDTO(Product product) {
        // Map basic product data
        ProductResponseDTO baseDto = productMapper.toResponseDTO(product);

        // Map images
        List<ProductImageDTO> imageDTOs = product.getProductImages().stream()
                .map(productMapper::toImageDTO)
                .collect(Collectors.toList());

        // Map features
        List<ProductFeatureDTO> featureDTOs = product.getFeatureBullets().stream()
                .map(productMapper::toFeatureDTO)
                .collect(Collectors.toList());

        // Get reviews
        List<Review> reviews = getReviews(product.getProductId());
        List<ReviewDTO> reviewDTOs = reviewMapper.toDtoList(reviews);

        // Create a new DTO with all data
        return new ProductResponseDTO(
                baseDto.productId(),
                baseDto.name(),
                baseDto.brand(),
                imageDTOs,
                baseDto.fullDescription(),
                featureDTOs,
                baseDto.pricing(),
                baseDto.listPrice(),
                baseDto.availabilityStatus(),
                baseDto.productCategory(),
                baseDto.productDimensions(),
                baseDto.dateFirstAvailable(),
                baseDto.manufacturer(),
                baseDto.countryOfOrigin(),
                baseDto.averageRating(),
                baseDto.totalReviews(),
                baseDto.fiveStarReviews(),
                baseDto.fourStarReviews(),
                baseDto.threeStarReviews(),
                baseDto.twoStarReviews(),
                baseDto.oneStarReviews(),
                reviewDTOs
        );
    }

    private List<Review> getReviews(Long productId) {
        return reviewRestTemplateClient.getAllReviews(productId);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO createDTO) {
        try {
            // Map DTO to entity
            Product product = productMapper.toEntity(createDTO);
            product = productRepository.save(product);

            // Handle image URLs
            if (createDTO.imageUrls() != null && !createDTO.imageUrls().isEmpty()) {
                List<ProductImage> images = productMapper.toProductImageList(createDTO.imageUrls(), product);
                productImageRepository.saveAll(images);
            }

            // Handle feature bullets
            if (createDTO.featureBullets() != null && !createDTO.featureBullets().isEmpty()) {
                List<ProductFeatures> features = productMapper.toProductFeaturesList(createDTO.featureBullets(), product);
                productFeaturesRepository.saveAll(features);
            }

            // Retrieve the saved product with relationships
            Product savedProduct = productRepository.findById(product.getProductId())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve saved product"));

            return buildFullResponseDTO(savedProduct);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating product", e);
        }
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductUpdateDTO updateDTO) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new CustomException("Product not found"));

            // Update properties from DTO
            productMapper.updateProductFromDTO(updateDTO, product);

            // Handle image URLs replacement
            if (updateDTO.imageUrls() != null) {
                productImageRepository.deleteByProduct(product);

                if (!updateDTO.imageUrls().isEmpty()) {
                    List<ProductImage> newImages = productMapper.toProductImageList(updateDTO.imageUrls(), product);
                    productImageRepository.saveAll(newImages);
                }
            }

            // Handle feature bullets replacement
            if (updateDTO.featureBullets() != null) {
                productFeaturesRepository.deleteByProduct(product);

                if (!updateDTO.featureBullets().isEmpty()) {
                    List<ProductFeatures> newFeatures = productMapper.toProductFeaturesList(updateDTO.featureBullets(), product);
                    productFeaturesRepository.saveAll(newFeatures);
                }
            }

            productRepository.save(product);

            // Fetch a fresh copy of the entity after saving
            Product freshProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new CustomException("Product not found after update"));

            removeFromCache(productId);
            cacheProductObject(freshProduct);
            return buildFullResponseDTO(freshProduct);
        } catch (Exception e) {
            log.error("Error updating product {}: {}", productId, e.getMessage(), e);
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
                log.error("Error getting review count: {}", e.getMessage(), e);
                reviewsCount = 0; // Default if it can't get count
            }

            // Delete related entities
            try {
                productImageRepository.deleteByProduct(product);
            } catch (Exception e) {
                log.error("Error deleting product images: {}", e.getMessage(), e);
                throw e;
            }

            try {
                productFeaturesRepository.deleteByProduct(product);
            } catch (Exception e) {
                log.error("Error deleting product features: {}", e.getMessage(), e);
                throw e;
            }

            try {
                reviewRestTemplateClient.deleteAllReviews(productId);
            } catch (Exception e) {
                log.error("Error deleting reviews: {}", e.getMessage(), e);
            }

            // Delete from DB first
            productRepository.delete(product);

            // Then remove from cache after successful DB deletion
            removeFromCache(productId);

            // Create response
            return new ProductDeletionResponseDTO(
                    productId,
                    product.getName(),
                    imagesCount,
                    featuresCount,
                    reviewsCount,
                    LocalDateTime.now(),
                    "Product and related data successfully deleted"
            );
        } catch (Exception e) {
            log.error("Error deleting product {}: {}", productId, e.getMessage(), e);
            throw new CustomException("Error deleting product: " + e.getMessage());
        }
    }

    public List<ProductSearchResultDTO> searchProductsByKeyword(String keyword) {
        try {
            List<Object[]> results = productRepository.searchProductsByKeyword(keyword);

            List<ProductSearchResultDTO> dtos = new ArrayList<>();
            for (Object[] result : results)
                try {
                    Long productId = ((Number) result[0]).longValue();
                    String name = (String) result[1];
                    BigDecimal pricing = (BigDecimal) result[2];
                    String firstImage = (String) result[3];

                    dtos.add(new ProductSearchResultDTO(productId, name, pricing, firstImage));
                } catch (Exception e) {
                    log.error("Error mapping result: {}", Arrays.toString(result), e);
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
        return new ProductResponseDTO(
                0L,
                "N/A",
                "N/A",
                List.of(),
                "N/A",
                List.of(),
                BigDecimal.valueOf(0.0),
                BigDecimal.valueOf(0.0),
                "N/A",
                "N/A",
                "N/A",
                null,
                "N/A",
                "N/A",
                0.0,
                0,
                0,
                0,
                0,
                0,
                0,
                List.of()
        );
    }
}