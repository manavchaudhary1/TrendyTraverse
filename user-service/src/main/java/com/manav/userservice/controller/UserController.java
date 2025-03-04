package com.manav.userservice.controller;

import com.manav.userservice.dto.RefreshTokenRequestDto;
import com.manav.userservice.dto.UserKeycloakDTO;
import com.manav.userservice.exception.RoleAssignmentException;
import com.manav.userservice.exception.UserAlreadyExistsException;
import com.manav.userservice.exception.UserCreationException;
import com.manav.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserKeycloakDTO user) {
        try {
            userService.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("message", "User created successfully"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (RoleAssignmentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred"));
        }
    }

    @GetMapping("/token")
    public Map<String, Object> getToken(@RequestBody UserKeycloakDTO user) {
        return userService.getToken(user.getUsername(), user.getPassword());
    }

    @GetMapping("/refresh-token")
    public Map<String, Object> refreshAccessToken(@RequestBody RefreshTokenRequestDto refreshTokenRequest) {
        return userService.refreshToken(refreshTokenRequest.getRefreshToken());
    }


    @PostMapping("/promote/{userName}")
    public String promoteUser(@PathVariable String userName){
        Boolean response = userService.assignUserToAdmin(userName);
        if (Boolean.TRUE.equals(response)){
            return ("User "+ userName +" promoted to admin");
        }else {
            return ("User "+ userName +" not promoted to admin");
        }
    }
}
