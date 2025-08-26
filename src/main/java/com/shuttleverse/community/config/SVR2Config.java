package com.shuttleverse.community.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cloudflare.r2")
public class SVR2Config {

  private String accountId;
  private String accessKey;
  private String secretKey;
  private String bucket;
  private String publicBaseUrl;
}

