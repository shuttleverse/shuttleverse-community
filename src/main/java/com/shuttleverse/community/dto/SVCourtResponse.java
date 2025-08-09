package com.shuttleverse.community.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVCourtResponse extends SVEntityDto {

  private String website;
  private List<SVCourtScheduleResponse> scheduleList;
  private List<SVCourtPriceResponse> priceList;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  protected SVUserResponse creator;
  protected SVUserResponse owner;
}
