package com.shuttleverse.community.repository;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.model.SVCourtPrice;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface SVCourtPriceRepository extends SVUpvotableRepository<SVCourtPrice> {

  @Override
  default SVEntityType getEntityType() {
    return SVEntityType.COURT;
  }

  @Override
  default SVInfoType getInfoType() {
    return SVInfoType.PRICE;
  }

  List<SVCourtPrice> findAllByCourtId(UUID courtId);

  void deleteAllByCourtId(UUID courtId);
}