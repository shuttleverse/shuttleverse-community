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
        when(coachService.getCoach(anyLong())).thenReturn(coach);

        ResponseEntity<ApiResponse<Coach>> response = coachController.getCoach(1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals(coach, response.getBody().getData());
        verify(coachService).getCoach(1L);
    }

    @Test
    void updateCoach_Success() {
        when(coachService.updateCoach(anyLong(), any(Coach.class))).thenReturn(coach);

        ResponseEntity<ApiResponse<Coach>> response = coachController.updateCoach(1L, coach);

        assertTrue(response.getBody().isSuccess());
        assertEquals(coach, response.getBody().getData());
        verify(coachService).updateCoach(1L, coach);
    }

    @Test
    void deleteCoach_Success() {
        doNothing().when(coachService).deleteCoach(anyLong());

        ResponseEntity<ApiResponse<Void>> response = coachController.deleteCoach(1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals("Coach deleted successfully", response.getBody().getMessage());
        verify(coachService).deleteCoach(1L);
    }

    @Test
    void addSchedule_Success() {
        when(coachService.addSchedule(anyLong(), any(CoachSchedule.class))).thenReturn(schedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.addSchedule(1L, schedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(coachService).addSchedule(1L, schedule);
    }

    @Test
    void updateSchedule_Success() {
        when(coachService.updateSchedule(anyLong(), anyLong(), any(CoachSchedule.class))).thenReturn(
                schedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.updateSchedule(1L, 1L, schedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(coachService).updateSchedule(1L, 1L, schedule);
    }

    @Test
    void upvoteSchedule_Success() {
        when(coachService.upvoteSchedule(anyLong())).thenReturn(schedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.upvoteSchedule(1L, 1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(coachService).upvoteSchedule(1L);
    }

    @Test
    void addCoachWithSchedule_Success() {
        // Create coach schedule
        CoachSchedule schedule = new CoachSchedule();
        schedule.setId(1L);
        schedule.setDayOfWeek(1); // Monday
        schedule.setStartTime("09:00");
        schedule.setEndTime("17:00");
        schedule.setUpvotes(0);
        schedule.setVerified(false);
        schedule.setSubmittedBy(user);
        schedule.setCoach(coach);

        when(coachService.createCoach(any(Coach.class))).thenReturn(coach);
        when(coachService.addSchedule(anyLong(), any(CoachSchedule.class))).thenReturn(schedule);

        // Create coach
        ResponseEntity<ApiResponse<Coach>> coachResponse = coachController.createCoach(coach);
        assertTrue(coachResponse.getBody().isSuccess());
        assertEquals(coach, coachResponse.getBody().getData());

        // Add schedule
        ResponseEntity<ApiResponse<CoachSchedule>> scheduleResponse = coachController.addSchedule(1L, schedule);
        assertTrue(scheduleResponse.getBody().isSuccess());
        assertEquals(schedule, scheduleResponse.getBody().getData());

        verify(coachService).createCoach(any(Coach.class));
        verify(coachService).addSchedule(anyLong(), any(CoachSchedule.class));
    }

    @Test
    void upvoteCoachSchedule_Success() {
        CoachSchedule schedule = new CoachSchedule();
        schedule.setId(1L);
        schedule.setDayOfWeek(1);
        schedule.setStartTime("09:00");
        schedule.setEndTime("17:00");
        schedule.setUpvotes(1); // After upvote
        schedule.setVerified(false);
        schedule.setSubmittedBy(user);
        schedule.setCoach(coach);

        when(coachService.upvoteSchedule(anyLong())).thenReturn(schedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.upvoteSchedule(1L, 1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        assertEquals(1, response.getBody().getData().getUpvotes());
        verify(coachService).upvoteSchedule(1L);
    }

    @Test
    void addNewScheduleForExistingCoach_Success() {
        CoachSchedule newSchedule = new CoachSchedule();
        newSchedule.setDayOfWeek(2); // Tuesday
        newSchedule.setStartTime("10:00");
        newSchedule.setEndTime("18:00");
        newSchedule.setUpvotes(0);
        newSchedule.setVerified(false);
        newSchedule.setSubmittedBy(user);
        newSchedule.setCoach(coach);

        when(coachService.addSchedule(anyLong(), any(CoachSchedule.class))).thenReturn(newSchedule);

        ResponseEntity<ApiResponse<CoachSchedule>> response = coachController.addSchedule(1L, newSchedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(newSchedule, response.getBody().getData());
        verify(coachService).addSchedule(1L, newSchedule);
    }
}