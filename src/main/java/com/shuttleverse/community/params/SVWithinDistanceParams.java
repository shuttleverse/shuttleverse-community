package com.shuttleverse.community.params;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Builder
@Getter
@Setter
public class SVWithinDistanceParams {

  @NotNull(message = "Missing location")
  private Point location;
  @NotNull(message = "Missing distance")
  private int distance;

  public SVWithinDistanceParams(Point location, int distance) {
    this.location = location;
    this.distance = distance;
  }
}
