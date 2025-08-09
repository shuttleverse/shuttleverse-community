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
import com.shuttleverse.community.dto.SVCoachCreationData;
import com.shuttleverse.community.dto.SVCoachPriceResponse;
import com.shuttleverse.community.dto.SVCoachResponse;
import com.shuttleverse.community.dto.SVCoachScheduleResponse;
import com.shuttleverse.community.mapper.SVMapStructMapper;
import com.shuttleverse.community.model.SVCoach;
import com.shuttleverse.community.model.SVCoachPrice;
import com.shuttleverse.community.model.SVCoachSchedule;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.service.SVCoachService;
import com.shuttleverse.community.service.SVUserService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
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
class SVCoachControllerTest extends SVBaseTest {

  @Mock
  private SVCoachService coachService;

  @Mock
  private SVUserService userService;

  @Mock
  private SVMapStructMapper mapper;

  @InjectMocks
  private SVCoachController coachController;

  private SVCoach coach;
  private SVCoachSchedule schedule;
  private SVCoachPrice price;
  private UUID coachId;
  private UUID scheduleId;
  private UUID priceId;
  private Jwt jwt;
  private SVCoachResponse coachResponse;
  private SVCoachScheduleResponse scheduleResponse;
  private SVCoachPriceResponse priceResponse;
  private SVCoachCreationData coachCreationData;

  @BeforeEach
  void setUp() {
    coachId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();
    priceId = UUID.randomUUID();

    coach = new SVCoach();
    coach.setId(coachId);
    coach.setName("Test Coach");
    coach.setOwner(user);

    coachCreationData = new SVCoachCreationData();
    coachCreationData.setName("Test Coach");
    coachCreationData.setLocation("Test Location");
    coachCreationData.setDescription("Test Description");
    coachCreationData.setOtherContacts("Test Contacts");
    coachCreationData.setPhoneNumber("1234567890");

    schedule = new SVCoachSchedule();
    schedule.setId(scheduleId);
    schedule.setDayOfWeek(1);
    schedule.setStartTime("09:00");
    schedule.setEndTime("17:00");
    schedule.setSubmittedBy(user);
    schedule.setUpvotes(0);
    schedule.setIsVerified(false);

    price = new SVCoachPrice();
    price.setId(priceId);
    price.setPrice(100.0);
    price.setDuration(60);
    price.setSubmittedBy(user);
    price.setUpvotes(0);
    price.setIsVerified(false);

    coachResponse = new SVCoachResponse();
    coachResponse.setId(coachId);
    coachResponse.setName("Test Coach");

    scheduleResponse = new SVCoachScheduleResponse();
    scheduleResponse.setId(scheduleId);
    scheduleResponse.setDayOfWeek(1);
    scheduleResponse.setStartTime("09:00");
    scheduleResponse.setEndTime("17:00");

    priceResponse = new SVCoachPriceResponse();
    priceResponse.setId(priceId);
    priceResponse.setPrice(100.0);
    priceResponse.setDuration(60);

    jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "user-123")
        .build();
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void createCoach_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(mapper.toCoach(any(SVCoachCreationData.class))).thenReturn(coach);
    when(coachService.createCoach(any(SVCoach.class), any(SVUser.class))).thenReturn(coach);
    when(mapper.toCoachResponse(any(SVCoach.class))).thenReturn(coachResponse);

    ResponseEntity<SVApiResponse<SVCoachResponse>> response = coachController.createCoach(
        coachCreationData, jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(coachResponse, response.getBody().getData());
    verify(mapper).toCoach(any(SVCoachCreationData.class));
    verify(coachService).createCoach(any(SVCoach.class), any(SVUser.class));
  }

  @Test
  void getCoach_Success() {
    when(coachService.getCoach(any(UUID.class))).thenReturn(coach);
    when(mapper.toCoachResponse(any(SVCoach.class))).thenReturn(coachResponse);

    ResponseEntity<SVApiResponse<SVCoachResponse>> response = coachController.getCoach(
        coachId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(coachResponse, response.getBody().getData());
    verify(coachService).getCoach(coachId);
  }

  @Test
  void updateCoach_Success() {
    when(coachService.updateCoach(any(UUID.class), any(SVCoach.class))).thenReturn(coach);
    when(mapper.toCoachResponse(any(SVCoach.class))).thenReturn(coachResponse);

    ResponseEntity<SVApiResponse<SVCoachResponse>> response = coachController.updateCoach(
        coachId.toString(),
        coach);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(coachResponse, response.getBody().getData());
    verify(coachService).updateCoach(coachId, coach);
  }

  @Test
  void deleteCoach_Success() {
    doNothing().when(coachService).deleteCoach(any(UUID.class));

    ResponseEntity<SVApiResponse<Void>> response = coachController.deleteCoach(coachId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    verify(coachService).deleteCoach(coachId);
  }

  @Test
  void addSchedule_Success() {
    List<SVCoachSchedule> schedules = List.of(schedule);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(schedules).when(coachService)
        .addSchedule(any(SVUser.class), any(UUID.class), any(List.class));
    when(mapper.toCoachScheduleResponse(any(SVCoachSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<SVApiResponse<List<SVCoachScheduleResponse>>> response = coachController.addSchedule(
        coachId.toString(),
        schedules,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(scheduleResponse, response.getBody().getData().get(0));
    verify(coachService).addSchedule(any(SVUser.class), any(UUID.class), any(List.class));
  }

  @Test
  void updateSchedule_Success() {
    when(coachService.updateSchedule(any(UUID.class), any(UUID.class),
        any(SVCoachSchedule.class))).thenReturn(schedule);
    when(mapper.toCoachScheduleResponse(any(SVCoachSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<SVApiResponse<SVCoachScheduleResponse>> response = coachController.updateSchedule(
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
    when(coachService.upvoteSchedule(any(UUID.class), any(SVUser.class))).thenReturn(schedule);
    when(mapper.toCoachScheduleResponse(any(SVCoachSchedule.class))).thenReturn(scheduleResponse);

    ResponseEntity<SVApiResponse<SVCoachScheduleResponse>> response = coachController.upvoteSchedule(
        scheduleId.toString(), jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(scheduleResponse, response.getBody().getData());
    verify(coachService).upvoteSchedule(scheduleId, user);
  }

  @Test
  void addPrice_Success() {
    List<SVCoachPrice> prices = List.of(price);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(prices).when(coachService)
        .addPrice(any(SVUser.class), any(UUID.class), any(List.class));
    when(mapper.toCoachPriceResponse(any(SVCoachPrice.class))).thenReturn(priceResponse);

    ResponseEntity<SVApiResponse<List<SVCoachPriceResponse>>> response = coachController.addPrice(
        coachId.toString(),
        prices,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(priceResponse, response.getBody().getData().get(0));
    verify(coachService).addPrice(any(SVUser.class), any(UUID.class), any(List.class));
  }

  @Test
  void updatePrice_Success() {
    when(coachService.updatePrice(any(UUID.class), any(UUID.class),
        any(SVCoachPrice.class))).thenReturn(price);
    when(mapper.toCoachPriceResponse(any(SVCoachPrice.class))).thenReturn(priceResponse);

    ResponseEntity<SVApiResponse<SVCoachPriceResponse>> response = coachController.updatePrice(
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
    when(coachService.upvotePrice(any(UUID.class), any(SVUser.class))).thenReturn(price);
    when(mapper.toCoachPriceResponse(any(SVCoachPrice.class))).thenReturn(priceResponse);

    ResponseEntity<SVApiResponse<SVCoachPriceResponse>> response = coachController.upvotePrice(
        priceId.toString(), jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(priceResponse, response.getBody().getData());
    verify(coachService).upvotePrice(priceId, user);
  }
}