package com.shuttleverse.community.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "court")
@Data
public class Court {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "court_id")
  private UUID id;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "location", nullable = false)
  private String location;

  @Column(name = "location_point")
  private Point locationPoint;

  @Column(name = "description", length = 400)
  private String description;

  @Column(name = "website")
  private String website;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "other_contacts")
  private String otherContacts;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "court_id")
  private List<CourtSchedule> scheduleList;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "court_id")
  private List<CourtPrice> priceList;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private User creator;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private User owner;

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
