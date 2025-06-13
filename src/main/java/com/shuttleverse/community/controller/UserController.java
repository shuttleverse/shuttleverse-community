package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<User>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    Optional<User> user = userService.findBySub(sub);

    return user.map(value -> ResponseEntity.ok(ApiResponse.success(value)))
        .orElseGet(() -> ResponseEntity.ok(ApiResponse.success("User Not Found", null)));
  }

  @PostMapping("/me")
  public ResponseEntity<ApiResponse<User>> updateProfile(@AuthenticationPrincipal Jwt jwt,
      @RequestBody User userDetails) {
    String sub = jwt.getSubject();
    String email = jwt.getClaimAsString("email");

    if (userDetails.getUsername() == null || userDetails.getUsername().trim().isEmpty()) {
      return ResponseEntity.badRequest().body(ApiResponse.error("Username is required"));
    }

    try {
      User user = userService.createOrUpdateUser(sub, email, userDetails);
      user.setBio(userDetails.getBio());
      userService.save(user);
      return ResponseEntity.ok(ApiResponse.success(user));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("Failed to create user: " + e.getMessage()));
    }
  }
}