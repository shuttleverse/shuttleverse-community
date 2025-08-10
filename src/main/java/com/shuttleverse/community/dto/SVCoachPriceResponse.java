package com.shuttleverse.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVCoachPriceResponse extends SVEntityPriceDto {

  private Double minPrice;
  private Double maxPrice;
  private Integer duration;
  private String durationUnit;
  private String description;
}
