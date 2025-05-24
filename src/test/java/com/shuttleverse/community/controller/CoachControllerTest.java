package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Coach;
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

  @InjectMocks
  private CoachController coachController;

  private Coach coach;
  private CoachSchedule schedule;
  private User user;
  private UUID coachId;
  private UUID scheduleId;
  private Jwt jwt;

  @BeforeEach
  void setUp() {
    coachId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();

    user = new User();
    user.setId(userId);
    user.setUsername("testuser");

    coach = new Coach();
    coach.setId(coachId);
    coach.setName("Test Coach");
    coach.setOwner(user);

    schedule = new CoachSchedule();
    schedule.setId(scheduleId);
    schedule.setDayOfWeek(1);
    schedule.setStartTime("09:00");
    schedule.setEndTime("17:00");
    schedule.setSubmittedBy(user);
    schedule.setUpvotes(0);
    schedule.setVerified(false);

    // Mock JWT token
    jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "user-123")
        .build();
  }

  @Test
  void createCoach_Success() {
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(coachService.createCoach(any(Coach.class), any(User.class))).thenReturn(coach);

    ResponseEntity<ApiResponse<Coach>> response = coachController.createCoach(coach, jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(coach, response.getBody().getData());
    verify(coachService).createCoach(any(Coach.class), any(User.class));
  }

  @Test
  void getCoach_Success() {
    when(coachService.getCoach(any(UUID.class))).thenReturn(coach);

    ResponseEntity<ApiResponse<Coach>> response = coachController.getCoach(coachId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(coach, response.getBody().getData());
    verify(coachService).getCoach(coachId);
  }

  @Test
  void updateCoach_Success() {
    when(coachService.updateCoach(any(UUID.class), any(Coach.class))).thenReturn(coach);

    ResponseEntity<ApiResponse<Coach>> response = coachController.updateCoach(coachId.toString(),
        coach);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(coach, response.getBody().getData());
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

    ResponseEntity<ApiResponse<List<CoachSchedule>>> response = coachController.addSchedule(
        coachId.toString(),
        schedules,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(schedule, response.getBody().getData().get(0));
    verify(coachService).addSchedule(any(User.class), any(UUID.class), any(List.class));
  }

  @Test
  void updateSchedule_Success() {
    when(coachService.updateSchedule(any(UUID.class), any(UUID.class),
        any(CoachSchedule.class))).thenReturn(
        schedule);

    ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.updateSchedule(
        coachId.toString(),
        scheduleId.toString(),
        schedule);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(schedule, response.getBody().getData());
    verify(coachService).updateSchedule(coachId, scheduleId, schedule);
  }

  @Test
  void upvoteSchedule_Success() {
    when(coachService.upvoteSchedule(any(UUID.class))).thenReturn(schedule);

    ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.upvoteSchedule(
        scheduleId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(schedule, response.getBody().getData());
    verify(coachService).upvoteSchedule(scheduleId);
  }

  @Test
  void addCoachWithSchedule_Success() {
    CoachSchedule schedule = new CoachSchedule();
    schedule.setId(UUID.randomUUID());
    schedule.setDayOfWeek(1);
    schedule.setStartTime("09:00");
    schedule.setEndTime("22:00");
    schedule.setUpvotes(0);
    schedule.setVerified(false);
    schedule.setSubmittedBy(user);
    schedule.setCoach(coach);

    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    when(coachService.createCoach(any(Coach.class), any(User.class))).thenReturn(coach);
    doReturn(List.of(schedule)).when(coachService)
        .addSchedule(any(User.class), any(UUID.class), any(List.class));

    ResponseEntity<ApiResponse<Coach>> coachResponse = coachController.createCoach(coach, jwt);
    assertTrue(Objects.requireNonNull(coachResponse.getBody()).isSuccess());
    assertEquals(coach, coachResponse.getBody().getData());

    ResponseEntity<ApiResponse<List<CoachSchedule>>> scheduleResponse = coachController.addSchedule(
        coachId.toString(),
        List.of(schedule),
        jwt);
    assertTrue(Objects.requireNonNull(scheduleResponse.getBody()).isSuccess());
    assertEquals(1, scheduleResponse.getBody().getData().size());
    assertEquals(schedule, scheduleResponse.getBody().getData().get(0));

    verify(coachService).createCoach(any(Coach.class), any(User.class));
    verify(coachService).addSchedule(any(User.class), any(UUID.class), any(List.class));
  }

  @Test
  void upvoteCoachSchedule_Success() {
    CoachSchedule schedule = new CoachSchedule();
    schedule.setId(scheduleId);
    schedule.setDayOfWeek(1);
    schedule.setStartTime("09:00");
    schedule.setEndTime("22:00");
    schedule.setUpvotes(1);
    schedule.setVerified(false);
    schedule.setSubmittedBy(user);
    schedule.setCoach(coach);

    when(coachService.upvoteSchedule(any(UUID.class))).thenReturn(schedule);

    ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.upvoteSchedule(
        scheduleId.toString());

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(schedule, response.getBody().getData());
    assertEquals(1, response.getBody().getData().getUpvotes());
    verify(coachService).upvoteSchedule(scheduleId);
  }

  @Test
  void addNewScheduleForExistingCoach_Success() {
    CoachSchedule newSchedule = new CoachSchedule();
    newSchedule.setDayOfWeek(2); // Tuesday
    newSchedule.setStartTime("10:00");
    newSchedule.setEndTime("21:00");
    newSchedule.setUpvotes(0);
    newSchedule.setVerified(false);
    newSchedule.setSubmittedBy(user);
    newSchedule.setCoach(coach);

    List<CoachSchedule> schedules = List.of(newSchedule);
    when(userService.findBySub(any(String.class))).thenReturn(Optional.of(user));
    doReturn(schedules).when(coachService)
        .addSchedule(any(User.class), any(UUID.class), any(List.class));

    ResponseEntity<ApiResponse<List<CoachSchedule>>> response = coachController.addSchedule(
        coachId.toString(),
        schedules,
        jwt);

    assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    assertEquals(1, response.getBody().getData().size());
    assertEquals(newSchedule, response.getBody().getData().get(0));
    verify(coachService).addSchedule(any(User.class), any(UUID.class), any(List.class));
  }
}