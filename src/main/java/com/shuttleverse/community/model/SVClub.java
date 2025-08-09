package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "club")
@Data
public class SVClub {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "club_id")
  private UUID id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column
  private String location;

  @Column(length = 1000)
  private String description;

  @Column
  private String website;

  @Column(length = 20)
  private String phone;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

  @Column(name = "creator_id", nullable = false)
  private UUID creatorId;

  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  @Column(name = "is_verified", nullable = false)
  private boolean isVerified;

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