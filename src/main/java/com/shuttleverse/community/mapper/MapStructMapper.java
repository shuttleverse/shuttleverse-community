package com.shuttleverse.community.mapper;

import com.shuttleverse.community.dto.CoachCreationData;
import com.shuttleverse.community.dto.CoachPriceResponse;
import com.shuttleverse.community.dto.CoachResponse;
import com.shuttleverse.community.dto.CoachScheduleResponse;
import com.shuttleverse.community.dto.CourtCreationData;
import com.shuttleverse.community.dto.CourtPriceResponse;
import com.shuttleverse.community.dto.CourtResponse;
import com.shuttleverse.community.dto.CourtScheduleResponse;
import com.shuttleverse.community.dto.LocationDto;
import com.shuttleverse.community.dto.StringerCreationData;
import com.shuttleverse.community.dto.StringerPriceResponse;
import com.shuttleverse.community.dto.StringerResponse;
import com.shuttleverse.community.dto.UserResponse;
import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachPrice;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import com.shuttleverse.community.model.User;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapStructMapper {

  default LocationDto pointToLocationDto(Point point) {
    if (point == null) {
      return null;
    }
    LocationDto dto = new LocationDto();
    dto.setLatitude(point.getY());
    dto.setLongitude(point.getX());
    return dto;
  }

  default Point locationDtoToPoint(LocationDto locationDto) {
    if (locationDto == null) {
      return null;
    }
    GeometryFactory geometryFactory = new GeometryFactory();
    return geometryFactory.createPoint(new Coordinate(
        locationDto.getLongitude(),
        locationDto.getLatitude()
    ));
  }

  UserResponse userToUserDto(User user);

  @Mapping(target = "locationPoint", source = "locationPoint")
  Court toCourt(CourtCreationData courtCreationData);

  @Mapping(target = "locationPoint", source = "locationPoint")
  Coach toCoach(CoachCreationData coachCreationData);

  @Mapping(target = "locationPoint", source = "locationPoint")
  Stringer toStringer(StringerCreationData stringerCreationData);

  CoachResponse toCoachResponse(Coach coach);

  @Mapping(source = "coachId", target = "parentEntityId")
  @Mapping(source = "isVerified", target = "verified")
  CoachPriceResponse toCoachPriceResponse(CoachPrice coachPrice);

  @Mapping(source = "coachId", target = "parentEntityId")
  CoachScheduleResponse toCoachScheduleResponse(CoachSchedule coachSchedule);

  CoachResponse coachResponse(Coach coach);

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
