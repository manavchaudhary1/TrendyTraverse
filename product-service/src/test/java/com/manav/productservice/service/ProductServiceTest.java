package com.manav.productservice.service;

import com.manav.productservice.dto.*;
import com.manav.productservice.exception.CustomException;
import com.manav.productservice.model.Product;
import com.manav.productservice.model.ProductFeatures;
import com.manav.productservice.model.ProductImage;
import com.manav.productservice.model.Review;
import com.manav.productservice.repository.ProductFeaturesRepository;
import com.manav.productservice.repository.ProductImageRepository;
import com.manav.productservice.repository.ProductRedisRepository;
import com.manav.productservice.repository.ProductRepository;
import com.manav.productservice.service.ProductService;
import com.manav.productservice.service.client.ReviewRestTemplateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductFeaturesRepository productFeaturesRepository;

    @Mock
    private ReviewRestTemplateClient reviewRestTemplateClient;

    @Mock
    private ProductRedisRepository productRedisRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductResponseDTO expectedResponseDTO;
    private ProductCreateDTO productCreateDTO;
    private ProductUpdateDTO productUpdateDTO;
    private List<Review> testReviews;
    private List<ProductImage> testImages;
    private List<ProductFeatures> testFeatures;

    @BeforeEach
    void setUp() {
        // Initialize test product
        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setName("Test Product");
        testProduct.setBrand("Test Brand");
        testProduct.setFullDescription("Test Description");
        testProduct.setPricing(BigDecimal.valueOf(99.99));
        testProduct.setListPrice(BigDecimal.valueOf(129.99));
        testProduct.setAvailabilityStatus("In Stock");
        testProduct.setProductCategory("Electronics");
        testProduct.setProductDimensions("5x5x5");
        testProduct.setDateFirstAvailable(LocalDate.now());
        testProduct.setManufacturer("Test Manufacturer");
        testProduct.setCountryOfOrigin("Test Country");
        testProduct.setAverageRating(4.5);
        testProduct.setTotalReviews(10);
        testProduct.setFiveStarReviews(5);
        testProduct.setFourStarReviews(3);
        testProduct.setThreeStarReviews(1);
        testProduct.setTwoStarReviews(1);
        testProduct.setOneStarReviews(0);

        // Initialize test images
        testImages = new ArrayList<>();
        ProductImage image1 = new ProductImage();
        image1.setImageId(1);
        image1.setProduct(testProduct);
        image1.setImageUrl("https://example.com/image1.jpg");

        ProductImage image2 = new ProductImage();
        image2.setImageId(2);
        image2.setProduct(testProduct);
        image2.setImageUrl("https://example.com/image2.jpg");

        testImages.add(image1);
        testImages.add(image2);
        testProduct.setProductImages(testImages);

        // Initialize test features
        testFeatures = new ArrayList<>();
        ProductFeatures feature1 = new ProductFeatures();
        feature1.setFeatureId(1);
        feature1.setProduct(testProduct);
        feature1.setBullet("Feature 1");

        ProductFeatures feature2 = new ProductFeatures();
        feature2.setFeatureId(2);
        feature2.setProduct(testProduct);
        feature2.setBullet("Feature 2");

        testFeatures.add(feature1);
        testFeatures.add(feature2);
        testProduct.setFeatureBullets(testFeatures);

        // Initialize test reviews
        testReviews = new ArrayList<>();
        Review review1 = new Review();
        review1.setReviewId(1L);
        review1.setProductId(1L);
        review1.setStars(5);
        review1.setReviewDate(LocalDate.now());
        review1.setVerifiedPurchase(true);
        review1.setManufacturerReplied(false);
        review1.setUserId(UUID.randomUUID());
        review1.setTitle("Great Product");
        review1.setReviewText("I love this product!");
        review1.setTotalFoundHelpful(10);
        review1.setImages(Arrays.asList("https://example.com/review1.jpg"));

        Review review2 = new Review();
        review2.setReviewId(2L);
        review2.setProductId(1L);
        review2.setStars(4);
        review2.setReviewDate(LocalDate.now().minusDays(1));
        review2.setVerifiedPurchase(true);
        review2.setManufacturerReplied(true);
        review2.setUserId(UUID.randomUUID());
        review2.setTitle("Good Product");
        review2.setReviewText("Works well!");
        review2.setTotalFoundHelpful(5);
        review2.setImages(List.of());

        testReviews.add(review1);
        testReviews.add(review2);

        // Initialize expectedResponseDTO
        expectedResponseDTO = new ProductResponseDTO();
        expectedResponseDTO.setProductId(1L);
        expectedResponseDTO.setName("Test Product");
        expectedResponseDTO.setBrand("Test Brand");
        expectedResponseDTO.setFullDescription("Test Description");
        expectedResponseDTO.setPricing(BigDecimal.valueOf(99.99));
        expectedResponseDTO.setListPrice(BigDecimal.valueOf(129.99));
        expectedResponseDTO.setAvailabilityStatus("In Stock");
        expectedResponseDTO.setProductCategory("Electronics");
        expectedResponseDTO.setProductDimensions("5x5x5");
        expectedResponseDTO.setDateFirstAvailable(LocalDate.now());
        expectedResponseDTO.setManufacturer("Test Manufacturer");
        expectedResponseDTO.setCountryOfOrigin("Test Country");
        expectedResponseDTO.setAverageRating(4.5);
        expectedResponseDTO.setTotalReviews(10);
        expectedResponseDTO.setFiveStarReviews(5);
        expectedResponseDTO.setFourStarReviews(3);
        expectedResponseDTO.setThreeStarReviews(1);
        expectedResponseDTO.setTwoStarReviews(1);
        expectedResponseDTO.setOneStarReviews(0);

        // List of product images for the DTO
        List<ProductImageDTO> imageDTOs = new ArrayList<>();
        ProductImageDTO imageDTO1 = new ProductImageDTO();
        imageDTO1.setImageId(1L);
        imageDTO1.setImageUrl("https://example.com/image1.jpg");

        ProductImageDTO imageDTO2 = new ProductImageDTO();
        imageDTO2.setImageId(2L);
        imageDTO2.setImageUrl("https://example.com/image2.jpg");

        imageDTOs.add(imageDTO1);
        imageDTOs.add(imageDTO2);
        expectedResponseDTO.setProductImages(imageDTOs);

        // List of product features for the DTO
        List<ProductFeatureDTO> featureDTOs = new ArrayList<>();
        ProductFeatureDTO featureDTO1 = new ProductFeatureDTO();
        featureDTO1.setFeatureId(1L);
        featureDTO1.setBullet("Feature 1");

        ProductFeatureDTO featureDTO2 = new ProductFeatureDTO();
        featureDTO2.setFeatureId(2L);
        featureDTO2.setBullet("Feature 2");

        featureDTOs.add(featureDTO1);
        featureDTOs.add(featureDTO2);
        expectedResponseDTO.setFeatureBullets(featureDTOs);

        // List of reviews for the DTO
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        ReviewDTO reviewDTO1 = new ReviewDTO();
        reviewDTO1.setReviewId(1L);
        reviewDTO1.setProductId(1L);
        reviewDTO1.setStars(5);
        reviewDTO1.setReviewDate(LocalDate.now());
        reviewDTO1.setVerifiedPurchase(true);
        reviewDTO1.setManufacturerReplied(false);
        reviewDTO1.setUserId(testReviews.get(0).getUserId());
        reviewDTO1.setTitle("Great Product");
        reviewDTO1.setReviewText("I love this product!");
        reviewDTO1.setTotalFoundHelpful(10);
        reviewDTO1.setImages(Arrays.asList("https://example.com/review1.jpg"));

        ReviewDTO reviewDTO2 = new ReviewDTO();
        reviewDTO2.setReviewId(2L);
        reviewDTO2.setProductId(1L);
        reviewDTO2.setStars(4);
        reviewDTO2.setReviewDate(LocalDate.now().minusDays(1));
        reviewDTO2.setVerifiedPurchase(true);
        reviewDTO2.setManufacturerReplied(true);
        reviewDTO2.setUserId(testReviews.get(1).getUserId());
        reviewDTO2.setTitle("Good Product");
        reviewDTO2.setReviewText("Works well!");
        reviewDTO2.setTotalFoundHelpful(5);
        reviewDTO2.setImages(List.of());

        reviewDTOs.add(reviewDTO1);
        reviewDTOs.add(reviewDTO2);
        expectedResponseDTO.setReviews(reviewDTOs);

        // Initialize productCreateDTO
        productCreateDTO = new ProductCreateDTO();
        productCreateDTO.setName("New Product");
        productCreateDTO.setBrand("New Brand");
        productCreateDTO.setFullDescription("New Description");
        productCreateDTO.setPricing(BigDecimal.valueOf(199.99));
        productCreateDTO.setListPrice(BigDecimal.valueOf(249.99));
        productCreateDTO.setAvailabilityStatus("In Stock");
        productCreateDTO.setProductCategory("Electronics");
        productCreateDTO.setProductDimensions("10x10x10");
        productCreateDTO.setDateFirstAvailable(LocalDate.now());
        productCreateDTO.setManufacturer("New Manufacturer");
        productCreateDTO.setCountryOfOrigin("New Country");
        productCreateDTO.setImageUrls(Arrays.asList("https://example.com/new1.jpg", "https://example.com/new2.jpg"));
        productCreateDTO.setFeatureBullets(Arrays.asList("New Feature 1", "New Feature 2", "New Feature 3"));

        // Initialize productUpdateDTO
        productUpdateDTO = new ProductUpdateDTO();
        productUpdateDTO.setName("Updated Product");
        productUpdateDTO.setBrand("Updated Brand");
        productUpdateDTO.setFullDescription("Updated Description");
        productUpdateDTO.setPricing(BigDecimal.valueOf(149.99));
        productUpdateDTO.setListPrice(BigDecimal.valueOf(199.99));
        productUpdateDTO.setAvailabilityStatus("Low Stock");
        productUpdateDTO.setProductCategory("Updated Electronics");
        productUpdateDTO.setProductDimensions("8x8x8");
        productUpdateDTO.setDateFirstAvailable(LocalDate.now().minusDays(30));
        productUpdateDTO.setManufacturer("Updated Manufacturer");
        productUpdateDTO.setCountryOfOrigin("Updated Country");
        productUpdateDTO.setImageUrls(Arrays.asList("https://example.com/updated1.jpg", "https://example.com/updated2.jpg"));
        productUpdateDTO.setFeatureBullets(Arrays.asList("Updated Feature 1", "Updated Feature 2"));
    }

    @Test
    void getProductById_FromCache_ReturnsProduct() {
        // Arrange
        when(productRedisRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRestTemplateClient.getAllReviews(1L)).thenReturn(testReviews);

        // Act
        ProductResponseDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals("Test Product", result.getName());
        assertEquals("Test Brand", result.getBrand());
        assertEquals(BigDecimal.valueOf(99.99), result.getPricing());
        assertEquals(2, result.getProductImages().size());
        assertEquals(2, result.getFeatureBullets().size());
        assertEquals(2, result.getReviews().size());

        // Verify that Redis was used but not the repository
        verify(productRedisRepository).findById(1L);
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void getProductById_FromRepository_ReturnsProduct() {
        // Arrange
        when(productRedisRepository.findById(1L)).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRestTemplateClient.getAllReviews(1L)).thenReturn(testReviews);

        // Act
        ProductResponseDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals("Test Product", result.getName());
        assertEquals("Test Brand", result.getBrand());
        assertEquals(BigDecimal.valueOf(99.99), result.getPricing());
        assertEquals(2, result.getProductImages().size());
        assertEquals(2, result.getFeatureBullets().size());
        assertEquals(2, result.getReviews().size());

        // Verify that both Redis and repository were used
        verify(productRedisRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(productRedisRepository).save(any(Product.class));
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        // Arrange
        when(productRedisRepository.findById(999L)).thenReturn(Optional.empty());
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            productService.getProductById(999L);
        });

        assertTrue(exception.getMessage().contains("Error retrieving product"));
    }

    @Test
    void createProduct_Success_ReturnsCreatedProduct() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setProductId(2L);
        newProduct.setName("New Product");
        newProduct.setBrand("New Brand");
        newProduct.setPricing(BigDecimal.valueOf(199.99));

        when(productRepository.save(any(Product.class))).thenReturn(newProduct);
        when(productRepository.findById(2L)).thenReturn(Optional.of(testProduct));
        when(reviewRestTemplateClient.getAllReviews(anyLong())).thenReturn(Collections.emptyList());

        // Act
        ProductResponseDTO result = productService.createProduct(productCreateDTO);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productImageRepository, times(1)).saveAll(anyList());
        verify(productFeaturesRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createProduct_Exception_ThrowsResponseStatusException() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            productService.createProduct(productCreateDTO);
        });
    }

    @Test
    void updateProduct_Success_ReturnsUpdatedProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(reviewRestTemplateClient.getAllReviews(1L)).thenReturn(testReviews);

        // Act
        ProductResponseDTO result = productService.updateProduct(1L, productUpdateDTO);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class));

        // Verify cache operations
        verify(productRedisRepository, times(1)).deleteById(1L);
        verify(productRedisRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> {
            productService.updateProduct(999L, productUpdateDTO);
        });
    }

    @Test
    void deleteProduct_Success_ReturnsDeletedInfo() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productImageRepository.countByProduct(testProduct)).thenReturn(2);
        when(productFeaturesRepository.countByProduct(testProduct)).thenReturn(2);
        when(reviewRestTemplateClient.getReviewCount(1L)).thenReturn(2);

        doNothing().when(productImageRepository).deleteByProduct(testProduct);
        doNothing().when(productFeaturesRepository).deleteByProduct(testProduct);
        doNothing().when(reviewRestTemplateClient).deleteAllReviews(1L);

        // Act
        ProductDeletionResponseDTO result = productService.deleteProduct(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals("Test Product", result.getProductName());
        assertEquals(2, result.getImagesDeleted());
        assertEquals(2, result.getFeaturesDeleted());
        assertEquals(2, result.getReviewsDeleted());
        assertNotNull(result.getDeletionTimestamp());
        assertTrue(result.getMessage().contains("successfully deleted"));

        // Verify that delete operations were called
        verify(productRepository).delete(testProduct);
        verify(productRedisRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> {
            productService.deleteProduct(999L);
        });
    }

    @Test
    void searchProductsByKeyword_ReturnsMatchingProducts() {
        // Arrange
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1L, "Test Product", BigDecimal.valueOf(99.99), "https://example.com/image1.jpg"});
        mockResults.add(new Object[]{2L, "Another Test", BigDecimal.valueOf(149.99), "https://example.com/image2.jpg"});

        when(productRepository.searchProductsByKeyword("test")).thenReturn(mockResults);

        // Act
        List<ProductSearchResultDTO> results = productService.searchProductsByKeyword("test");

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(1L, results.get(0).getProductId());
        assertEquals("Test Product", results.get(0).getName());
        assertEquals(BigDecimal.valueOf(99.99), results.get(0).getPricing());
        assertEquals("https://example.com/image1.jpg", results.get(0).getFirstImage());
    }

    @Test
    void searchProductsByKeyword_WithNullKeyword_ReturnsAllProducts() {
        // Arrange
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1L, "Test Product", BigDecimal.valueOf(99.99), "https://example.com/image1.jpg"});
        mockResults.add(new Object[]{2L, "Another Test", BigDecimal.valueOf(149.99), "https://example.com/image2.jpg"});
        mockResults.add(new Object[]{3L, "Third Product", BigDecimal.valueOf(199.99), "https://example.com/image3.jpg"});

        when(productRepository.searchProductsByKeyword(null)).thenReturn(mockResults);

        // Act
        List<ProductSearchResultDTO> results = productService.searchProductsByKeyword(null);

        // Assert
        assertNotNull(results);
        assertEquals(3, results.size());
    }

    @Test
    void searchProductsByKeyword_MappingException_HandlesGracefully() {
        // Arrange - Create a malformed result that will cause mapping error
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1L, "Test Product", BigDecimal.valueOf(99.99), "https://example.com/image1.jpg"});
        mockResults.add(new Object[]{null, null, null, null}); // This will cause exception during mapping

        when(productRepository.searchProductsByKeyword("test")).thenReturn(mockResults);

        // Act
        List<ProductSearchResultDTO> results = productService.searchProductsByKeyword("test");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size()); // Only the valid item should be in the result
    }
}