package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "verification_file")
@Data
public class SVVerificationFile {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "claim_id", nullable = false)
  private UUID claimId;

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "file_url", nullable = false)
  private String fileUrl;
}
