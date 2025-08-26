package com.shuttleverse.community.service;

import com.shuttleverse.community.config.SVR2Config;
import java.net.URI;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class SVR2StorageService {

  private final SVR2Config properties;
  private final S3Client s3Client;

  public SVR2StorageService(SVR2Config properties) {
    this.properties = properties;
    this.s3Client = S3Client.builder()
        .region(Region.of("auto"))
        .endpointOverride(
            URI.create("https://" + properties.getAccountId() + ".r2.cloudflarestorage.com"))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
            )
        )
        .build();
  }

  public String uploadFile(MultipartFile file, String key) {
    try {
      s3Client.putObject(PutObjectRequest.builder()
              .bucket(properties.getBucket())
              .key(key)
              .contentType(file.getContentType())
              .build(),
          RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

      return properties.getPublicBaseUrl() + "/" + key;

    } catch (Exception e) {
      throw new RuntimeException("Failed to upload file to R2", e);
    }
  }
}
