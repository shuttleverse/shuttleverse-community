package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class SVUser {

  /**
   * ID should be created from Google sub. No generation method needed.
   */
  @Id
  @Column(name = "user_id")
  private UUID id;

  @Column(nullable = false, length = 50)
  private String username;

  @Column(nullable = false, length = 100)
  private String email;

  private String bio;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

  @Column(name = "is_admin", nullable = false)
  private boolean isAdmin;

  @PrePersist
  protected void onCreate() {
    createdAt = ZonedDateTime.now();
    updatedAt = ZonedDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = ZonedDateTime.now();
  }

}