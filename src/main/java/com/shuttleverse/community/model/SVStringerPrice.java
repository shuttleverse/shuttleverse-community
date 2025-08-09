package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "stringer_price")
@Data
public class SVStringerPrice extends SVBaseUpvotable {

  @Column(name = "stringer_id", nullable = false)
  private UUID stringerId;

  @Column(name = "string_name", nullable = false, length = 100)
  private String stringName;

  @Column(name = "price", nullable = false)
  private Double price;
}