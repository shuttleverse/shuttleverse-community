package com.shuttleverse.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVEntityPriceCreationData {

  private Double minPrice;

  private Double maxPrice;

  private String description;

  private Integer duration;

  private String durationUnit;
}
