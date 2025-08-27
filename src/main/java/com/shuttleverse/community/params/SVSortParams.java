package com.shuttleverse.community.params;

import com.shuttleverse.community.constants.SVInfoConstant;
import com.shuttleverse.community.constants.SVSortDirection;
import com.shuttleverse.community.constants.SVSortType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVSortParams {

  private SVSortType sortType;
  private SVSortDirection sortDirection;
  private Double longitude;
  private Double latitude;

  public SVSortParams(SVSortType sortType, SVSortDirection sortDirection, Double longitude,
      Double latitude) {
    this.sortType = sortType != null ? sortType : SVSortType.LOCATION;
    this.sortDirection = sortDirection != null ? sortDirection : SVSortDirection.ASC;
    this.longitude = longitude != null ? longitude
        : SVInfoConstant.DEFAULT_LOCATION.getX();
    this.latitude = latitude != null ? latitude
        : SVInfoConstant.DEFAULT_LOCATION.getY();
  }
}
