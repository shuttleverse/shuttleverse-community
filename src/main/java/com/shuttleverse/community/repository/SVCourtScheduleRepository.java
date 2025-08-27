package com.shuttleverse.community.repository;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.model.SVCourtSchedule;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface SVCourtScheduleRepository extends SVUpvotableRepository<SVCourtSchedule> {

  @Override
  default SVEntityType getEntityType() {
    return SVEntityType.COURT;
  }

  @Override
  default SVInfoType getInfoType() {
    return SVInfoType.SCHEDULE;
  }

  List<SVCourtSchedule> findAllByCourtId(UUID courtId);

  void deleteAllByCourtId(UUID courtId);
}