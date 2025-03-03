package com.manav.userservice.config;

import com.manav.userservice.exception.KeycloakConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.resource.ClientResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@Slf4j
public class KeycloakConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloak() {
        try {
            Keycloak kc = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
                    .build();
            kc.serverInfo().getInfo();
            log.info("Keycloak connection successful!");
            return kc;
        } catch (Exception e) {
            log.error("Failed to connect to Keycloak");
            throw new KeycloakConnectionException("Could not initialize Keycloak client", e);
        }
    }

// Alternate way to connect to Keycloak
//    @Bean
//    public Keycloak keycloak() {
//        System.out.println("Connecting to Keycloak at: " + serverUrl);
//        System.out.println("Using realm: " + realm);
//        System.out.println("Using admin username: " + adminUsername);
//
//        try {
//            Keycloak kc = Keycloak.getInstance(
//                    serverUrl,
//                    "master",
//                    adminUsername,
//                    adminPassword,
//                    "admin-cli"
//            );
//            // Test the connection
//            kc.serverInfo().getInfo();
//            System.out.println("Keycloak connection successful!");
//            printAllRoles(kc);
//            return kc;
//        } catch (Exception e) {
//            System.err.println("Keycloak connection failed: " + e.getMessage());
//            e.printStackTrace();
//            throw e;
//        }
//    }
//
//    Print all roles in Keycloak
//    private void printAllRoles(Keycloak keycloak) {
//        try {
//            List<ClientRepresentation> clients = keycloak.realm("user-realm").clients().findByClientId("manav");
//
//            if (clients.isEmpty()) {
//                System.err.println("Client not found: " + "manav");
//                return;
//            }
//
//            String clientUuid = clients.get(0).getId();
//            List<String> roles = keycloak.realm("user-realm")
//                    .clients()
//                    .get(clientUuid)
//                    .roles()
//                    .list()
//                    .stream()
//                    .map(RoleRepresentation::getName)
//                    .collect(Collectors.toList());
//
//            System.out.println("Available roles in Keycloak:");
//            roles.forEach(System.out::println);
//        } catch (Exception e) {
//            System.err.println("Error fetching roles: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    @Bean
    public RealmResource realmResource(Keycloak keycloak) {
        return keycloak.realm(realm);
    }

    @Bean
    public UsersResource usersResource(RealmResource realmResource) {
        return realmResource.users();
    }

    @Bean
    public ClientResource clientResource(RealmResource realmResource) {
        return realmResource.clients().get(clientId);
    }
}
