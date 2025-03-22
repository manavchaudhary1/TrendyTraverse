package com.manav.gatewayserver.filters;

import brave.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseFilter {

    final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    final FilterUtils filterUtils;
    private final Tracer tracer;

    public ResponseFilter(FilterUtils filterUtils, Tracer tracer) {
        this.filterUtils = filterUtils;
        this.tracer = tracer;
    }

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
            String correlationId = filterUtils.getCorrelationId(requestHeaders);

            // Get or generate trace ID
            String traceId;
            try {
                brave.Span currentSpan = tracer.currentSpan();
                traceId = currentSpan != null ? currentSpan.context().traceIdString() : generateTraceId();
            } catch (Exception e) {
                logger.warn("Error getting trace ID from tracer: {}", e.getMessage());
                traceId = generateTraceId();
            }

            logger.debug("Adding the trace id to the outbound headers. {}", traceId);
            exchange.getResponse().getHeaders().add(FilterUtils.TRACE_ID, traceId);
            logger.debug("Adding the correlation id to the outbound headers. {}", correlationId);
            exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);
            logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
        }));
    }

    private String generateTraceId() {
        return java.util.UUID.randomUUID().toString();
    }
}