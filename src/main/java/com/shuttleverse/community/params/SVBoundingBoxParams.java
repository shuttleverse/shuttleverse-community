package com.shuttleverse.community.params;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVBoundingBoxParams {

  @NotNull(message = "Missing bounding box minLat")
  private Double minLat;
  @NotNull(message = "Missing bounding box minLon")
  private Double minLon;
  @NotNull(message = "Missing bounding box maxLat")
  private Double maxLat;
  @NotNull(message = "Missing bounding box maxLon")
  private Double maxLon;

  public SVBoundingBoxParams(Double minLat, Double minLon, Double maxLat, Double maxLon) {
    this.minLat = minLat;
    this.minLon = minLon;
    this.maxLat = maxLat;
    this.maxLon = maxLon;
  }
}
