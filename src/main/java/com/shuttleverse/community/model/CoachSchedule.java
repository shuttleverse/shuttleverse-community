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
@Table(name = "coach_schedule")
@Data
public class CoachSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "schedule_id")
  private UUID id;

  @Column(name = "coach_id", nullable = false)
  private UUID coachId;

  @Column(name = "day_of_week", nullable = false)
  private Integer dayOfWeek;

  @Column(name = "start_time", nullable = false, length = 10)
  private String startTime;

  @Column(name = "end_time", nullable = false, length = 10)
  private String endTime;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

  @Column(name = "upvotes", nullable = false)
  private Integer upvotes = 0;

  @Column(name = "is_verified", nullable = false)
  private boolean isVerified = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "submitted_by", nullable = false)
  private User submittedBy;

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