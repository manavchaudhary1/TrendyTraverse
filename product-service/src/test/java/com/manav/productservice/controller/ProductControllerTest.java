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
import java.time.LocalDate;
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
        // Initialize test DTOs using record constructors
        productResponseDTO = new ProductResponseDTO(
                1L,
                "Test Product",
                "Test Brand",
                new ArrayList<>(),  // productImages
                "Test Description",
                new ArrayList<>(),  // featureBullets
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
                new ArrayList<>()  // reviews
        );

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
                List.of("https://example.com/image1.jpg"),
                List.of("Feature 1", "Feature 2")
        );

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
                List.of("https://example.com/updated1.jpg"),
                List.of("Updated Feature 1", "Updated Feature 2")
        );

        productDeletionResponseDTO = new ProductDeletionResponseDTO(
                1L,
                "Test Product",
                2,
                3,
                5,
                LocalDateTime.now(),
                "Product successfully deleted"
        );

        searchResults = new ArrayList<>();
        searchResults.add(new ProductSearchResultDTO(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                "https://example.com/image1.jpg"
        ));

        searchResults.add(new ProductSearchResultDTO(
                2L,
                "Another Test",
                BigDecimal.valueOf(149.99),
                "https://example.com/image2.jpg"
        ));
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
        assertEquals(productId, response.getBody().productId());
        assertEquals("Product successfully deleted", response.getBody().message());
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