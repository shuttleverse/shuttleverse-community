package com.shuttleverse.community.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Table(name = "coach_price")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CoachPrice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "price_id")
  private UUID id;

  @Column(name = "coach_id", nullable = false)
  private UUID coachId;

  @Column(name = "price", nullable = false)
  private Double price;

  @Column(name = "duration", nullable = false)
  private Integer duration;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

  @Column(name = "upvotes", nullable = false)
  private Integer upvotes = 0;

  @Column(name = "is_verified", nullable = false)
  private Boolean isVerified = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "submitted_by", nullable = false)
  @JsonBackReference("user-price")
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
