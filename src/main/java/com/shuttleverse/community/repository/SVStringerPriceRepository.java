package com.shuttleverse.community.repository;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.model.SVStringerPrice;
import java.util.List;
import java.util.UUID;
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

  List<SVStringerPrice> findAllByStringerId(UUID stringerId);

  void deleteAllByStringerId(UUID stringerId);
}