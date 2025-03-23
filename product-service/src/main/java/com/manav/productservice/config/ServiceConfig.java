package com.manav.productservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
@Getter
@Setter
@Slf4j
public class ServiceConfig {

    @Value("${spring.data.redis.host}")
    private String redisServer;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @PostConstruct
    public void logRedisConfig() {
        if (redisServer == null || redisServer.isBlank()) {
            log.error("Redis host is not configured!");
            return;
        }

        log.info("Connecting to Redis at {}:{}", redisServer, redisPort);
        try (Jedis jedis = new Jedis(redisServer, redisPort)) {
            String response = jedis.ping();
            log.info("Redis Connection Test: {}", response);
        } catch (Exception e) {
            log.error("Redis Connection Failed!", e);
        }
    }

}
