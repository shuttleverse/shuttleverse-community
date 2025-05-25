package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.dto.CourtPriceResponse;
import com.shuttleverse.community.dto.CourtResponse;
import com.shuttleverse.community.dto.CourtScheduleResponse;
import com.shuttleverse.community.mapper.MapStructMapper;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.CourtService;
import com.shuttleverse.community.service.UserService;
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
class CourtControllerTest {

  @Mock
  private CourtService courtService;

  @Mock
  private UserService userService;

  @Mock
  private MapStructMapper mapper;

  @InjectMocks
  private CourtController courtController;

  private Court court;
  private CourtSchedule schedule;
  private CourtPrice price;
  private User user;
  private UUID courtId;
  private UUID scheduleId;
  private UUID priceId;
  private Jwt jwt;
  private CourtResponse courtResponse;
  private CourtScheduleResponse scheduleResponse;
  private CourtPriceResponse priceResponse;

  @BeforeEach
  void setUp() {
    courtId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();
    priceId = UUID.randomUUID();

    user = new User();
    user.setId(userId);
    user.setUsername("testuser");

    court = new Court();
    court.setId(courtId);
    court.setName("Test Court");
    court.setOwner(user);

    schedule = new CourtSchedule();
    schedule.setId(scheduleId);
    schedule.setDayOfWeek(1);
    schedule.setOpenTime("09:00");
    schedule.setCloseTime("17:00");
    schedule.setSubmittedBy(user);
    schedule.setUpvotes(0);
    schedule.setVerified(false);

    price = new CourtPrice();
    price.setId(priceId);
    price.setPrice(100.0);
    price.setDuration(60);
    price.setSubmittedBy(user);
    price.setUpvotes(0);
    price.setIsVerified(false);

    courtResponse = new CourtResponse();
    courtResponse.setId(courtId);
    courtResponse.setName("Test Court");

    scheduleResponse = new CourtScheduleResponse();
    scheduleResponse.setId(scheduleId);
    scheduleResponse.setDayOfWeek(1);
    scheduleResponse.setStartTime("09:00");
    scheduleResponse.setEndTime("17:00");

    priceResponse = new CourtPriceResponse();
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
    when(courtService.createCourt(any(User.class), any(Court.class))).thenReturn(court);
    when(mapper.toCourtResponse(any(Court.class))).thenReturn(courtResponse);

    ResponseEntity<ApiResponse<CourtResponse>> response = courtController.createCourt(court, jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(courtResponse, response.getBody().getData());
    verify(courtService).createCourt(any(User.class), any(Court.class));
  }

  @Test
  void getCourt_Success() {
    when(courtService.getCourt(any(UUID.class))).thenReturn(court);
    when(mapper.toCourtResponse(any(Court.class))).thenReturn(courtResponse);

    ResponseEntity<ApiResponse<CourtResponse>> response = courtController.getCourt(
        courtId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(courtResponse, response.getBody().getData());
    verify(courtService).getCourt(courtId);
  }

  @Test
  void updateCourt_Success() {
    when(courtService.updateCourt(any(UUID.class), any(Court.class))).thenReturn(court);
    when(mapper.toCourtResponse(any(Court.class))).thenReturn(courtResponse);

    ResponseEntity<ApiResponse<CourtResponse>> response = courtController.updateCourt(
        courtId.toString(),
        court);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(courtResponse, response.getBody().getData());
    verify(courtService).updateCourt(courtId, court);
  }

  @Test
  void deleteCourt_Success() {
    doNothing().when(courtService).deleteCourt(any(UUID.class));

    ResponseEntity<ApiResponse<Void>> response = courtController.deleteCourt(courtId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    verify(courtService).deleteCourt(courtId);
  }

  @Test
  void addSchedule_Success() {
    List<CourtSchedule> schedules = List.of(schedule);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(schedules).when(courtService)
        .addSchedule(any(User.class), any(UUID.class), any(List.class));
    when(mapper.toCourtScheduleResponse(any(CourtSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<ApiResponse<List<CourtScheduleResponse>>> response = courtController.addSchedule(
        courtId.toString(),
        schedules,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(scheduleResponse, response.getBody().getData().get(0));
    verify(courtService).addSchedule(any(User.class), any(UUID.class), any(List.class));
  }

  @Test
  void updateSchedule_Success() {
    when(courtService.updateSchedule(any(UUID.class), any(UUID.class),
        any(CourtSchedule.class))).thenReturn(schedule);
    when(mapper.toCourtScheduleResponse(any(CourtSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<ApiResponse<CourtScheduleResponse>> response = courtController.updateSchedule(
        courtId.toString(),
        scheduleId.toString(),
        schedule);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(scheduleResponse, response.getBody().getData());
    verify(courtService).updateSchedule(courtId, scheduleId, schedule);
  }

  @Test
  void upvoteSchedule_Success() {
    when(courtService.upvoteSchedule(any(UUID.class))).thenReturn(schedule);
    when(mapper.toCourtScheduleResponse(any(CourtSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<ApiResponse<CourtScheduleResponse>> response = courtController.upvoteSchedule(
        scheduleId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(scheduleResponse, response.getBody().getData());
    verify(courtService).upvoteSchedule(scheduleId);
  }

  @Test
  void addPrice_Success() {
    List<CourtPrice> prices = List.of(price);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(prices).when(courtService).addPrice(any(User.class), any(UUID.class), any(List.class));
    when(mapper.toCourtPriceResponse(any(CourtPrice.class))).thenReturn(priceResponse);

    ResponseEntity<ApiResponse<List<CourtPriceResponse>>> response = courtController.addPrice(
        courtId.toString(),
        prices,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(priceResponse, response.getBody().getData().get(0));
    verify(courtService).addPrice(any(User.class), any(UUID.class), any(List.class));
  }

  @Test
  void upvotePrice_Success() {
    when(courtService.upvotePrice(any(UUID.class))).thenReturn(price);
    when(mapper.toCourtPriceResponse(any(CourtPrice.class))).thenReturn(priceResponse);

    ResponseEntity<ApiResponse<CourtPriceResponse>> response = courtController.upvotePrice(
        priceId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(priceResponse, response.getBody().getData());
    verify(courtService).upvotePrice(priceId);
  }
}