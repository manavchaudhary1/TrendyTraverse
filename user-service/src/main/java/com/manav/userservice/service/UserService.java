package com.manav.userservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manav.userservice.dto.UserDto;
import com.manav.userservice.dto.UserKeycloakDTO;
import com.manav.userservice.exception.*;
import com.manav.userservice.model.User;
import com.manav.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UsersResource usersResource;
    private final RealmResource realmResource;
    private final ClientResource clientResource;
    private final UserRepository userRepository;

    public UserService(UsersResource usersResource, RealmResource realmResource, ClientResource clientResource, UserRepository userRepository) {
        this.usersResource = usersResource;
        this.realmResource = realmResource;
        this.clientResource = clientResource;
        this.userRepository = userRepository;
    }
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${keycloak.server-url}")
    public String serverUrl;

    @Value("${keycloak.realm}")
    public String realm;

    @Value("${keycloak.client-id}")
    public String clientId;

    @Value("${keycloak.client-secret}")
    public String clientSecret;

    @Value("${keycloak.group-id}")
    public String groupId;

    @Value("${keycloak.admin-group-id}")
    public String adminGroupId;

    @Transactional
    public void addUser(UserKeycloakDTO user) {
        // Throw specific exceptions based on existence
        if (isUsernameExists(user.getUsername())) {
            throw new UserAlreadyExistsException("User with the same username already exists.");
        }

        if (isEmailExists(user.getEmail())) {
            throw new UserAlreadyExistsException("User with the same email already exists.");
        }

        // Proceed with user creation
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUsername());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(true);
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));

        Response response = usersResource.create(kcUser);
        if (response.getStatus() == 201) { // HTTP 201 Created
            String userId = extractUserId(response);
            if (userId != null) {
                if (assignClientRole(userId, "customer") && assignUserToGroup(userId)) {
                    log.info("User {} created, role assigned, and group {} joined successfully!", userId, groupId);
                } else {
                    log.error("Failed to assign role or joining group deleting user {}...", userId);
                    usersResource.get(userId).remove(); // Rollback user creation
                    throw new RoleAssignmentException("Failed to assign role or join group, user creation rolled back.");
                }
            }
        } else {
            throw new UserCreationException("Failed to create user: " + response.getStatus());
        }
        try {
//            // Save user in the database
            UserDto userDto = new UserDto();
            userDto.setId(UUID.randomUUID());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());

            userRepository.save(mapToUser(userDto));
        }catch (Exception e) {
            log.error("Error occurred, rolling back user creation: {}", e.getMessage());
            String userId = extractUserId(response);
            if (userId != null) usersResource.get(userId).remove(); // Rollback user from Keycloak
            throw new UserCreationException("Failed to create user: " + e.getMessage());
        }
    }


    private boolean assignClientRole(String userId, String roleName) {
        try {// Use actual client ID
            String clientUuid = realmResource.clients().findByClientId(clientId).getFirst().getId();

            // Check if the role exists
            List<RoleRepresentation> clientRoles = realmResource.clients().get(clientUuid).roles().list();
            RoleRepresentation role = clientRoles.stream()
                    .filter(r -> roleName.equals(r.getName()))
                    .findFirst()
                    .orElse(null);

            if (role == null) {
                log.error("Role '{}' not found in client.", roleName);
                return false;
            }

            // Check if the user already has the role
            List<RoleRepresentation> assignedRoles = usersResource.get(userId).roles().clientLevel(clientUuid).listAll();
            boolean alreadyAssigned = assignedRoles.stream().anyMatch(r -> roleName.equals(r.getName()));

            if (!alreadyAssigned) {
                usersResource.get(userId).roles().clientLevel(clientUuid).add(Collections.singletonList(role));
                log.info("Role '{}' assigned to user {}", roleName, userId);
            } else {
                log.info("User already has role '{}'.", roleName);
            }
            return true;
        } catch (Exception e) {
            log.error("Error assigning role: {}", e.getMessage());
            return false;
        }
    }

    private boolean assignUserToGroup(String userId) {
        try {
            usersResource.get(userId).joinGroup(groupId);
            return true; // Successfully assigned
        } catch (Exception e) {
            log.error("Failed to assign user {} to group {}", userId, groupId);
            return false; // Assignment failed
        }
    }

    public boolean assignUserToAdmin(String userName) {
        try{
            String userId = usersResource.search(userName, true).getFirst().getId();
            usersResource.get(userId).joinGroup(adminGroupId);
            return true;
        } catch (Exception e) {
            log.error("Failed to assign user {} to admin group {}", userName, adminGroupId);
            return false;
        }
    }

    private User mapToUser(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        return user;
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private String extractUserId(Response response) {
        String location = response.getHeaderString("Location"); // Get user location from response
        if (location != null) {
            return location.substring(location.lastIndexOf("/") + 1); // Extract user ID from URL
        }
        return null;
    }

    private boolean isUsernameExists(String username) {
        List<UserRepresentation> existingUserName = usersResource.search(username, true);
        return existingUserName.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    private boolean isEmailExists(String email) {
        List<UserRepresentation> existingEmail = usersResource.searchByEmail(email, true);
        return existingEmail.stream()
                .anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email));
    }

    public Map<String, Object> getToken(String username, String password) {
        try {
            Map<String, Object> tokens = WebClient.create().post()
                    .uri(serverUrl + "/realms/{realm}/protocol/openid-connect/token", realm)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "password")
                            .with("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("username", username)
                            .with("password", password))
                    .retrieve()
                    .bodyToMono(String.class)
                    .mapNotNull(this::extractAccessToken)
                    .block();

            // Update last_login
            userRepository.findByUsername(username).ifPresent(user -> {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
            });

            return tokens;
        } catch (WebClientResponseException.Unauthorized e) {
            throw new InvalidCredentialsException("Invalid username or password.");
        } catch (WebClientResponseException e) {
            throw new KeycloakConnectionException("Error while fetching token: " + e.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while fetching token");
        }
    }

    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            return WebClient.create()
                    .post()
                    .uri(serverUrl + "/realms/{realm}/protocol/openid-connect/token", realm)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                            .with("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("refresh_token", refreshToken))
                    .retrieve()
                    .bodyToMono(String.class)
                    .mapNotNull(this::extractAccessToken)
                    .block();
        } catch (WebClientResponseException.Unauthorized e) {
            throw new InvalidCredentialsException("Invalid refresh token.");
        } catch (WebClientResponseException e) {
            throw new KeycloakConnectionException("Error while refreshing token: " + e.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while refreshing token");
        }
    }


    private Map<String, Object> extractAccessToken(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            Map<String, Object> tokens = new HashMap<>();
            tokens.put("access_token", jsonNode.get("access_token").asText());
            tokens.put("expires_in", jsonNode.get("expires_in").asInt(0));
            tokens.put("refresh_token", jsonNode.get("refresh_token").asText());
            tokens.put("refresh_expires_in", jsonNode.get("refresh_expires_in").asInt(0));
            return tokens;
        } catch (Exception e) {
            log.error("Error extracting tokens");
            return Collections.emptyMap();
        }
    }

    public boolean validateUsername(String username, UUID uuid) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .map(uuid::equals)  // Compare directly with UUID
                .orElse(false);      // Return false if the user is not found
    }
}
