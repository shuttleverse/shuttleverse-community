package com.shuttleverse.community.params;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BoundingBoxParams {

  private Double minLat;
  private Double minLon;
  private Double maxLat;
  private Double maxLon;

  public BoundingBoxParams(Double minLat, Double minLon, Double maxLat, Double maxLon) {
    this.minLat = minLat;
    this.minLon = minLon;
    this.maxLat = maxLat;
    this.maxLon = maxLon;
  }
}
