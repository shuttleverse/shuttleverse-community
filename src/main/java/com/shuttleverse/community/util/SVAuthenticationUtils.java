package com.shuttleverse.community.util;

import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.service.SVUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SVAuthenticationUtils {

  private static SVUserService userService;

  @Autowired
  public void setUserService(SVUserService userService) {
    SVAuthenticationUtils.userService = userService;
  }

  public static SVUser getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
      String sub = jwt.getSubject();
      return userService.findBySub(sub)
          .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
    throw new SecurityException("No authenticated user found");
  }
}
