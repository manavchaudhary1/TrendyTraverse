package com.manav.productservice.controller;

import com.manav.productservice.dto.*;
import com.manav.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductResponseDTO productResponseDTO;
    private ProductCreateDTO productCreateDTO;
    private ProductUpdateDTO productUpdateDTO;
    private ProductDeletionResponseDTO productDeletionResponseDTO;
    private List<ProductSearchResultDTO> searchResults;

    @BeforeEach
    void setUp() {
        // Initialize test DTOs
        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setProductId(1L);
        productResponseDTO.setName("Test Product");
        productResponseDTO.setBrand("Test Brand");
        productResponseDTO.setPricing(BigDecimal.valueOf(99.99));
        productResponseDTO.setProductImages(new ArrayList<>());
        productResponseDTO.setFeatureBullets(new ArrayList<>());

        productCreateDTO = new ProductCreateDTO();
        productCreateDTO.setName("New Product");
        productCreateDTO.setBrand("New Brand");
        productCreateDTO.setPricing(BigDecimal.valueOf(199.99));
        productCreateDTO.setAvailabilityStatus("In Stock");

        productUpdateDTO = new ProductUpdateDTO();
        productUpdateDTO.setName("Updated Product");
        productUpdateDTO.setBrand("Updated Brand");
        productUpdateDTO.setPricing(BigDecimal.valueOf(149.99));

        productDeletionResponseDTO = new ProductDeletionResponseDTO();
        productDeletionResponseDTO.setProductId(1L);
        productDeletionResponseDTO.setProductName("Test Product");
        productDeletionResponseDTO.setImagesDeleted(2);
        productDeletionResponseDTO.setFeaturesDeleted(3);
        productDeletionResponseDTO.setReviewsDeleted(5);
        productDeletionResponseDTO.setDeletionTimestamp(LocalDateTime.now());
        productDeletionResponseDTO.setMessage("Product successfully deleted");

        searchResults = new ArrayList<>();
        ProductSearchResultDTO searchResult1 = new ProductSearchResultDTO();
        searchResult1.setProductId(1L);
        searchResult1.setName("Test Product");
        searchResult1.setPricing(BigDecimal.valueOf(99.99));
        searchResult1.setFirstImage("https://example.com/image1.jpg");

        ProductSearchResultDTO searchResult2 = new ProductSearchResultDTO();
        searchResult2.setProductId(2L);
        searchResult2.setName("Another Test");
        searchResult2.setPricing(BigDecimal.valueOf(149.99));
        searchResult2.setFirstImage("https://example.com/image2.jpg");

        searchResults.add(searchResult1);
        searchResults.add(searchResult2);
    }

    @Test
    void getProductById_ReturnsProduct() {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(productResponseDTO);

        // Act
        ResponseEntity<ProductResponseDTO> response = productController.getProductById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productResponseDTO, response.getBody());
        verify(productService).getProductById(1L);
    }

    @Test
    void createProduct_ReturnsCreatedProduct() {
        // Arrange
        when(productService.createProduct(productCreateDTO)).thenReturn(productResponseDTO);

        // Act
        ResponseEntity<ProductResponseDTO> response = productController.createProduct(productCreateDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(productResponseDTO, response.getBody());
        verify(productService).createProduct(productCreateDTO);
    }

    @Test
    void updateProduct_ReturnsUpdatedProduct() {
        // Arrange
        Long productId = 1L;
        when(productService.updateProduct(productId, productUpdateDTO)).thenReturn(productResponseDTO);

        // Act
        ResponseEntity<ProductResponseDTO> response = productController.updateProduct(productId, productUpdateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productResponseDTO, response.getBody());
        verify(productService).updateProduct(productId, productUpdateDTO);
    }

    @Test
    void deleteProduct_ReturnsDeletedProductInfo() {
        // Arrange
        Long productId = 1L;
        when(productService.deleteProduct(productId)).thenReturn(productDeletionResponseDTO);

        // Act
        ResponseEntity<ProductDeletionResponseDTO> response = productController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDeletionResponseDTO, response.getBody());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getProductId());
        assertEquals("Product successfully deleted", response.getBody().getMessage());
        verify(productService).deleteProduct(productId);
    }

    @Test
    void searchProducts_WithKeyword_ReturnsSearchResults() {
        // Arrange
        String keyword = "test";
        when(productService.searchProductsByKeyword(keyword)).thenReturn(searchResults);

        // Act
        ResponseEntity<List<ProductSearchResultDTO>> response = productController.searchProducts(keyword);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(searchResults, response.getBody());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(productService).searchProductsByKeyword(keyword);
    }

    @Test
    void searchProducts_WithNullKeyword_ReturnsAllProducts() {
        // Arrange
        when(productService.searchProductsByKeyword(null)).thenReturn(searchResults);

        // Act
        ResponseEntity<List<ProductSearchResultDTO>> response = productController.searchProducts(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(searchResults, response.getBody());
        verify(productService).searchProductsByKeyword(null);
    }

    @Test
    void searchProducts_WithEmptyKeyword_ReturnsAllProducts() {
        // Arrange
        String keyword = "";
        when(productService.searchProductsByKeyword(keyword)).thenReturn(searchResults);

        // Act
        ResponseEntity<List<ProductSearchResultDTO>> response = productController.searchProducts(keyword);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(searchResults, response.getBody());
        verify(productService).searchProductsByKeyword(keyword);
    }
}