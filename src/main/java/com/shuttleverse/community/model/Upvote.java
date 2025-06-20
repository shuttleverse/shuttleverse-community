package com.shuttleverse.community.model;

import com.shuttleverse.community.constants.BadmintonEntityType;
import com.shuttleverse.community.constants.BadmintonInfoType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "upvote")
@Data
public class Upvote {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "upvote_id")
  private UUID upvoteId;

  @Column(name = "entity_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private BadmintonEntityType entityType;

  @Column(name = "info_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private BadmintonInfoType infoType;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User upvoteCreator;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "entity_id", nullable = false)
  private UUID entityId;

  @PrePersist
  protected void onCreate() {
    createdAt = ZonedDateTime.now();
  }

}
