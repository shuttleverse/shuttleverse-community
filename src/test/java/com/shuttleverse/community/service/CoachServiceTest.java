package com.shuttleverse.community.service;

import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.repository.CoachRepository;
import com.shuttleverse.community.repository.CoachScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        coach = new Coach();
        coach.setId(1L);
        coach.setName("Test Coach");
        coach.setOwner(user);

        schedule = new CoachSchedule();
        schedule.setId(1L);
        schedule.setDayOfWeek(1);
        schedule.setStartTime("09:00");
        schedule.setEndTime("17:00");
        schedule.setUpvotes(0);
        schedule.setVerified(false);
        schedule.setSubmittedBy(user);
        schedule.setCoach(coach);
    }

    @Test
    void createCoach_Success() {
        when(coachRepository.save(any(Coach.class))).thenReturn(coach);

        Coach result = coachService.createCoach(coach);

        assertNotNull(result);
        assertEquals(coach.getId(), result.getId());
        assertEquals(coach.getName(), result.getName());
        verify(coachRepository).save(any(Coach.class));
    }

    @Test
    void getCoach_Success() {
        when(coachRepository.findById(anyLong())).thenReturn(Optional.of(coach));

        Coach result = coachService.getCoach(1L);

        assertNotNull(result);
        assertEquals(coach.getId(), result.getId());
        assertEquals(coach.getName(), result.getName());
        verify(coachRepository).findById(1L);
    }

    @Test
    void getCoach_NotFound() {
        when(coachRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> coachService.getCoach(1L));
        verify(coachRepository).findById(1L);
    }

    @Test
    void addSchedule_Success() {
        when(coachRepository.findById(anyLong())).thenReturn(Optional.of(coach));
        when(scheduleRepository.save(any(CoachSchedule.class))).thenReturn(schedule);

        CoachSchedule result = coachService.addSchedule(1L, schedule);

        assertNotNull(result);
        assertEquals(schedule.getId(), result.getId());
        assertEquals(schedule.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(schedule.getStartTime(), result.getStartTime());
        assertEquals(schedule.getEndTime(), result.getEndTime());
        verify(coachRepository).findById(1L);
        verify(scheduleRepository).save(any(CoachSchedule.class));
    }

    @Test
    void upvoteSchedule_Success() {
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any(CoachSchedule.class))).thenReturn(schedule);

        CoachSchedule result = coachService.upvoteSchedule(1L);

        assertNotNull(result);
        assertEquals(1, result.getUpvotes());
        verify(scheduleRepository).findById(1L);
        verify(scheduleRepository).save(any(CoachSchedule.class));
    }

    @Test
    void upvoteSchedule_NotFound() {
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> coachService.upvoteSchedule(1L));
        verify(scheduleRepository).findById(1L);
    }

    @Test
    void updateSchedule_Success() {
        when(coachRepository.findById(anyLong())).thenReturn(Optional.of(coach));
        when(scheduleRepository.save(any(CoachSchedule.class))).thenReturn(schedule);

        CoachSchedule result = coachService.updateSchedule(1L, 1L, schedule);

        assertNotNull(result);
        assertEquals(schedule.getId(), result.getId());
        assertEquals(schedule.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(schedule.getStartTime(), result.getStartTime());
        assertEquals(schedule.getEndTime(), result.getEndTime());
        verify(coachRepository, times(2)).findById(1L);
        verify(scheduleRepository).save(any(CoachSchedule.class));
    }

    @Test
    void isOwner_Success() {
        when(coachRepository.findById(anyLong())).thenReturn(Optional.of(coach));

        boolean result = coachService.isOwner(1L, 1L);

        assertTrue(result);
        verify(coachRepository).findById(1L);
    }

    @Test
    void isOwner_Failure() {
        when(coachRepository.findById(anyLong())).thenReturn(Optional.of(coach));

        boolean result = coachService.isOwner(1L, 2L);

        assertFalse(result);
        verify(coachRepository).findById(1L);
    }
}