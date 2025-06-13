package com.shuttleverse.community.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoachResponse extends BadmintonEntityDto {

  private Integer experienceYears;
  private List<CoachScheduleResponse> scheduleList;
  private List<CoachPriceResponse> priceList;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  protected UserResponse creator;
  protected UserResponse owner;
}
