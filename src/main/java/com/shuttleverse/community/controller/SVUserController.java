package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.service.SVUserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SVUserController {

  private final SVUserService userService;

  @GetMapping("/me")
  public ResponseEntity<SVApiResponse<SVUser>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    Optional<SVUser> user = userService.findBySub(sub);

    return user.map(value -> ResponseEntity.ok(SVApiResponse.success(value)))
        .orElseGet(() -> ResponseEntity.ok(SVApiResponse.success("User Not Found", null)));
  }

  @PostMapping("/me")
  public ResponseEntity<SVApiResponse<SVUser>> updateProfile(@AuthenticationPrincipal Jwt jwt,
      @RequestBody SVUser userDetails) {
    String sub = jwt.getSubject();
    String email = jwt.getClaimAsString("email");

    if (userDetails.getUsername() == null || userDetails.getUsername().trim().isEmpty()) {
      return ResponseEntity.badRequest().body(SVApiResponse.error("Username is required"));
    }

    try {
      SVUser user = userService.createOrUpdateUser(sub, email, userDetails);
      user.setBio(userDetails.getBio());
      userService.save(user);
      return ResponseEntity.ok(SVApiResponse.success(user));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(SVApiResponse.error("Failed to create user: " + e.getMessage()));
    }
  }

  @GetMapping("/{userId}")
  public ResponseEntity<SVApiResponse<SVUser>> getUserById(@PathVariable String userId) {
    try {
      UUID userUuid = UUID.fromString(userId);
      Optional<SVUser> user = userService.findById(userUuid);
      return user.map(value -> ResponseEntity.ok(SVApiResponse.success(value)))
          .orElseGet(() -> ResponseEntity.ok(SVApiResponse.success("User Not Found", null)));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(SVApiResponse.error("Invalid user ID format: " + userId));
    }
  }

  @PostMapping("/batch")
  public ResponseEntity<SVApiResponse<List<SVUser>>> getUsersByIds(
      @RequestBody List<String> userIds) {
    try {
      List<UUID> userUuids = userIds.stream()
          .map(UUID::fromString)
          .collect(Collectors.toList());

      List<SVUser> users = userService.findByIds(userUuids);
      return ResponseEntity.ok(SVApiResponse.success(users));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(SVApiResponse.error("Invalid user ID format in batch request"));
    }
  }

  @GetMapping("/search")
  public ResponseEntity<SVApiResponse<List<SVUser>>> searchUsers(
      @org.springframework.web.bind.annotation.RequestParam String query) {
    if (query == null || query.trim().isEmpty()) {
      return ResponseEntity.ok(SVApiResponse.success(java.util.Collections.emptyList()));
    }
    List<SVUser> users = userService.searchByUsername(query);
    return ResponseEntity.ok(SVApiResponse.success(users));
  }
}