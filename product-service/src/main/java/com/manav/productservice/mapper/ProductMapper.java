package com.manav.productservice.mapper;

import com.manav.productservice.dto.*;
import com.manav.productservice.model.Product;
import com.manav.productservice.model.ProductFeatures;
import com.manav.productservice.model.ProductImage;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "productImages", ignore = true)
    @Mapping(target = "featureBullets", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    ProductResponseDTO toResponseDTO(Product product);

    ProductFeatureDTO toFeatureDTO(ProductFeatures feature);

    ProductImageDTO toImageDTO(ProductImage image);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "productImages", ignore = true)
    @Mapping(target = "featureBullets", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "fiveStarReviews", ignore = true)
    @Mapping(target = "fourStarReviews", ignore = true)
    @Mapping(target = "threeStarReviews", ignore = true)
    @Mapping(target = "twoStarReviews", ignore = true)
    @Mapping(target = "oneStarReviews", ignore = true)
    Product toEntity(ProductCreateDTO dto);

    @Named("productImageFromUrl")
    default ProductImage productImageFromUrl(String url, @Context Product product) {
        if (url == null) return null;
        ProductImage image = new ProductImage();
        image.setImageUrl(url);
        image.setProduct(product);
        return image;
    }

    @Named("productFeatureFromBullet")
    default ProductFeatures productFeatureFromBullet(String bullet, @Context Product product) {
        if (bullet == null) return null;
        ProductFeatures feature = new ProductFeatures();
        feature.setBullet(bullet);
        feature.setProduct(product);
        return feature;
    }

    @IterableMapping(qualifiedByName = "productImageFromUrl")
    List<ProductImage> toProductImageList(List<String> imageUrls, @Context Product product);

    @IterableMapping(qualifiedByName = "productFeatureFromBullet")
    List<ProductFeatures> toProductFeaturesList(List<String> featureBullets, @Context Product product);

    // Method to update Product entity from ProductUpdateDTO
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "productImages", ignore = true)
    @Mapping(target = "featureBullets", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "fiveStarReviews", ignore = true)
    @Mapping(target = "fourStarReviews", ignore = true)
    @Mapping(target = "threeStarReviews", ignore = true)
    @Mapping(target = "twoStarReviews", ignore = true)
    @Mapping(target = "oneStarReviews", ignore = true)
    void updateProductFromDTO(ProductUpdateDTO dto, @MappingTarget Product product);
}