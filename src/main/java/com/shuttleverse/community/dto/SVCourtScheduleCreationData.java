package com.shuttleverse.community.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVCourtScheduleCreationData {

  @NotNull
  private Integer dayOfWeek;

  @NotNull
  private String startTime;

  @NotNull
  private String endTime;
}
