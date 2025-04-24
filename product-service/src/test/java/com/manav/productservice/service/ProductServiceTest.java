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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.Mockito.lenient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductCreateDTO productCreateDTO;
    private ProductUpdateDTO productUpdateDTO;
    private List<Review> testReviews;

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
        List<ProductImage> testImages = new ArrayList<>();
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
        List<ProductFeatures> testFeatures = new ArrayList<>();
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
        review1.setImages(List.of("https://example.com/review1.jpg"));

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

        // Initialize image DTOs
        List<ProductImageDTO> imageDTOs = new ArrayList<>();
        imageDTOs.add(new ProductImageDTO(1L, "https://example.com/image1.jpg"));
        imageDTOs.add(new ProductImageDTO(2L, "https://example.com/image2.jpg"));

        // Initialize feature DTOs
        List<ProductFeatureDTO> featureDTOs = new ArrayList<>();
        featureDTOs.add(new ProductFeatureDTO(1L, "Feature 1"));
        featureDTOs.add(new ProductFeatureDTO(2L, "Feature 2"));

        // Initialize review DTOs
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        reviewDTOs.add(new ReviewDTO(
                1L,
                1L,
                5,
                LocalDate.now(),
                true,
                false,
                testReviews.get(0).getUserId(),
                "Great Product",
                "I love this product!",
                10,
                List.of("https://example.com/review1.jpg")
        ));

        reviewDTOs.add(new ReviewDTO(
                2L,
                1L,
                4,
                LocalDate.now().minusDays(1),
                true,
                true,
                testReviews.get(1).getUserId(),
                "Good Product",
                "Works well!",
                5,
                List.of()
        ));

        // Initialize expectedResponseDTO using record constructor
        ProductResponseDTO expectedResponseDTO = new ProductResponseDTO(
                1L,
                "Test Product",
                "Test Brand",
                imageDTOs,
                "Test Description",
                featureDTOs,
                BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(129.99),
                "In Stock",
                "Electronics",
                "5x5x5",
                LocalDate.now(),
                "Test Manufacturer",
                "Test Country",
                4.5,
                10,
                5,
                3,
                1,
                1,
                0,
                reviewDTOs
        );

        // Initialize productCreateDTO using record constructor
        productCreateDTO = new ProductCreateDTO(
                "New Product",
                "New Brand",
                "New Description",
                BigDecimal.valueOf(199.99),
                BigDecimal.valueOf(249.99),
                "In Stock",
                "Electronics",
                "10x10x10",
                LocalDate.now(),
                "New Manufacturer",
                "New Country",
                Arrays.asList("https://example.com/new1.jpg", "https://example.com/new2.jpg"),
                Arrays.asList("New Feature 1", "New Feature 2", "New Feature 3")
        );

        // Initialize productUpdateDTO using record constructor
        productUpdateDTO = new ProductUpdateDTO(
                "Updated Product",
                "Updated Brand",
                "Updated Description",
                BigDecimal.valueOf(149.99),
                BigDecimal.valueOf(199.99),
                "Low Stock",
                "Updated Electronics",
                "8x8x8",
                LocalDate.now().minusDays(30),
                "Updated Manufacturer",
                "Updated Country",
                Arrays.asList("https://example.com/updated1.jpg", "https://example.com/updated2.jpg"),
                Arrays.asList("Updated Feature 1", "Updated Feature 2")
        );

        // Setup basic mappings for the mapper mocks
        lenient().when(productMapper.toResponseDTO(any(Product.class))).thenReturn(expectedResponseDTO);
        lenient().when(productMapper.toEntity(any(ProductCreateDTO.class))).thenReturn(testProduct);
        lenient().when(reviewMapper.toDtoList(anyList())).thenReturn(reviewDTOs);

        // Setup image and feature mapping
        for (ProductImage image : testImages) {
            ProductImageDTO imageDTO = new ProductImageDTO(Long.valueOf(image.getImageId()), image.getImageUrl());
            lenient().when(productMapper.toImageDTO(image)).thenReturn(imageDTO);
        }

        for (ProductFeatures feature : testFeatures) {
            ProductFeatureDTO featureDTO = new ProductFeatureDTO(Long.valueOf(feature.getFeatureId()), feature.getBullet());
            lenient().when(productMapper.toFeatureDTO(feature)).thenReturn(featureDTO);
        }
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
        assertEquals(1L, result.productId());
        assertEquals("Test Product", result.name());
        assertEquals("Test Brand", result.brand());
        assertEquals(BigDecimal.valueOf(99.99), result.pricing());
        assertEquals(2, result.productImages().size());
        assertEquals(2, result.featureBullets().size());
        assertEquals(2, result.reviews().size());

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
        assertEquals(1L, result.productId());
        assertEquals("Test Product", result.name());
        assertEquals("Test Brand", result.brand());
        assertEquals(BigDecimal.valueOf(99.99), result.pricing());
        assertEquals(2, result.productImages().size());
        assertEquals(2, result.featureBullets().size());
        assertEquals(2, result.reviews().size());

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
        CustomException exception = assertThrows(CustomException.class, () -> productService.getProductById(999L));

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
        assertThrows(ResponseStatusException.class, () -> productService.createProduct(productCreateDTO));
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
        assertEquals(1L, result.productId());
        assertEquals("Test Product", result.productName());
        assertEquals(2, result.imagesDeleted());
        assertEquals(2, result.featuresDeleted());
        assertEquals(2, result.reviewsDeleted());
        assertNotNull(result.deletionTimestamp());
        assertTrue(result.message().contains("successfully deleted"));

        // Verify that delete operations were called
        verify(productRepository).delete(testProduct);
        verify(productRedisRepository).deleteById(1L);
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
        assertEquals(1L, results.getFirst().productId());
        assertEquals("Test Product", results.getFirst().name());
        assertEquals(BigDecimal.valueOf(99.99), results.getFirst().pricing());
        assertEquals("https://example.com/image1.jpg", results.getFirst().firstImage());
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
}