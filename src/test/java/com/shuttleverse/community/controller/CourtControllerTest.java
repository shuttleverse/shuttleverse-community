package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.api.ApiResponse;
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

  @InjectMocks
  private CourtController courtController;

  private Court court;
  private CourtSchedule schedule;
  private User user;
  private UUID courtId;
  private UUID scheduleId;
  private Jwt jwt;

  @BeforeEach
  void setUp() {
    courtId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();

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

    jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "user-123")
        .build();
  }

  @Test
  void createCourt_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(courtService.createCourt(any(User.class), any(Court.class))).thenReturn(court);

    ResponseEntity<ApiResponse<Court>> response = courtController.createCourt(court, jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(court, response.getBody().getData());
    verify(courtService).createCourt(any(User.class), any(Court.class));
  }

  @Test
  void getCourt_Success() {
    when(courtService.getCourt(any(UUID.class))).thenReturn(court);

    ResponseEntity<ApiResponse<Court>> response = courtController.getCourt(courtId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(court, response.getBody().getData());
    verify(courtService).getCourt(courtId);
  }

  @Test
  void updateCourt_Success() {
    when(courtService.updateCourt(any(UUID.class), any(Court.class))).thenReturn(court);

    ResponseEntity<ApiResponse<Court>> response = courtController.updateCourt(courtId.toString(),
        court);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(court, response.getBody().getData());
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
    doReturn(List.of(schedule)).when(courtService).addSchedule(any(UUID.class), any(List.class));

    ResponseEntity<ApiResponse<List<CourtSchedule>>> response = courtController.addSchedule(
        courtId.toString(),
        List.of(schedule));

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(schedule, response.getBody().getData().get(0));
    verify(courtService).addSchedule(any(UUID.class), any(List.class));
  }

  @Test
  void updateSchedule_Success() {
    when(courtService.updateSchedule(any(UUID.class), any(UUID.class), any(CourtSchedule.class)))
        .thenReturn(schedule);

    ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.updateSchedule(
        courtId.toString(),
        scheduleId.toString(),
        schedule);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(schedule, response.getBody().getData());
    verify(courtService).updateSchedule(courtId, scheduleId, schedule);
  }

  @Test
  void upvoteSchedule_Success() {
    when(courtService.upvoteSchedule(any(UUID.class))).thenReturn(schedule);

    ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.upvoteSchedule(
        courtId.toString(),
        scheduleId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(schedule, response.getBody().getData());
    verify(courtService).upvoteSchedule(scheduleId);
  }

  @Test
  void addCourtWithPriceAndSchedule_Success() {
    CourtPrice price = new CourtPrice();
    price.setId(UUID.randomUUID());
    price.setPrice(30.0);
    price.setDuration(60);
    price.setUpvotes(0);
    price.setIsVerified(false);
    price.setCourt(court);

    CourtSchedule schedule = new CourtSchedule();
    schedule.setId(UUID.randomUUID());
    schedule.setDayOfWeek(1);
    schedule.setOpenTime("09:00");
    schedule.setCloseTime("22:00");
    schedule.setUpvotes(0);
    schedule.setVerified(false);
    schedule.setSubmittedBy(user);
    schedule.setCourt(court);

    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(courtService.createCourt(any(User.class), any(Court.class))).thenReturn(court);
    doReturn(List.of(schedule)).when(courtService).addSchedule(any(UUID.class), any(List.class));

    ResponseEntity<ApiResponse<Court>> courtResponse = courtController.createCourt(court, jwt);
    assertTrue(Objects.requireNonNull(courtResponse.getBody()).isSuccess());
    assertEquals(court, courtResponse.getBody().getData());

    ResponseEntity<ApiResponse<List<CourtSchedule>>> scheduleResponse = courtController.addSchedule(
        courtId.toString(), List.of(schedule));
    assertTrue(Objects.requireNonNull(scheduleResponse.getBody()).isSuccess());
    assertEquals(1, scheduleResponse.getBody().getData().size());
    assertEquals(schedule, scheduleResponse.getBody().getData().get(0));

    verify(courtService).createCourt(any(User.class), any(Court.class));
    verify(courtService).addSchedule(any(UUID.class), any(List.class));
  }

  @Test
  void upvoteCourtSchedule_Success() {
    CourtSchedule schedule = new CourtSchedule();
    schedule.setId(scheduleId);
    schedule.setDayOfWeek(1);
    schedule.setOpenTime("09:00");
    schedule.setCloseTime("22:00");
    schedule.setUpvotes(1); // After upvote
    schedule.setVerified(false);
    schedule.setSubmittedBy(user);
    schedule.setCourt(court);

    when(courtService.upvoteSchedule(any(UUID.class))).thenReturn(schedule);

    ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.upvoteSchedule(
        courtId.toString(),
        scheduleId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(schedule, response.getBody().getData());
    assertEquals(1, response.getBody().getData().getUpvotes());
    verify(courtService).upvoteSchedule(scheduleId);
  }

  @Test
  void addNewScheduleForExistingCourt_Success() {
    CourtSchedule newSchedule = new CourtSchedule();
    newSchedule.setDayOfWeek(2);
    newSchedule.setOpenTime("10:00");
    newSchedule.setCloseTime("21:00");
    newSchedule.setUpvotes(0);
    newSchedule.setVerified(false);
    newSchedule.setSubmittedBy(user);
    newSchedule.setCourt(court);

    doReturn(List.of(newSchedule)).when(courtService).addSchedule(any(UUID.class), any(List.class));

    ResponseEntity<ApiResponse<List<CourtSchedule>>> response = courtController.addSchedule(
        courtId.toString(),
        List.of(newSchedule));

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(newSchedule, response.getBody().getData().get(0));
    verify(courtService).addSchedule(any(UUID.class), any(List.class));
  }

  @Test
  void addPrice_Success() {
    CourtPrice price = new CourtPrice();
    price.setId(UUID.randomUUID());
    price.setPrice(30.0);
    price.setDuration(60);
    price.setUpvotes(0);
    price.setIsVerified(false);
    price.setCourt(court);

    doReturn(List.of(price)).when(courtService).addPrice(any(UUID.class), any(List.class));

    ResponseEntity<ApiResponse<List<CourtPrice>>> response = courtController.addPrice(
        courtId.toString(),
        List.of(price));

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(price, response.getBody().getData().get(0));
    verify(courtService).addPrice(any(UUID.class), any(List.class));
  }
}