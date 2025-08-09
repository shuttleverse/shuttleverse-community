package com.shuttleverse.community.repository;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.model.SVCoachPrice;

public interface SVCoachPriceRepository extends SVUpvotableRepository<SVCoachPrice> {

  @Override
  default SVEntityType getEntityType() {
    return SVEntityType.COACH;
  }

  @Override
  default SVInfoType getInfoType() {
    return SVInfoType.PRICE;
  }
}
