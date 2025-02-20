package com.manav.productservice.repository;

import com.manav.productservice.model.Product;
import com.manav.productservice.model.ProductFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductFeaturesRepository extends JpaRepository<ProductFeatures, Long> {
    List<ProductFeatures> findByProduct(Product product);
    void deleteByProduct(Product product);
    int countByProduct(Product product);
}
