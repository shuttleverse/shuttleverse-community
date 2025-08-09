package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.SVBaseTest;
import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.dto.SVCourtCreationData;
import com.shuttleverse.community.dto.SVCourtPriceResponse;
import com.shuttleverse.community.dto.SVCourtResponse;
import com.shuttleverse.community.dto.SVCourtScheduleResponse;
import com.shuttleverse.community.mapper.SVMapStructMapper;
import com.shuttleverse.community.model.SVCourt;
import com.shuttleverse.community.model.SVCourtPrice;
import com.shuttleverse.community.model.SVCourtSchedule;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.service.SVCourtService;
import com.shuttleverse.community.service.SVUserService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SVCourtControllerTest extends SVBaseTest {

  @Mock
  private SVCourtService courtService;

  @Mock
  private SVUserService userService;

  @Mock
  private SVMapStructMapper mapper;

  @InjectMocks
  private SVCourtController courtController;

  private SVCourt court;
  private SVCourtSchedule schedule;
  private SVCourtPrice price;
  private UUID courtId;
  private UUID scheduleId;
  private UUID priceId;
  private Jwt jwt;
  private SVCourtResponse courtResponse;
  private SVCourtScheduleResponse scheduleResponse;
  private SVCourtPriceResponse priceResponse;
  private SVCourtCreationData courtCreationData;

  @BeforeEach
  void setUp() {
    courtId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();
    priceId = UUID.randomUUID();

    court = new SVCourt();
    court.setId(courtId);
    court.setName("Test Court");
    court.setOwner(user);

    courtCreationData = new SVCourtCreationData();
    courtCreationData.setName("Test Court");
    courtCreationData.setLocation("Test Location");
    courtCreationData.setDescription("Test Description");
    courtCreationData.setOtherContacts("Test Contacts");
    courtCreationData.setPhoneNumber("1234567890");

    schedule = new SVCourtSchedule();
    schedule.setId(scheduleId);
    schedule.setDayOfWeek(1);
    schedule.setOpenTime("09:00");
    schedule.setCloseTime("17:00");
    schedule.setSubmittedBy(user);
    schedule.setUpvotes(0);
    schedule.setIsVerified(false);

    price = new SVCourtPrice();
    price.setId(priceId);
    price.setPrice(100.0);
    price.setDuration(60);
    price.setSubmittedBy(user);
    price.setUpvotes(0);
    price.setIsVerified(false);

    courtResponse = new SVCourtResponse();
    courtResponse.setId(courtId);
    courtResponse.setName("Test Court");

    scheduleResponse = new SVCourtScheduleResponse();
    scheduleResponse.setId(scheduleId);
    scheduleResponse.setDayOfWeek(1);
    scheduleResponse.setStartTime("09:00");
    scheduleResponse.setEndTime("17:00");

    priceResponse = new SVCourtPriceResponse();
    priceResponse.setId(priceId);
    priceResponse.setPrice(100.0);
    priceResponse.setDuration(60);

    jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "user-123")
        .build();
  }

  @Test
  void createCourt_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(mapper.toCourt(any(SVCourtCreationData.class))).thenReturn(court);
    when(courtService.createCourt(any(SVUser.class), any(SVCourt.class))).thenReturn(court);
    when(mapper.toCourtResponse(any(SVCourt.class))).thenReturn(courtResponse);

    ResponseEntity<SVApiResponse<SVCourtResponse>> response = courtController.createCourt(
        courtCreationData, jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(courtResponse, response.getBody().getData());
    verify(mapper).toCourt(any(SVCourtCreationData.class));
    verify(courtService).createCourt(any(SVUser.class), any(SVCourt.class));
  }

  @Test
  void getCourt_Success() {
    when(courtService.getCourt(any(UUID.class))).thenReturn(court);
    when(mapper.toCourtResponse(any(SVCourt.class))).thenReturn(courtResponse);

    ResponseEntity<SVApiResponse<SVCourtResponse>> response = courtController.getCourt(
        courtId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(courtResponse, response.getBody().getData());
    verify(courtService).getCourt(courtId);
  }

  @Test
  void updateCourt_Success() {
    when(courtService.updateCourt(any(UUID.class), any(SVCourt.class))).thenReturn(court);
    when(mapper.toCourtResponse(any(SVCourt.class))).thenReturn(courtResponse);

    ResponseEntity<SVApiResponse<SVCourtResponse>> response = courtController.updateCourt(
        courtId.toString(),
        court);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(courtResponse, response.getBody().getData());
    verify(courtService).updateCourt(courtId, court);
  }

  @Test
  void deleteCourt_Success() {
    doNothing().when(courtService).deleteCourt(any(UUID.class));

    ResponseEntity<SVApiResponse<Void>> response = courtController.deleteCourt(courtId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    verify(courtService).deleteCourt(courtId);
  }

  @Test
  void addSchedule_Success() {
    List<SVCourtSchedule> schedules = List.of(schedule);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(schedules).when(courtService)
        .addSchedule(any(SVUser.class), any(UUID.class), any(List.class));
    when(mapper.toCourtScheduleResponse(any(SVCourtSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<SVApiResponse<List<SVCourtScheduleResponse>>> response = courtController.addSchedule(
        courtId.toString(),
        schedules,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(scheduleResponse, response.getBody().getData().get(0));
    verify(courtService).addSchedule(any(SVUser.class), any(UUID.class), any(List.class));
  }

  @Test
  void updateSchedule_Success() {
    when(courtService.updateSchedule(any(UUID.class), any(UUID.class),
        any(SVCourtSchedule.class))).thenReturn(schedule);
    when(mapper.toCourtScheduleResponse(any(SVCourtSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<SVApiResponse<SVCourtScheduleResponse>> response = courtController.updateSchedule(
        courtId.toString(),
        scheduleId.toString(),
        schedule);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(scheduleResponse, response.getBody().getData());
    verify(courtService).updateSchedule(courtId, scheduleId, schedule);
  }

  @Test
  void upvoteSchedule_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(courtService.upvoteSchedule(any(UUID.class), any(SVUser.class))).thenReturn(schedule);
    when(mapper.toCourtScheduleResponse(any(SVCourtSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<SVApiResponse<SVCourtScheduleResponse>> response = courtController.upvoteSchedule(
        scheduleId.toString(), jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(scheduleResponse, response.getBody().getData());
    verify(courtService).upvoteSchedule(scheduleId, user);
  }

  @Test
  void addPrice_Success() {
    List<SVCourtPrice> prices = List.of(price);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(prices).when(courtService)
        .addPrice(any(SVUser.class), any(UUID.class), any(List.class));
    when(mapper.toCourtPriceResponse(any(SVCourtPrice.class))).thenReturn(priceResponse);

    ResponseEntity<SVApiResponse<List<SVCourtPriceResponse>>> response = courtController.addPrice(
        courtId.toString(),
        prices,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(priceResponse, response.getBody().getData().get(0));
    verify(courtService).addPrice(any(SVUser.class), any(UUID.class), any(List.class));
  }

  @Test
  void upvotePrice_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(courtService.upvotePrice(any(UUID.class), any(SVUser.class))).thenReturn(price);
    when(mapper.toCourtPriceResponse(any(SVCourtPrice.class))).thenReturn(priceResponse);

    ResponseEntity<SVApiResponse<SVCourtPriceResponse>> response = courtController.upvotePrice(
        priceId.toString(), jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(priceResponse, response.getBody().getData());
    verify(courtService).upvotePrice(priceId, user);
  }
}