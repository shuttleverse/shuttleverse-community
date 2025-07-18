package com.shuttleverse.community.params;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Builder
@Getter
@Setter
public class WithinDistanceParams {

  private Point location;
  private int distance;

  public WithinDistanceParams(Point location, int distance) {
    this.location = location;
    this.distance = distance;
  }
}
