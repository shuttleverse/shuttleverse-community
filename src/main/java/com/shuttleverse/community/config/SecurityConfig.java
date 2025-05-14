package com.shuttleverse.community.config;

import jakarta.ws.rs.HttpMethod;
import java.util.Collections;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for setting up OAuth2 resource server with JWT authentication. Configures
 * the application to validate JWT tokens using the public JWK set from Google's OAuth2 service.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${jwt.secret}")
  private String jwtSecretKey;

  /**
   * Configures the security settings for the application. This includes disabling CSRF, requiring
   * authentication for all requests, and setting up OAuth2 resource server with JWT support using
   * Google's public JWK Set.
   *
   * @param http The HttpSecurity object used for configuring security.
   * @return The SecurityFilterChain that defines the security configuration.
   * @throws Exception If an error occurs during the configuration of security.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/error").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthenticationConverter())));
    return http.build();
  }

  /**
   * Creates a JWT decoder bean that validates and decodes JWT tokens using HMAC-SHA256 algorithm.
   * This decoder uses a shared secret key defined in application properties to verify token
   * signatures.
   *
   * @return A configured JwtDecoder that can validate tokens created by the gateway
   */
  @Bean
  public JwtDecoder jwtDecoder() {
    SecretKey key = new SecretKeySpec(jwtSecretKey.getBytes(), "HmacSHA256");

    return NimbusJwtDecoder.withSecretKey(key)
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
  }

  /**
   * Creates a JWT authentication converter that assigns a default "USER" authority to all
   * authenticated requests.
   *
   * @return A configured JwtAuthenticationConverter that converts JWT claims to Spring Security
   *     authorities
   */
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
        jwt -> Collections.singletonList(new SimpleGrantedAuthority("USER")));

    return jwtAuthenticationConverter;
  }

}