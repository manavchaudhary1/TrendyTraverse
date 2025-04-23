package com.manav.productservice.events.handler;

import com.manav.productservice.events.model.ReviewChangeModel;
import com.manav.productservice.service.client.ReviewRestTemplateClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.ErrorMessage;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ReviewChangeHandler {

    final ReviewRestTemplateClient reviewRestTemplateClient;


    @PostConstruct
    public void init() {
        log.info("ReviewChangeHandler Bean initialized...");
    }

    @Bean
    public Consumer<ReviewChangeModel> inboundReviewChange() {
        return reviewChangeModel -> {
            log.info("Received a message of type {} with action {} and correlationId {}",
                    reviewChangeModel.getType(), reviewChangeModel.getAction(), reviewChangeModel.getCorrelationId());
            switch (reviewChangeModel.getAction().toUpperCase()) {
                case "SAVE":
                    log.info("Received a SAVE event for Review with ID: {}", reviewChangeModel.getProductId());
                    reviewRestTemplateClient.deleteCacheReviewsList(reviewChangeModel.getProductId());
                    reviewRestTemplateClient.getAllReviews(reviewChangeModel.getProductId());
                    break;
                // UPDATE event is not used because update review is not implemented in the review service module and intend to keep it that way
                case "UPDATE":
                    log.info("Received an UPDATE event for Review with ID: {}", reviewChangeModel.getProductId());
                    break;
                case "DELETE":
                    log.info("Received a DELETE event for Review with ID: {}", reviewChangeModel.getProductId());
                    reviewRestTemplateClient.deleteCacheReviewsList(reviewChangeModel.getProductId());
                    break;
                default:
                    log.error("Received an UNKNOWN event for Review with ID: {}", reviewChangeModel.getProductId());
                    break;
            }
        };
    }

    @Bean
    public Consumer<ErrorMessage> errorHandler() {
        return errorMessage -> log.error("Error in message processing: {}", errorMessage.getPayload(), errorMessage.getPayload());
    }
}
