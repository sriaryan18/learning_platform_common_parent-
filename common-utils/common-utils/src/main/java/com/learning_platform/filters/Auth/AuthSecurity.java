package com.learning_platform.filters.Auth;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("auth")
public class AuthSecurity {

  private static final Logger log = LoggerFactory.getLogger(AuthSecurity.class);

  public boolean isSelf(String studentId) {
    log.info("Checking if user is self: {}", studentId);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) return false;

    Object principal = authentication.getPrincipal();
    if (principal instanceof Map<?, ?> map) {
      log.info("User map: {}", map);
      return studentId.equals(map.get("username")); // or whatever claim you set
    }

    return false;
  }
}
