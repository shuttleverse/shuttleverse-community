package com.shuttleverse.community.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "coach_schedule")
@Data
public class SVCoachSchedule extends SVBaseUpvotable {

  @Column(name = "coach_id", nullable = false)
  private UUID coachId;

  @Column(name = "day_of_week", nullable = false)
  private Integer dayOfWeek;

  @Column(name = "start_time", nullable = false, length = 10)
  private String startTime;

  @Column(name = "end_time", nullable = false, length = 10)
  private String endTime;
}