package com.manav.userservice;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @RestController
    @RequestMapping("/test")
    public class TestController {
        private final Keycloak keycloak;

        public TestController(Keycloak keycloak) {
            this.keycloak = keycloak;
        }

        @GetMapping
        public String testKeycloak() {
            try {
                // Try a simple operation
                int count = keycloak.realm("master").users().count();
                return "Success! User count: " + count;
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
    }
}
