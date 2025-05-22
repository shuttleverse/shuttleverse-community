package com.shuttleverse.community.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

@Entity
@Table(name = "coach")
@Data
public class Coach {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coach_id")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "club_id")
  private Club club;

  @Column(nullable = false, length = 100)
  private String name;

  @Column
  private String location;

  @Column(length = 1000)
  private String description;

  @Column(name = "experience_years")
  private Integer experienceYears;

  @Column(name = "other_contacts", length = 100)
  private String otherContacts;

  @Column(name = "phone_number", length = 20)
  private String contactPhone;

  @OneToMany(mappedBy = "coach", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference("coach-schedule")
  private List<CoachSchedule> scheduleList;

  @OneToMany(mappedBy = "coach", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference("coach-price")
  private List<CoachPrice> priceList;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private User owner;

  @Column(name = "is_verified", nullable = false)
  private boolean isVerified;

  @ManyToOne
  @JoinColumn(name = "creator_id")
  private User creator;

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