package com.shuttleverse.community.service;

import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.model.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachServiceTest {

    @Mock
    private CoachRepository coachRepository;

    @Mock
    private CoachScheduleRepository scheduleRepository;

    @InjectMocks
    private CoachService coachService;

    private Coach coach;
    private CoachSchedule schedule;
    private User user;
    private UUID coachId;
    private UUID userId;
    private UUID scheduleId;

    @BeforeEach
    void setUp() {
        coachId = UUID.randomUUID();
        userId = UUID.randomUUID();
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
        List<CoachSchedule> schedules = List.of(schedule);
        when(coachRepository.findById(any(UUID.class))).thenReturn(Optional.of(coach));
        when(scheduleRepository.saveAll(anyList())).thenReturn(schedules);

        List<CoachSchedule> results = coachService.addSchedule(user, coachId, schedules);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(schedule.getId(), results.get(0).getId());
        assertEquals(schedule.getDayOfWeek(), results.get(0).getDayOfWeek());
        assertEquals(schedule.getStartTime(), results.get(0).getStartTime());
        assertEquals(schedule.getEndTime(), results.get(0).getEndTime());
        verify(coachRepository).findById(coachId);
        verify(scheduleRepository).saveAll(anyList());
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

    @Test
    void isOwner_Success() {
        when(coachRepository.findById(any(UUID.class))).thenReturn(Optional.of(coach));

        boolean result = coachService.isOwner(coachId, userId);

        assertTrue(result);
        verify(coachRepository).findById(coachId);
    }

    @Test
    void isOwner_Failure() {
        when(coachRepository.findById(any(UUID.class))).thenReturn(Optional.of(coach));

        boolean result = coachService.isOwner(coachId, UUID.randomUUID());

        assertFalse(result);
        verify(coachRepository).findById(coachId);
    }
}