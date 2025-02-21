package com.manav.configserver;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import static io.github.cdimascio.dotenv.Dotenv.load;

@SpringBootApplication
@EnableConfigServer
@RefreshScope
public class ConfigserverApplication {

    public static void main(String[] args)
    {
        Dotenv dotenv = load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        SpringApplication.run(ConfigserverApplication.class, args);
    }

}