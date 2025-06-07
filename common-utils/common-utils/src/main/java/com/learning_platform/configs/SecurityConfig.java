package com.learning_platform.configs;

import com.learning_platform.filters.JWTFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ConditionalOnProperty(
    name = "common-utils.security.enabled",
    havingValue = "true",
    matchIfMissing = true)
@ConfigurationProperties(prefix = "common-utils.security")
@Getter
@Setter
public class SecurityConfig {

  private List<String> unprotectedEndpoints;

  private boolean permitAll;

  @Autowired JWTFilter jwtFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // Define public endpoints that don't need authentication
    String[] publicEndpoints = {
      "/auth/api/v1/login",
      "/auth/api/v1/signup",
      "/auth/api/v1/register",
      "/auth/api/v1/refresh",
      "/auth/api/v1/forgot-password",
      "/auth/api/v1/verifyToken",
      "/health",
      "/actuator/**",
      "/error",
      "/debug/**"
    };

    // Combine public endpoints with dynamic unprotected endpoints
    List<String> allPublicEndpoints = new ArrayList<>();
    allPublicEndpoints.addAll(List.of(publicEndpoints));
    allPublicEndpoints.addAll(unprotectedEndpoints);
    System.out.println("allPublicEndpoints: " + unprotectedEndpoints + " " + permitAll);

    HttpSecurity httpSecurity =
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(
                authorize -> {
                  if (permitAll) {
                    // If permit-all is enabled, allow everything
                    authorize.anyRequest().permitAll();
                  } else {
                    // Standard security configuration
                    authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        .requestMatchers(allPublicEndpoints.toArray(new String[0]))
                        .permitAll()
                        .anyRequest()
                        .authenticated();
                  }
                })
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(
                exceptions ->
                    exceptions
                        .authenticationEntryPoint(
                            (request, response, authException) -> {
                              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                              response.setContentType("application/json");
                              response
                                  .getWriter()
                                  .write(
                                      "{\"error\": \"Unauthorized\", \"message\": \"Authentication required\", \"status\": 401}");
                            })
                        .accessDeniedHandler(
                            (request, response, accessDeniedException) -> {
                              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                              response.setContentType("application/json");
                              response
                                  .getWriter()
                                  .write(
                                      "{\"error\": \"Forbidden\", \"message\": \"Access denied\", \"status\": 403}");
                            }));

    // Only add JWT filter if not in permit-all mode
    if (!permitAll) {
      httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    return httpSecurity.build();
  }

  // @Bean
  // public JWTFilter jwtFilter() {
  // return new JWTFilter();
  // }

  @Bean
  @ConditionalOnProperty(name = "common-utils.security.provide-default-user", havingValue = "true")
  public UserDetailsService userDetailsService() {
    // Provide a default user for testing/development
    UserDetails user = User.withUsername("admin").password("admin").roles("ADMIN").build();

    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }
}
