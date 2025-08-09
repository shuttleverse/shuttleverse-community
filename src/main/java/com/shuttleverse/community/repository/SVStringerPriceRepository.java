package com.shuttleverse.community.repository;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.model.SVStringerPrice;
import org.springframework.stereotype.Repository;

@Repository
public interface SVStringerPriceRepository extends SVUpvotableRepository<SVStringerPrice> {

  @Override
  default SVEntityType getEntityType() {
    return SVEntityType.STRINGER;
  }

  @Override
  default SVInfoType getInfoType() {
    return SVInfoType.PRICE;
  }
}