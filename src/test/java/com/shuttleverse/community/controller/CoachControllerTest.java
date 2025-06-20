package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.dto.CoachCreationData;
import com.shuttleverse.community.dto.CoachPriceResponse;
import com.shuttleverse.community.dto.CoachResponse;
import com.shuttleverse.community.dto.CoachScheduleResponse;
import com.shuttleverse.community.mapper.MapStructMapper;
import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachPrice;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.CoachService;
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
class CoachControllerTest {

  @Mock
  private CoachService coachService;

  @Mock
  private UserService userService;

  @Mock
  private MapStructMapper mapper;

  @InjectMocks
  private CoachController coachController;

  private Coach coach;
  private CoachSchedule schedule;
  private CoachPrice price;
  private User user;
  private UUID coachId;
  private UUID scheduleId;
  private UUID priceId;
  private Jwt jwt;
  private CoachResponse coachResponse;
  private CoachScheduleResponse scheduleResponse;
  private CoachPriceResponse priceResponse;
  private CoachCreationData coachCreationData;

  @BeforeEach
  void setUp() {
    coachId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();
    priceId = UUID.randomUUID();

    user = new User();
    user.setId(userId);
    user.setUsername("testuser");

    coach = new Coach();
    coach.setId(coachId);
    coach.setName("Test Coach");
    coach.setOwner(user);

    coachCreationData = new CoachCreationData();
    coachCreationData.setName("Test Coach");
    coachCreationData.setLocation("Test Location");
    coachCreationData.setDescription("Test Description");
    coachCreationData.setOtherContacts("Test Contacts");
    coachCreationData.setPhoneNumber("1234567890");

    schedule = new CoachSchedule();
    schedule.setId(scheduleId);
    schedule.setDayOfWeek(1);
    schedule.setStartTime("09:00");
    schedule.setEndTime("17:00");
    schedule.setSubmittedBy(user);
    schedule.setUpvotes(0);
    schedule.setVerified(false);

    price = new CoachPrice();
    price.setId(priceId);
    price.setPrice(100.0);
    price.setDuration(60);
    price.setSubmittedBy(user);
    price.setUpvotes(0);
    price.setIsVerified(false);

    coachResponse = new CoachResponse();
    coachResponse.setId(coachId);
    coachResponse.setName("Test Coach");

    scheduleResponse = new CoachScheduleResponse();
    scheduleResponse.setId(scheduleId);
    scheduleResponse.setDayOfWeek(1);
    scheduleResponse.setStartTime("09:00");
    scheduleResponse.setEndTime("17:00");

    priceResponse = new CoachPriceResponse();
    priceResponse.setId(priceId);
    priceResponse.setPrice(100.0);
    priceResponse.setDuration(60);

    jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "user-123")
        .build();
  }

  @Test
  void createCoach_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(mapper.toCoach(any(CoachCreationData.class))).thenReturn(coach);
    when(coachService.createCoach(any(Coach.class), any(User.class))).thenReturn(coach);
    when(mapper.toCoachResponse(any(Coach.class))).thenReturn(coachResponse);

    ResponseEntity<ApiResponse<CoachResponse>> response = coachController.createCoach(
        coachCreationData, jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(coachResponse, response.getBody().getData());
    verify(mapper).toCoach(any(CoachCreationData.class));
    verify(coachService).createCoach(any(Coach.class), any(User.class));
  }

  @Test
  void getCoach_Success() {
    when(coachService.getCoach(any(UUID.class))).thenReturn(coach);
    when(mapper.toCoachResponse(any(Coach.class))).thenReturn(coachResponse);

    ResponseEntity<ApiResponse<CoachResponse>> response = coachController.getCoach(
        coachId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(coachResponse, response.getBody().getData());
    verify(coachService).getCoach(coachId);
  }

  @Test
  void updateCoach_Success() {
    when(coachService.updateCoach(any(UUID.class), any(Coach.class))).thenReturn(coach);
    when(mapper.toCoachResponse(any(Coach.class))).thenReturn(coachResponse);

    ResponseEntity<ApiResponse<CoachResponse>> response = coachController.updateCoach(
        coachId.toString(),
        coach);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(coachResponse, response.getBody().getData());
    verify(coachService).updateCoach(coachId, coach);
  }

  @Test
  void deleteCoach_Success() {
    doNothing().when(coachService).deleteCoach(any(UUID.class));

    ResponseEntity<ApiResponse<Void>> response = coachController.deleteCoach(coachId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    verify(coachService).deleteCoach(coachId);
  }

  @Test
  void addSchedule_Success() {
    List<CoachSchedule> schedules = List.of(schedule);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(schedules).when(coachService)
        .addSchedule(any(User.class), any(UUID.class), any(List.class));
    when(mapper.toCoachScheduleResponse(any(CoachSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<ApiResponse<List<CoachScheduleResponse>>> response = coachController.addSchedule(
        coachId.toString(),
        schedules,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(scheduleResponse, response.getBody().getData().get(0));
    verify(coachService).addSchedule(any(User.class), any(UUID.class), any(List.class));
  }

  @Test
  void updateSchedule_Success() {
    when(coachService.updateSchedule(any(UUID.class), any(UUID.class),
        any(CoachSchedule.class))).thenReturn(schedule);
    when(mapper.toCoachScheduleResponse(any(CoachSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<ApiResponse<CoachScheduleResponse>> response = coachController.updateSchedule(
        coachId.toString(),
        scheduleId.toString(),
        schedule);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(scheduleResponse, response.getBody().getData());
    verify(coachService).updateSchedule(coachId, scheduleId, schedule);
  }

  @Test
  void upvoteSchedule_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(coachService.upvoteSchedule(any(UUID.class), any(User.class))).thenReturn(schedule);
    when(mapper.toCoachScheduleResponse(any(CoachSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<ApiResponse<CoachScheduleResponse>> response = coachController.upvoteSchedule(
        scheduleId.toString(), jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(scheduleResponse, response.getBody().getData());
    verify(coachService).upvoteSchedule(scheduleId, user);
  }

  @Test
  void addPrice_Success() {
    List<CoachPrice> prices = List.of(price);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(prices).when(coachService).addPrice(any(User.class), any(UUID.class), any(List.class));
    when(mapper.toCoachPriceResponse(any(CoachPrice.class))).thenReturn(priceResponse);

    ResponseEntity<ApiResponse<List<CoachPriceResponse>>> response = coachController.addPrice(
        coachId.toString(),
        prices,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(priceResponse, response.getBody().getData().get(0));
    verify(coachService).addPrice(any(User.class), any(UUID.class), any(List.class));
  }

  @Test
  void updatePrice_Success() {
    when(coachService.updatePrice(any(UUID.class), any(UUID.class),
        any(CoachPrice.class))).thenReturn(price);
    when(mapper.toCoachPriceResponse(any(CoachPrice.class))).thenReturn(priceResponse);

    ResponseEntity<ApiResponse<CoachPriceResponse>> response = coachController.updatePrice(
        coachId.toString(),
        priceId.toString(),
        price);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(priceResponse, response.getBody().getData());
    verify(coachService).updatePrice(coachId, priceId, price);
  }

  @Test
  void upvotePrice_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(coachService.upvotePrice(any(UUID.class), any(User.class))).thenReturn(price);
    when(mapper.toCoachPriceResponse(any(CoachPrice.class))).thenReturn(priceResponse);

    ResponseEntity<ApiResponse<CoachPriceResponse>> response = coachController.upvotePrice(
        priceId.toString(), jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(priceResponse, response.getBody().getData());
    verify(coachService).upvotePrice(priceId, user);
  }
}