package com.shuttleverse.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVStringerPriceResponse extends SVEntityPriceDto {

  private Double price;
  private String stringName;
}
