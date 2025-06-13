package com.shuttleverse.community.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtResponse extends BadmintonEntityDto {

  private String website;
  private List<CourtScheduleResponse> scheduleList;
  private List<CourtPriceResponse> priceList;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  protected UserResponse creator;
  protected UserResponse owner;
}
