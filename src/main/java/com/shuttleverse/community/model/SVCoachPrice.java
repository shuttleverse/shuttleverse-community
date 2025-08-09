package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "coach_price")
@Data
public class SVCoachPrice extends SVBaseUpvotable {

  @Column(name = "coach_id", nullable = false)
  private UUID coachId;

  @Column(name = "price", nullable = false)
  private Double price;

  @Column(name = "duration", nullable = false)
  private Integer duration;
}
