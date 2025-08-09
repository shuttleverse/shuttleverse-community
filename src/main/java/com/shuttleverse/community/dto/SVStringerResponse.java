package com.shuttleverse.community.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVStringerResponse extends SVEntityDto {

  private List<SVStringerPriceResponse> priceList;
  private String additionalDetails;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  protected SVUserResponse creator;
  protected SVUserResponse owner;
}
