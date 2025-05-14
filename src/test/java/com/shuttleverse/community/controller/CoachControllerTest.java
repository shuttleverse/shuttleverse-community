package com.shuttleverse.community.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.CoachService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CoachControllerTest {

    @Mock
    private CoachService coachService;

    @InjectMocks
    private CoachController coachController;

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
        schedule.setEndTime("17:00");
        schedule.setSubmittedBy(user);
        schedule.setUpvotes(0);
        schedule.setVerified(false);
    }

    @Test
    void createCoach_Success() {
        when(coachService.createCoach(any(Coach.class))).thenReturn(coach);

        ResponseEntity<ApiResponse<Coach>> response = coachController.createCoach(coach);

        assertTrue(response.getBody().isSuccess());
        assertEquals(coach, response.getBody().getData());
        verify(coachService).createCoach(any(Coach.class));
    }

    @Test
    void getCoach_Success() {
        when(coachService.getCoach(any(UUID.class))).thenReturn(coach);

        ResponseEntity<ApiResponse<Coach>> response = coachController.getCoach(coachId);

        assertTrue(response.getBody().isSuccess());
        assertEquals(coach, response.getBody().getData());
        verify(coachService).getCoach(coachId);
    }

    @Test
    void updateCoach_Success() {
        when(coachService.updateCoach(any(UUID.class), any(Coach.class))).thenReturn(coach);

        ResponseEntity<ApiResponse<Coach>> response = coachController.updateCoach(coachId, coach);

        assertTrue(response.getBody().isSuccess());
        assertEquals(coach, response.getBody().getData());
        verify(coachService).updateCoach(coachId, coach);
    }

    @Test
    void deleteCoach_Success() {
        doNothing().when(coachService).deleteCoach(any(UUID.class));

        ResponseEntity<ApiResponse<Void>> response = coachController.deleteCoach(coachId);

        assertTrue(response.getBody().isSuccess());
        verify(coachService).deleteCoach(coachId);
    }

    @Test
    void addSchedule_Success() {
        when(coachService.addSchedule(any(UUID.class), any(CoachSchedule.class))).thenReturn(schedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.addSchedule(coachId, schedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(coachService).addSchedule(coachId, schedule);
    }

    @Test
    void updateSchedule_Success() {
        when(coachService.updateSchedule(any(UUID.class), any(UUID.class), any(CoachSchedule.class))).thenReturn(
                schedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.updateSchedule(coachId, scheduleId,
                schedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(coachService).updateSchedule(coachId, scheduleId, schedule);
    }

    @Test
    void upvoteSchedule_Success() {
        when(coachService.upvoteSchedule(any(UUID.class))).thenReturn(schedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.upvoteSchedule(coachId, scheduleId);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(coachService).upvoteSchedule(scheduleId);
    }

    @Test
    void addCoachWithSchedule_Success() {
        // Create coach schedule
        CoachSchedule schedule = new CoachSchedule();
        schedule.setId(UUID.randomUUID());
        schedule.setDayOfWeek(1); // Monday
        schedule.setStartTime("09:00");
        schedule.setEndTime("22:00");
        schedule.setUpvotes(0);
        schedule.setVerified(false);
        schedule.setSubmittedBy(user);
        schedule.setCoach(coach);

        when(coachService.createCoach(any(Coach.class))).thenReturn(coach);
        when(coachService.addSchedule(any(UUID.class), any(CoachSchedule.class))).thenReturn(schedule);

        // Create coach
        ResponseEntity<ApiResponse<Coach>> coachResponse = coachController.createCoach(coach);
        assertTrue(coachResponse.getBody().isSuccess());
        assertEquals(coach, coachResponse.getBody().getData());

        // Add schedule
        ResponseEntity<ApiResponse<CoachSchedule>> scheduleResponse = coachController.addSchedule(coachId, schedule);
        assertTrue(scheduleResponse.getBody().isSuccess());
        assertEquals(schedule, scheduleResponse.getBody().getData());

        verify(coachService).createCoach(any(Coach.class));
        verify(coachService).addSchedule(any(UUID.class), any(CoachSchedule.class));
    }

    @Test
    void upvoteCoachSchedule_Success() {
        CoachSchedule schedule = new CoachSchedule();
        schedule.setId(scheduleId);
        schedule.setDayOfWeek(1);
        schedule.setStartTime("09:00");
        schedule.setEndTime("22:00");
        schedule.setUpvotes(1); // After upvote
        schedule.setVerified(false);
        schedule.setSubmittedBy(user);
        schedule.setCoach(coach);

        when(coachService.upvoteSchedule(any(UUID.class))).thenReturn(schedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.upvoteSchedule(coachId, scheduleId);

        assertTrue(response.getBody().isSuccess());
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

        when(coachService.addSchedule(any(UUID.class), any(CoachSchedule.class))).thenReturn(newSchedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.addSchedule(coachId, newSchedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(newSchedule, response.getBody().getData());
        verify(coachService).addSchedule(coachId, newSchedule);
    }
}