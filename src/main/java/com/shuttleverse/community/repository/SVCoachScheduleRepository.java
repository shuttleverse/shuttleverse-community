package com.shuttleverse.community.repository;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.model.SVCoachSchedule;
import org.springframework.stereotype.Repository;

@Repository
public interface SVCoachScheduleRepository extends SVUpvotableRepository<SVCoachSchedule> {

  @Override
  default SVEntityType getEntityType() {
    return SVEntityType.COACH;
  }

  @Override
  default SVInfoType getInfoType() {
    return SVInfoType.SCHEDULE;
  }
}