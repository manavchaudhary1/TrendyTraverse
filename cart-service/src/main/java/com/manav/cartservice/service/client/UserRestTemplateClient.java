package com.manav.cartservice.service.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class UserRestTemplateClient {

    private final RestTemplate restTemplate;

    public UserRestTemplateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean approveUser(String userName, String uuid) {
        ResponseEntity<Boolean> response = restTemplate.exchange(
                "http://localhost:8072/user-service/users/validate?userName=" + userName + "&uuid=" + uuid,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        return Boolean.TRUE.equals(response.getBody());
    }
}
