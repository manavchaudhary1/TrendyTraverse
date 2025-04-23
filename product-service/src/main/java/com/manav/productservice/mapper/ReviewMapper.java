package com.manav.productservice.mapper;

import com.manav.productservice.dto.ReviewDTO;
import com.manav.productservice.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    ReviewDTO toDto(Review review);
    List<ReviewDTO> toDtoList(List<Review> reviews);
    Review toEntity(ReviewDTO dto);
}