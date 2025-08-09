package com.shuttleverse.community.constants;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class SVInfoConstant {

  private static final GeometryFactory geometryFactory = new GeometryFactory();

  public static final Point DEFAULT_LOCATION = geometryFactory.createPoint(
      new Coordinate(-71.0589, 42.3601));
}
