package com.shuttleverse.community.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.shuttleverse.community.dto.SVCoachPriceResponse;
import com.shuttleverse.community.dto.SVCoachResponse;
import com.shuttleverse.community.dto.SVCoachScheduleResponse;
import com.shuttleverse.community.dto.SVCourtPriceResponse;
import com.shuttleverse.community.dto.SVCourtResponse;
import com.shuttleverse.community.dto.SVCourtScheduleResponse;
import com.shuttleverse.community.dto.SVStringerPriceResponse;
import com.shuttleverse.community.dto.SVStringerResponse;
import com.shuttleverse.community.model.SVCoach;
import com.shuttleverse.community.model.SVCoachPrice;
import com.shuttleverse.community.model.SVCoachSchedule;
import com.shuttleverse.community.model.SVCourt;
import com.shuttleverse.community.model.SVCourtPrice;
import com.shuttleverse.community.model.SVCourtSchedule;
import com.shuttleverse.community.model.SVStringer;
import com.shuttleverse.community.model.SVStringerPrice;
import com.shuttleverse.community.model.SVUser;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class SVMapStructMapperTest {

  private final SVMapStructMapper mapper = Mappers.getMapper(SVMapStructMapper.class);
  private SVUser user;
  private UUID userId;
  private UUID priceId;
  private UUID entityId;
  private UUID scheduleId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    priceId = UUID.randomUUID();
    entityId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();

    user = new SVUser();
    user.setId(userId);
    user.setUsername("testuser");
    user.setEmail("test@example.com");
  }

  @Test
  void testCoachPriceMapping() {
    SVCoachPrice coachPrice = new SVCoachPrice();
    coachPrice.setId(priceId);
    coachPrice.setCoachId(entityId);
    coachPrice.setPrice(100.0);
    coachPrice.setDuration(60);
    coachPrice.setUpvotes(5);
    coachPrice.setSubmittedBy(user);
    coachPrice.setCreatedAt(ZonedDateTime.now());
    coachPrice.setUpdatedAt(ZonedDateTime.now());

    SVCoachPriceResponse response = mapper.toCoachPriceResponse(coachPrice);

    assertNotNull(response);
    assertEquals(priceId, response.getId());
    assertEquals(entityId, response.getParentEntityId());
    assertEquals(100.0, response.getPrice());
    assertEquals(5, response.getUpvotes());
    assertNotNull(response.getSubmittedBy());
    assertEquals(userId, response.getSubmittedBy().getId());
  }

  @Test
  void testCourtPriceMapping() {
    SVCourtPrice courtPrice = new SVCourtPrice();
    courtPrice.setId(priceId);
    courtPrice.setCourtId(entityId);
    courtPrice.setPrice(150.0);
    courtPrice.setDuration(90);
    courtPrice.setUpvotes(3);
    courtPrice.setSubmittedBy(user);
    courtPrice.setCreatedAt(ZonedDateTime.now());
    courtPrice.setUpdatedAt(ZonedDateTime.now());

    SVCourtPriceResponse response = mapper.toCourtPriceResponse(courtPrice);

    assertNotNull(response);
    assertEquals(priceId, response.getId());
    assertEquals(entityId, response.getParentEntityId());
    assertEquals(150.0, response.getPrice());
    assertEquals(3, response.getUpvotes());
    assertNotNull(response.getSubmittedBy());
    assertEquals(userId, response.getSubmittedBy().getId());
  }

  @Test
  void testStringerPriceMapping() {
    SVStringerPrice stringerPrice = new SVStringerPrice();
    stringerPrice.setId(priceId);
    stringerPrice.setStringerId(entityId);
    stringerPrice.setStringName("Yonex BG65");
    stringerPrice.setPrice(30.0);
    stringerPrice.setUpvotes(10);
    stringerPrice.setSubmittedBy(user);
    stringerPrice.setCreatedAt(ZonedDateTime.now());
    stringerPrice.setUpdatedAt(ZonedDateTime.now());

    SVStringerPriceResponse response = mapper.toStringerPriceResponse(stringerPrice);

    assertNotNull(response);
    assertEquals(priceId, response.getId());
    assertEquals(entityId, response.getParentEntityId());
    assertEquals(30.0, response.getPrice());
    assertEquals(10, response.getUpvotes());
    assertNotNull(response.getSubmittedBy());
    assertEquals(userId, response.getSubmittedBy().getId());
  }

  @Test
  void testCoachResponseMapping() {
    SVCoach coach = new SVCoach();
    coach.setId(entityId);
    coach.setName("Test Coach");
    coach.setLocation("Test Location");
    coach.setDescription("Test Description");
    coach.setExperienceYears(5);
    coach.setOtherContacts("test@example.com");
    coach.setPhoneNumber("1234567890");
    coach.setOwner(user);
    coach.setCreator(user);
    coach.setCreatedAt(ZonedDateTime.now());
    coach.setUpdatedAt(ZonedDateTime.now());

    SVCoachSchedule schedule = new SVCoachSchedule();
    schedule.setId(scheduleId);
    schedule.setCoachId(entityId);
    schedule.setDayOfWeek(1);
    schedule.setStartTime("09:00");
    schedule.setEndTime("17:00");
    schedule.setUpvotes(5);
    schedule.setSubmittedBy(user);
    schedule.setCreatedAt(ZonedDateTime.now());
    schedule.setUpdatedAt(ZonedDateTime.now());
    coach.setScheduleList(List.of(schedule));

    SVCoachPrice price = new SVCoachPrice();
    price.setId(priceId);
    price.setCoachId(entityId);
    price.setPrice(100.0);
    price.setDuration(60);
    price.setUpvotes(5);
    price.setSubmittedBy(user);
    price.setCreatedAt(ZonedDateTime.now());
    price.setUpdatedAt(ZonedDateTime.now());
    coach.setPriceList(List.of(price));

    SVCoachResponse response = mapper.toCoachResponse(coach);

    assertNotNull(response);
    assertEquals(entityId, response.getId());
    assertEquals("Test Coach", response.getName());
    assertEquals("Test Location", response.getLocation());
    assertEquals("Test Description", response.getDescription());
    assertEquals(5, response.getExperienceYears());
    assertEquals("test@example.com", response.getOtherContacts());
    assertEquals("1234567890", response.getPhoneNumber());
    assertNotNull(response.getOwner());
    assertEquals(userId, response.getOwner().getId());

    assertNotNull(response.getScheduleList());
    assertEquals(1, response.getScheduleList().size());
    SVCoachScheduleResponse scheduleResponse = response.getScheduleList().get(0);
    assertEquals(scheduleId, scheduleResponse.getId());
    assertEquals(entityId, scheduleResponse.getParentEntityId());
    assertEquals(1, scheduleResponse.getDayOfWeek());
    assertEquals("09:00", scheduleResponse.getStartTime());
    assertEquals("17:00", scheduleResponse.getEndTime());
    assertEquals(5, scheduleResponse.getUpvotes());

    assertNotNull(response.getPriceList());
    assertEquals(1, response.getPriceList().size());
    SVCoachPriceResponse priceResponse = response.getPriceList().get(0);
    assertEquals(priceId, priceResponse.getId());
    assertEquals(entityId, priceResponse.getParentEntityId());
    assertEquals(100.0, priceResponse.getPrice());
    assertEquals(5, priceResponse.getUpvotes());
  }

  @Test
  void testCourtResponseMapping() {
    SVCourt court = new SVCourt();
    court.setId(entityId);
    court.setName("Test Court");
    court.setLocation("Test Location");
    court.setDescription("Test Description");
    court.setOwner(user);
    court.setCreator(user);
    court.setCreatedAt(ZonedDateTime.now());
    court.setUpdatedAt(ZonedDateTime.now());

    SVCourtSchedule schedule = new SVCourtSchedule();
    schedule.setId(scheduleId);
    schedule.setCourtId(entityId);
    schedule.setDayOfWeek(2);
    schedule.setOpenTime("10:00");
    schedule.setCloseTime("22:00");
    schedule.setUpvotes(3);
    schedule.setSubmittedBy(user);
    schedule.setCreatedAt(ZonedDateTime.now());
    schedule.setUpdatedAt(ZonedDateTime.now());
    court.setScheduleList(List.of(schedule));

    SVCourtPrice price = new SVCourtPrice();
    price.setId(priceId);
    price.setCourtId(entityId);
    price.setPrice(150.0);
    price.setDuration(90);
    price.setUpvotes(3);
    price.setSubmittedBy(user);
    price.setCreatedAt(ZonedDateTime.now());
    price.setUpdatedAt(ZonedDateTime.now());
    court.setPriceList(List.of(price));

    SVCourtResponse response = mapper.toCourtResponse(court);

    assertNotNull(response);
    assertEquals(entityId, response.getId());
    assertEquals("Test Court", response.getName());
    assertEquals("Test Location", response.getLocation());
    assertEquals("Test Description", response.getDescription());
    assertNotNull(response.getOwner());
    assertEquals(userId, response.getOwner().getId());

    assertNotNull(response.getScheduleList());
    assertEquals(1, response.getScheduleList().size());
    SVCourtScheduleResponse scheduleResponse = response.getScheduleList().get(0);
    assertEquals(scheduleId, scheduleResponse.getId());
    assertEquals(entityId, scheduleResponse.getParentEntityId());
    assertEquals(2, scheduleResponse.getDayOfWeek());
    assertEquals("10:00", scheduleResponse.getStartTime());
    assertEquals("22:00", scheduleResponse.getEndTime());
    assertEquals(3, scheduleResponse.getUpvotes());

    assertNotNull(response.getPriceList());
    assertEquals(1, response.getPriceList().size());
    SVCourtPriceResponse priceResponse = response.getPriceList().get(0);
    assertEquals(priceId, priceResponse.getId());
    assertEquals(entityId, priceResponse.getParentEntityId());
    assertEquals(150.0, priceResponse.getPrice());
    assertEquals(3, priceResponse.getUpvotes());
  }

  @Test
  void testStringerResponseMapping() {
    SVStringer stringer = new SVStringer();
    stringer.setId(entityId);
    stringer.setName("Test Stringer");
    stringer.setLocation("Test Location");
    stringer.setDescription("Test Description");
    stringer.setOwner(user);
    stringer.setCreator(user);
    stringer.setCreatedAt(ZonedDateTime.now());
    stringer.setUpdatedAt(ZonedDateTime.now());

    SVStringerPrice price = new SVStringerPrice();
    price.setId(priceId);
    price.setStringerId(entityId);
    price.setStringName("Yonex BG65");
    price.setPrice(30.0);
    price.setUpvotes(10);
    price.setSubmittedBy(user);
    price.setCreatedAt(ZonedDateTime.now());
    price.setUpdatedAt(ZonedDateTime.now());
    stringer.setPriceList(List.of(price));

    SVStringerResponse response = mapper.toStringerResponse(stringer);

    assertNotNull(response);
    assertEquals(entityId, response.getId());
    assertEquals("Test Stringer", response.getName());
    assertEquals("Test Location", response.getLocation());
    assertEquals("Test Description", response.getDescription());
    assertNotNull(response.getOwner());
    assertEquals(userId, response.getOwner().getId());

    assertNotNull(response.getPriceList());
    assertEquals(1, response.getPriceList().size());
    SVStringerPriceResponse priceResponse = response.getPriceList().get(0);
    assertEquals(priceId, priceResponse.getId());
    assertEquals(entityId, priceResponse.getParentEntityId());
    assertEquals(30.0, priceResponse.getPrice());
    assertEquals(10, priceResponse.getUpvotes());
  }

  @Test
  void testCoachScheduleMapping() {
    SVCoachSchedule schedule = new SVCoachSchedule();
    schedule.setId(scheduleId);
    schedule.setCoachId(entityId);
    schedule.setDayOfWeek(1);
    schedule.setStartTime("09:00");
    schedule.setEndTime("17:00");
    schedule.setUpvotes(5);
    schedule.setSubmittedBy(user);
    schedule.setCreatedAt(ZonedDateTime.now());
    schedule.setUpdatedAt(ZonedDateTime.now());

    SVCoachScheduleResponse response = mapper.toCoachScheduleResponse(schedule);

    assertNotNull(response);
    assertEquals(scheduleId, response.getId());
    assertEquals(entityId, response.getParentEntityId());
    assertEquals(1, response.getDayOfWeek());
    assertEquals("09:00", response.getStartTime());
    assertEquals("17:00", response.getEndTime());
    assertEquals(5, response.getUpvotes());
    assertNotNull(response.getSubmittedBy());
    assertEquals(userId, response.getSubmittedBy().getId());
  }

  @Test
  void testCourtScheduleMapping() {
    SVCourtSchedule schedule = new SVCourtSchedule();
    schedule.setId(scheduleId);
    schedule.setCourtId(entityId);
    schedule.setDayOfWeek(2);
    schedule.setOpenTime("10:00");
    schedule.setCloseTime("22:00");
    schedule.setUpvotes(3);
    schedule.setSubmittedBy(user);
    schedule.setCreatedAt(ZonedDateTime.now());
    schedule.setUpdatedAt(ZonedDateTime.now());

    SVCourtScheduleResponse response = mapper.toCourtScheduleResponse(schedule);

    assertNotNull(response);
    assertEquals(scheduleId, response.getId());
    assertEquals(entityId, response.getParentEntityId());
    assertEquals(2, response.getDayOfWeek());
    assertEquals("10:00", response.getStartTime());
    assertEquals("22:00", response.getEndTime());
    assertEquals(3, response.getUpvotes());
    assertNotNull(response.getSubmittedBy());
    assertEquals(userId, response.getSubmittedBy().getId());
  }
}