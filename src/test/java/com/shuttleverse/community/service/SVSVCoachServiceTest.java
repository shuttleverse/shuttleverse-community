package com.shuttleverse.community.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.SVBaseTest;
import com.shuttleverse.community.model.SVCoach;
import com.shuttleverse.community.model.SVCoachSchedule;
import com.shuttleverse.community.repository.SVCoachRepository;
import com.shuttleverse.community.repository.SVCoachScheduleRepository;
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
class SVSVCoachServiceTest extends SVBaseTest {

  @Mock
  private SVCoachRepository coachRepository;

  @Mock
  private SVCoachScheduleRepository scheduleRepository;

  @InjectMocks
  private SVCoachService coachService;

  private SVCoach coach;
  private SVCoachSchedule schedule;
  private UUID coachId;
  private UUID scheduleId;

  @BeforeEach
  void setUp() {
    coachId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();

    coach = new SVCoach();
    coach.setId(coachId);
    coach.setName("Test Coach");
    coach.setOwner(user);

    schedule = new SVCoachSchedule();
    schedule.setId(scheduleId);
    schedule.setDayOfWeek(1);
    schedule.setStartTime("09:00");
    schedule.setEndTime("22:00");
    schedule.setUpvotes(0);
    schedule.setIsVerified(false);
    schedule.setSubmittedBy(user);
    schedule.setCoachId(coachId);
  }

  @Test
  void createCoach_Success() {
    when(coachRepository.save(any(SVCoach.class))).thenReturn(coach);

    SVCoach result = coachService.createCoach(coach, user);

    assertNotNull(result);
    assertEquals(coach.getId(), result.getId());
    assertEquals(coach.getName(), result.getName());
    assertEquals(user, result.getOwner());
    verify(coachRepository).save(any(SVCoach.class));
  }

  @Test
  void getCoach_Success() {
    when(coachRepository.findById(any(UUID.class))).thenReturn(Optional.of(coach));

    SVCoach result = coachService.getCoach(coachId);

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
    SVCoach coach = new SVCoach();
    coach.setId(coachId);
    coach.setOwner(user);
    when(coachRepository.findById(coachId)).thenReturn(Optional.of(coach));
    when(scheduleRepository.saveAll(any())).thenReturn(List.of(schedule));

    List<SVCoachSchedule> result = coachService.addSchedule(user, coachId, List.of(schedule));

    verify(coachRepository).findById(coachId);
    verify(scheduleRepository).saveAll(any());
    assertEquals(1, result.size());
    assertEquals(schedule, result.get(0));
  }

  @Test
  void upvoteSchedule_Success() {
    when(scheduleRepository.findById(any(UUID.class))).thenReturn(Optional.of(schedule));
    when(scheduleRepository.save(any(SVCoachSchedule.class))).thenReturn(schedule);

    SVCoachSchedule result = coachService.upvoteSchedule(scheduleId);

    assertNotNull(result);
    assertEquals(1, result.getUpvotes());
    verify(scheduleRepository).findById(scheduleId);
    verify(scheduleRepository).save(any(SVCoachSchedule.class));
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
    when(scheduleRepository.save(any(SVCoachSchedule.class))).thenReturn(schedule);

    SVCoachSchedule result = coachService.updateSchedule(coachId, scheduleId, schedule);

    assertNotNull(result);
    assertEquals(schedule.getId(), result.getId());
    assertEquals(schedule.getDayOfWeek(), result.getDayOfWeek());
    assertEquals(schedule.getStartTime(), result.getStartTime());
    assertEquals(schedule.getEndTime(), result.getEndTime());
    verify(coachRepository, times(2)).findById(coachId);
    verify(scheduleRepository).save(any(SVCoachSchedule.class));
  }
}