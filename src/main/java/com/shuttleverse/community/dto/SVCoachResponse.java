package com.shuttleverse.community.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVCoachResponse extends SVEntityDto {

  private Integer experienceYears;
  private List<SVCoachScheduleResponse> scheduleList;
  private List<SVCoachPriceResponse> priceList;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  protected SVUserResponse creator;
  protected SVUserResponse owner;
}
