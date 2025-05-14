package com.shuttleverse.community.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "court")
@Data
public class Court {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "court_id")
  private UUID id;

  @Column(name = "name")
  private String name;

  @Column(name = "location")
  private String location;

  @Column(name = "description")
  private String description;

  @Column(name = "website")
  private String website;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

  @OneToMany(mappedBy = "court", cascade = CascadeType.ALL)
  private List<CourtSchedule> scheduleList;

  @ManyToOne
  @JoinColumn(name = "creator_id")
  private User creator;

  @ManyToOne
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
