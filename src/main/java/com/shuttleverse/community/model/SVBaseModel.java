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
import org.locationtech.jts.geom.Point;

@MappedSuperclass
@Getter
@Setter
public abstract class SVBaseModel {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  protected UUID id;

  @Column(nullable = false, length = 100)
  protected String name;

  @Column(length = 100)
  protected String location;

  @Column(name = "location_point")
  protected Point locationPoint;

  @Column(length = 400)
  protected String description;

  @Column(name = "created_at", nullable = false)
  protected ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  protected ZonedDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private SVUser owner;

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
