package com.shuttleverse.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVEntityScheduleCreationData {

  private Integer dayOfWeek;

  private String startTime;

  private String endTime;
}
