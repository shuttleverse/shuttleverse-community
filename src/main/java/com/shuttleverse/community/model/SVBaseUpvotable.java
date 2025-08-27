package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class SVBaseUpvotable implements SVUpvotable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  protected UUID id;

  @Column(name = "created_at", nullable = false)
  protected ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  protected ZonedDateTime updatedAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "submitted_by", nullable = false)
  protected SVUser submittedBy;

  @Column(name = "upvotes", nullable = false)
  protected Integer upvotes = 0;

  @Column(name = "is_verified", nullable = false)
  protected Boolean isVerified = false;

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
