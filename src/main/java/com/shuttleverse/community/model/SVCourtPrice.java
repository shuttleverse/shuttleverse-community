package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "court_price")
@Data
public class SVCourtPrice extends SVBaseUpvotable {

  @Column(name = "court_id", nullable = false)
  private UUID courtId;

  @Column(name = "price", nullable = false)
  private Double price;

  @Column(name = "duration", nullable = false)
  private Integer duration;
}
