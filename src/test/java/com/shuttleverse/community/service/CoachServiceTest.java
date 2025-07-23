package com.shuttleverse.community.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.SVBaseTest;
import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.repository.CoachRepository;
import com.shuttleverse.community.repository.CoachScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CoachServiceTest extends SVBaseTest {

  @Mock
  private CoachRepository coachRepository;

  @Mock
  private CoachScheduleRepository scheduleRepository;

  @InjectMocks
  private CoachService coachService;

  private Coach coach;
  private CoachSchedule schedule;
  private UUID coachId;
  private UUID scheduleId;

  @BeforeEach
  void setUp() {
    coachId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();

    coach = new Coach();
    coach.setId(coachId);
    coach.setName("Test Coach");
    coach.setOwner(user);

    schedule = new CoachSchedule();
    schedule.setId(scheduleId);
    schedule.setDayOfWeek(1);
    schedule.setStartTime("09:00");
    schedule.setEndTime("22:00");
    schedule.setUpvotes(0);
    schedule.setVerified(false);
    schedule.setSubmittedBy(user);
    schedule.setCoachId(coachId);
  }

  @Test
  void createCoach_Success() {
    when(coachRepository.save(any(Coach.class))).thenReturn(coach);

    Coach result = coachService.createCoach(coach, user);

    assertNotNull(result);
    assertEquals(coach.getId(), result.getId());
    assertEquals(coach.getName(), result.getName());
    assertEquals(user, result.getOwner());
    verify(coachRepository).save(any(Coach.class));
  }

  @Test
  void getCoach_Success() {
    when(coachRepository.findById(any(UUID.class))).thenReturn(Optional.of(coach));

    Coach result = coachService.getCoach(coachId);

    assertNotNull(result);
    assertEquals(coach.getId(), result.getId());
    assertEquals(coach.getName(), result.getName());
    verify(coachRepository).findById(coachId);
  }

  @Test
  void getCoach_NotFound() {
    when(coachRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> coachService.getCoach(coachId));
    verify(coachRepository).findById(coachId);
  }

  @Test
  void addSchedule_Success() {
    UUID coachId = UUID.fromString("c3b5349f-f309-42a8-aecf-9b8bc6ff5932");
    Coach coach = new Coach();
    coach.setId(coachId);
    coach.setOwner(user);
    when(coachRepository.findById(coachId)).thenReturn(Optional.of(coach));
    when(scheduleRepository.saveAll(any())).thenReturn(List.of(schedule));

    List<CoachSchedule> result = coachService.addSchedule(user, coachId, List.of(schedule));

    verify(coachRepository).findById(coachId);
    verify(scheduleRepository).saveAll(any());
    assertEquals(1, result.size());
    assertEquals(schedule, result.get(0));
  }

  @Test
  void upvoteSchedule_Success() {
    when(scheduleRepository.findById(any(UUID.class))).thenReturn(Optional.of(schedule));
    when(scheduleRepository.save(any(CoachSchedule.class))).thenReturn(schedule);

    CoachSchedule result = coachService.upvoteSchedule(scheduleId);

    assertNotNull(result);
    assertEquals(1, result.getUpvotes());
    verify(scheduleRepository).findById(scheduleId);
    verify(scheduleRepository).save(any(CoachSchedule.class));
  }

  @Test
  void upvoteSchedule_NotFound() {
    when(scheduleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> coachService.upvoteSchedule(scheduleId));
    verify(scheduleRepository).findById(scheduleId);
  }

  @Test
  void updateSchedule_Success() {
    when(coachRepository.findById(any(UUID.class))).thenReturn(Optional.of(coach));
    when(scheduleRepository.save(any(CoachSchedule.class))).thenReturn(schedule);

    CoachSchedule result = coachService.updateSchedule(coachId, scheduleId, schedule);

    assertNotNull(result);
    assertEquals(schedule.getId(), result.getId());
    assertEquals(schedule.getDayOfWeek(), result.getDayOfWeek());
    assertEquals(schedule.getStartTime(), result.getStartTime());
    assertEquals(schedule.getEndTime(), result.getEndTime());
    verify(coachRepository, times(2)).findById(coachId);
    verify(scheduleRepository).save(any(CoachSchedule.class));
  }
}