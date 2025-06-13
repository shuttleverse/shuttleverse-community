package com.shuttleverse.community.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringerResponse extends BadmintonEntityDto {

  private List<StringerPriceResponse> priceList;
  private String additionalDetails;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  protected UserResponse creator;
  protected UserResponse owner;
}
