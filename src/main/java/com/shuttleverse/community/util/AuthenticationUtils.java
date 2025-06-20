package com.shuttleverse.community.util;

import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {

  private static UserService userService;

  @Autowired
  public void setUserService(UserService userService) {
    AuthenticationUtils.userService = userService;
  }

  public static User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
      String sub = jwt.getSubject();
      return userService.findBySub(sub)
          .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
    throw new SecurityException("No authenticated user found");
  }
}
