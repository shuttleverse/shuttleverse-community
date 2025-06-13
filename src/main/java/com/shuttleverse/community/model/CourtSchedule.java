package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "court_schedule")
@Data
public class CourtSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "schedule_id")
  private UUID id;

  @Column(name = "court_id", nullable = false)
  private UUID courtId;

  @Column(name = "day_of_week", nullable = false)
  private Integer dayOfWeek;

  @Column(name = "open_time", nullable = false)
  private String openTime;

  @Column(name = "close_time", nullable = false)
  private String closeTime;

  @Column(name = "is_verified", nullable = false)
  private boolean isVerified = false;

  @Column(name = "upvotes", nullable = false)
  private Integer upvotes = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "submitted_by", nullable = false)
  private User submittedBy;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

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
