package com.manav.reviewservice.events.source;

import com.manav.reviewservice.events.model.ReviewChangeModel;
import com.manav.reviewservice.utils.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.function.context.PollableBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class ReviewSourceBean {
    private final BlockingQueue<ReviewChangeModel> messageQueue = new LinkedBlockingQueue<>();

    final KafkaTemplate<String, ReviewChangeModel> kafkaTemplate;

    public ReviewSourceBean(KafkaTemplate<String, ReviewChangeModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @PollableBean
    public Supplier<ReviewChangeModel> supplyReviewChangeModel() {
        return () -> {
            try{
                ReviewChangeModel message = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                if(message != null) {
                    log.info("Sending kafka message for Product Id: {}", message.getProductId());
                    return message;
                }
                return null;
            } catch (InterruptedException e) {
                log.error("Review change model was interrupted", e);
                Thread.currentThread().interrupt();
                return null;
            }
        };
    }

    public void publishReviewChange(String action, Long productId) {
        log.debug("Queuing Kafka message {} for Review Id: {}", action, productId);
        ReviewChangeModel changeModel = new ReviewChangeModel(
                ReviewChangeModel.class.getTypeName(),
                action,
                productId,
                UserContextHolder.getContext().getCorrelationId()
        );
        boolean offered = messageQueue.offer(changeModel);
        if(offered){
            log.info("Kafka message queued for Product Id: {}", productId);
        } else {
            log.error("Kafka message queuing failed for Review Id: {}. Queue might be full.", productId);
        }
    }
}
