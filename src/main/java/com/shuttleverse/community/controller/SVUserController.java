package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.service.SVUserService;
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
}