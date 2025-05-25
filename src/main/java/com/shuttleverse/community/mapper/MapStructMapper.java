package com.shuttleverse.community.mapper;

import com.shuttleverse.community.dto.CoachPriceResponse;
import com.shuttleverse.community.dto.CoachResponse;
import com.shuttleverse.community.dto.CoachScheduleResponse;
import com.shuttleverse.community.dto.CourtPriceResponse;
import com.shuttleverse.community.dto.CourtResponse;
import com.shuttleverse.community.dto.CourtScheduleResponse;
import com.shuttleverse.community.dto.StringerPriceResponse;
import com.shuttleverse.community.dto.StringerResponse;
import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachPrice;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  CoachResponse toCoachResponse(Coach coach);

  @Mapping(source = "coachId", target = "parentEntityId")
  @Mapping(source = "isVerified", target = "verified")
  CoachPriceResponse toCoachPriceResponse(CoachPrice coachPrice);

  @Mapping(source = "coachId", target = "parentEntityId")
  CoachScheduleResponse toCoachScheduleResponse(CoachSchedule coachSchedule);

  CourtResponse toCourtResponse(Court court);

  @Mapping(source = "courtId", target = "parentEntityId")
  @Mapping(source = "isVerified", target = "verified")
  CourtPriceResponse toCourtPriceResponse(CourtPrice courtPrice);

  @Mapping(source = "courtId", target = "parentEntityId")
  @Mapping(source = "openTime", target = "startTime")
  @Mapping(source = "closeTime", target = "endTime")
  CourtScheduleResponse toCourtScheduleResponse(CourtSchedule courtSchedule);

  StringerResponse toStringerResponse(Stringer stringer);

  @Mapping(source = "stringerId", target = "parentEntityId")
  @Mapping(source = "isVerified", target = "verified")
  StringerPriceResponse toStringerPriceResponse(StringerPrice stringerPrice);
}
