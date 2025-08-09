package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "court_schedule")
@Data
public class SVCourtSchedule extends SVBaseUpvotable {

  @Column(name = "court_id", nullable = false)
  private UUID courtId;

  @Column(name = "day_of_week", nullable = false)
  private Integer dayOfWeek;

  @Column(name = "open_time", nullable = false)
  private String openTime;

  @Column(name = "close_time", nullable = false)
  private String closeTime;
}
