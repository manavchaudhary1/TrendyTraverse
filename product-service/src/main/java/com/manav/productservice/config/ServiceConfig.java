package com.manav.productservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ServiceConfig {

    @Value("${redis.server.host}")
    private String redisServer;

    @Value("${redis.server.port}")
    private String redisPort;
}
