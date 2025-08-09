package com.shuttleverse.community.params;

import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVEntityFilterParams {

  private List<Integer> daysOfWeek;
  private Integer minPrice;
  private Integer maxPrice;
  private Boolean isVerified;

  public SVEntityFilterParams(@Nullable List<Integer> daysOfWeek, Integer minPrice,
      Integer maxPrice,
      Boolean isVerified) {
    this.daysOfWeek = daysOfWeek;
    this.minPrice = minPrice != null ? minPrice : 0;
    this.maxPrice = maxPrice == null ? Integer.MAX_VALUE : maxPrice;
    this.isVerified = isVerified != null ? isVerified : false;
  }
}
