package com.shuttleverse.community.mapper;

import com.shuttleverse.community.dto.SVCoachCreationData;
import com.shuttleverse.community.dto.SVCoachPriceResponse;
import com.shuttleverse.community.dto.SVCoachResponse;
import com.shuttleverse.community.dto.SVCoachScheduleResponse;
import com.shuttleverse.community.dto.SVCourtCreationData;
import com.shuttleverse.community.dto.SVCourtPriceResponse;
import com.shuttleverse.community.dto.SVCourtResponse;
import com.shuttleverse.community.dto.SVCourtScheduleResponse;
import com.shuttleverse.community.dto.SVLocationDto;
import com.shuttleverse.community.dto.SVStringerCreationData;
import com.shuttleverse.community.dto.SVStringerPriceResponse;
import com.shuttleverse.community.dto.SVStringerResponse;
import com.shuttleverse.community.dto.SVUserResponse;
import com.shuttleverse.community.model.SVCoach;
import com.shuttleverse.community.model.SVCoachPrice;
import com.shuttleverse.community.model.SVCoachSchedule;
import com.shuttleverse.community.model.SVCourt;
import com.shuttleverse.community.model.SVCourtPrice;
import com.shuttleverse.community.model.SVCourtSchedule;
import com.shuttleverse.community.model.SVStringer;
import com.shuttleverse.community.model.SVStringerPrice;
import com.shuttleverse.community.model.SVUser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SVMapStructMapper {

  default SVLocationDto pointToLocationDto(Point point) {
    if (point == null) {
      return null;
    }
    SVLocationDto dto = new SVLocationDto();
    dto.setLatitude(point.getY());
    dto.setLongitude(point.getX());
    return dto;
  }

  default Point locationDtoToPoint(SVLocationDto locationDto) {
    if (locationDto == null) {
      return null;
    }
    GeometryFactory geometryFactory = new GeometryFactory();
    return geometryFactory.createPoint(new Coordinate(
        locationDto.getLongitude(),
        locationDto.getLatitude()
    ));
  }

  SVUserResponse userToUserDto(SVUser user);

  @Mapping(target = "locationPoint", source = "locationPoint")
  SVCourt toCourt(SVCourtCreationData courtCreationData);

  @Mapping(target = "locationPoint", source = "locationPoint")
  SVCoach toCoach(SVCoachCreationData coachCreationData);

  @Mapping(target = "locationPoint", source = "locationPoint")
  SVStringer toStringer(SVStringerCreationData stringerCreationData);

  SVCoachResponse toCoachResponse(SVCoach coach);

  @Mapping(source = "coachId", target = "parentEntityId")
  @Mapping(source = "isVerified", target = "verified")
  SVCoachPriceResponse toCoachPriceResponse(SVCoachPrice coachPrice);

  @Mapping(source = "coachId", target = "parentEntityId")
  SVCoachScheduleResponse toCoachScheduleResponse(SVCoachSchedule coachSchedule);

  SVCourtResponse toCourtResponse(SVCourt court);

  @Mapping(source = "courtId", target = "parentEntityId")
  @Mapping(source = "isVerified", target = "verified")
  SVCourtPriceResponse toCourtPriceResponse(SVCourtPrice courtPrice);

  @Mapping(source = "courtId", target = "parentEntityId")
  @Mapping(source = "openTime", target = "startTime")
  @Mapping(source = "closeTime", target = "endTime")
  SVCourtScheduleResponse toCourtScheduleResponse(SVCourtSchedule courtSchedule);

  SVStringerResponse toStringerResponse(SVStringer stringer);

  @Mapping(source = "stringerId", target = "parentEntityId")
  @Mapping(source = "isVerified", target = "verified")
  SVStringerPriceResponse toStringerPriceResponse(SVStringerPrice stringerPrice);
}
