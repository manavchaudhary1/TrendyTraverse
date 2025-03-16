package com.manav.reviewservice.utils;


import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserContext {
    public static final String CORRELATION_ID = "tmx-correlation-id";
    private String correlationId;



    public String getCorrelationId() {
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString(); // Generate if missing
        }
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CORRELATION_ID, getCorrelationId());
        return headers;
    }
}
