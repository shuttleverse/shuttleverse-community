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
@Table(name = "stringer")
@Data
public class Stringer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "stringer_id")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "club_id")
  private Club club;

  @Column(name = "name")
  private String name;

  @Column(name = "location")
  private String location;

  @Column(name = "description")
  private String description;

  @Column(name = "other_contacts")
  private String otherContacts;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "additional_details")
  private String additionalDetails;

  @OneToMany(mappedBy = "stringer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference("stringer-price")
  private List<StringerPrice> priceList;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

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
