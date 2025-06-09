package com.learning_platform.filters;

import com.learning_platform.constants.AppConstants;
import com.learning_platform.utils.CommonJwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JWTFilter extends OncePerRequestFilter {

  @Autowired private CommonJwtUtils commonJwtUtils;

  public String getTokenFromCookies(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("token".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }

    return null; // token cookie not found
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    System.out.println("JWTFilter: doFilterInternal");

    Enumeration<String> headers = request.getHeaderNames();
    while (headers.hasMoreElements()) {
      String headerName = headers.nextElement();
      System.out.println("Header: " + headerName + " = " + request.getHeader(headerName));
    }

    String token = getTokenFromCookies(request);

    if (token != null) {
      token = token.replace("Bearer ", "");

      try {
        // Decode and validate the JWT token
        Claims claims = commonJwtUtils.decodeJWTClaims(token);

        // Additional validation - check if token is expired
        if (claims == null || claims.getExpiration().before(new java.util.Date())) {
          System.out.println("JWT token is expired or invalid");
          sendUnauthorizedResponse(response, "Token expired or invalid");
          return;
        }

        String username = claims.getSubject();

        if (username == null || username.trim().isEmpty()) {
          System.out.println("JWT token has no valid subject");
          sendUnauthorizedResponse(response, "Invalid token - no subject");
          return;
        }

        List<String> roles = claims.get("roles", List.class);

        List<GrantedAuthority> authorities =
            roles != null
                ? roles.stream()
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                    .toList()
                : List.of();

        // Create user details
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", username);
        userDetails.put(AppConstants.SUBSCRIPTION, claims.get(AppConstants.SUBSCRIPTION));
        userDetails.put(AppConstants.CLAIM_USER, claims.get(AppConstants.CLAIM_USER));
        // userDetails.put(AppConstants.CLAIM_USER_ID, claims.get(AppConstants.CLAIM_USER_ID));
        userDetails.put(AppConstants.ALL_CLAIMS, claims);
        // Set authentication in security context
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println("JWT authentication successful for user: " + username);

      } catch (JwtException | IllegalArgumentException e) {
        System.out.println("JWT token validation failed: " + e.getMessage());
        // Clear any existing authentication
        SecurityContextHolder.clearContext();
        sendUnauthorizedResponse(response, "Invalid or malformed token");
        return;
      } catch (Exception e) {
        System.out.println("Unexpected error during JWT processing: " + e.getMessage());
        SecurityContextHolder.clearContext();
        sendUnauthorizedResponse(response, "Authentication error");
        return;
      }
    } else {
      // No token found - clear context to ensure no residual authentication
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, String message)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String jsonResponse =
        String.format(
            "{\"error\": \"Unauthorized\", \"message\": \"%s\", \"status\": 401}", message);

    response.getWriter().write(jsonResponse);
    response.getWriter().flush();
  }
}
