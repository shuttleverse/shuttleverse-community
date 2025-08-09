package com.shuttleverse.community.params;

import com.shuttleverse.community.constants.SVInfoConstant;
import com.shuttleverse.community.constants.SVSortDirection;
import com.shuttleverse.community.constants.SVSortType;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
public class SVSortParams {

  private SVSortType sortType;
  private SVSortDirection sortDirection;
  private Point location;

  public SVSortParams(SVSortType sortType, SVSortDirection sortDirection, Point location) {
    this.sortType = sortType != null ? sortType : SVSortType.LOCATION;
    this.sortDirection = sortDirection != null ? sortDirection : SVSortDirection.ASC;
    this.location = location != null ? location : SVInfoConstant.DEFAULT_LOCATION;
  }
}
