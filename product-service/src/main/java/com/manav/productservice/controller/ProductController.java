package com.manav.productservice.controller;


import com.manav.productservice.dto.*;
import com.manav.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    public final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateDTO productUpdateDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(productId, productUpdateDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDeletionResponseDTO> deleteProduct(@PathVariable Long productId) {
        ProductDeletionResponseDTO deletionResponse = productService.deleteProduct(productId);
        return ResponseEntity.ok(deletionResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductSearchResultDTO>> searchProducts(
            @RequestParam(required = false) String keyword) {

        List<ProductSearchResultDTO> searchResults = productService.searchProductsByKeyword(keyword);
        return ResponseEntity.ok(searchResults);
    }
}
