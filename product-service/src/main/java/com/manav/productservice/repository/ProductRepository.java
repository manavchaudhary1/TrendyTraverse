package com.manav.productservice.repository;

import com.manav.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByProductId(Long productId);

    @Query(value = "SELECT p.product_id, p.name, p.price, " +
            "(SELECT pi.image_url FROM product_images pi WHERE pi.product_id = p.product_id LIMIT 1) as first_image " +
            "FROM products p " +
            "WHERE (:keyword IS NULL OR " +
            "to_tsvector('english', p.name) @@ plainto_tsquery('english', :keyword) OR " +
            "to_tsvector('english', p.brand) @@ plainto_tsquery('english', :keyword) OR " +
            "p.product_category ILIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    List<Object[]> searchProductsByKeyword(@Param("keyword") String keyword);
 }
