package com.shuttleverse.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

/**
 * Web configuration for Spring Data pagination and serialization.
 */
@Configuration
@EnableSpringDataWebSupport
public class SVWebConfig {

  /**
   * Customize the Pageable resolver to use sensible defaults.
   */
  @Bean
  public PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
    return pageableResolver -> {
      pageableResolver.setOneIndexedParameters(true);
      pageableResolver.setMaxPageSize(100);
      pageableResolver.setFallbackPageable(PageRequest.of(0, 20));
    };
  }
}