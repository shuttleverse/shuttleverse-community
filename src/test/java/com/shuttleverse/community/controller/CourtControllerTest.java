package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.model.OwnershipClaim;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.CourtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CourtControllerTest {

    @Mock
    private CourtService courtService;

    @InjectMocks
    private CourtController courtController;

    private Court court;
    private CourtSchedule schedule;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        court = new Court();
        court.setId(1L);
        court.setName("Test Court");
        court.setOwner(user);

        schedule = new CourtSchedule();
        schedule.setId(1L);
        schedule.setDayOfWeek(1);
        schedule.setOpenTime("09:00");
        schedule.setCloseTime("17:00");
        schedule.setSubmittedBy(user);
    }

    @Test
    void createCourt_Success() {
        when(courtService.createCourt(any(Court.class))).thenReturn(court);

        ResponseEntity<ApiResponse<Court>> response = courtController.createCourt(court);

        assertTrue(response.getBody().isSuccess());
        assertEquals(court, response.getBody().getData());
        verify(courtService).createCourt(any(Court.class));
    }

    @Test
    void getCourt_Success() {
        when(courtService.getCourt(anyLong())).thenReturn(court);

        ResponseEntity<ApiResponse<Court>> response = courtController.getCourt(1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals(court, response.getBody().getData());
        verify(courtService).getCourt(1L);
    }

    @Test
    void updateCourt_Success() {
        when(courtService.updateCourt(anyLong(), any(Court.class))).thenReturn(court);

        ResponseEntity<ApiResponse<Court>> response = courtController.updateCourt(1L, court);

        assertTrue(response.getBody().isSuccess());
        assertEquals(court, response.getBody().getData());
        verify(courtService).updateCourt(1L, court);
    }

    @Test
    void deleteCourt_Success() {
        doNothing().when(courtService).deleteCourt(anyLong());

        ResponseEntity<ApiResponse<Void>> response = courtController.deleteCourt(1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals("Court deleted successfully", response.getBody().getMessage());
        verify(courtService).deleteCourt(1L);
    }

    @Test
    void addSchedule_Success() {
        when(courtService.addSchedule(anyLong(), any(CourtSchedule.class))).thenReturn(schedule);

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.addSchedule(1L, schedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(courtService).addSchedule(1L, schedule);
    }

    @Test
    void updateSchedule_Success() {
        when(courtService.updateSchedule(anyLong(), anyLong(), any(CourtSchedule.class))).thenReturn(schedule);

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.updateSchedule(1L, 1L, schedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(courtService).updateSchedule(1L, 1L, schedule);
    }

    @Test
    void upvoteSchedule_Success() {
        when(courtService.upvoteSchedule(anyLong())).thenReturn(schedule);

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.upvoteSchedule(1L, 1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(courtService).upvoteSchedule(1L);
    }

    @Test
    void addCourtWithPriceAndSchedule_Success() {
        // Create court price
        CourtPrice price = new CourtPrice();
        price.setId(1L);
        price.setPrice(30.0);
        price.setDuration(60); // 1 hour
        price.setUpvotes(0);
        price.setIsVerified(false);
        price.setCourt(court);

        // Create court schedule
        CourtSchedule schedule = new CourtSchedule();
        schedule.setId(1L);
        schedule.setDayOfWeek(1); // Monday
        schedule.setOpenTime("09:00");
        schedule.setCloseTime("22:00");
        schedule.setUpvotes(0);
        schedule.setVerified(false);
        schedule.setSubmittedBy(user);
        schedule.setCourt(court);

        when(courtService.createCourt(any(Court.class))).thenReturn(court);
        when(courtService.addSchedule(anyLong(), any(CourtSchedule.class))).thenReturn(schedule);

        // Create court
        ResponseEntity<ApiResponse<Court>> courtResponse = courtController.createCourt(court);
        assertTrue(courtResponse.getBody().isSuccess());
        assertEquals(court, courtResponse.getBody().getData());

        // Add schedule
        ResponseEntity<ApiResponse<CourtSchedule>> scheduleResponse = courtController.addSchedule(1L, schedule);
        assertTrue(scheduleResponse.getBody().isSuccess());
        assertEquals(schedule, scheduleResponse.getBody().getData());

        verify(courtService).createCourt(any(Court.class));
        verify(courtService).addSchedule(anyLong(), any(CourtSchedule.class));
    }

    @Test
    void upvoteCourtSchedule_Success() {
        CourtSchedule schedule = new CourtSchedule();
        schedule.setId(1L);
        schedule.setDayOfWeek(1);
        schedule.setOpenTime("09:00");
        schedule.setCloseTime("22:00");
        schedule.setUpvotes(1); // After upvote
        schedule.setVerified(false);
        schedule.setSubmittedBy(user);
        schedule.setCourt(court);

        when(courtService.upvoteSchedule(anyLong())).thenReturn(schedule);

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.upvoteSchedule(1L, 1L);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        assertEquals(1, response.getBody().getData().getUpvotes());
        verify(courtService).upvoteSchedule(1L);
    }

    @Test
    void addNewScheduleForExistingCourt_Success() {
        CourtSchedule newSchedule = new CourtSchedule();
        newSchedule.setDayOfWeek(2); // Tuesday
        newSchedule.setOpenTime("10:00");
        newSchedule.setCloseTime("21:00");
        newSchedule.setUpvotes(0);
        newSchedule.setVerified(false);
        newSchedule.setSubmittedBy(user);
        newSchedule.setCourt(court);

        when(courtService.addSchedule(anyLong(), any(CourtSchedule.class))).thenReturn(newSchedule);

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.addSchedule(1L, newSchedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(newSchedule, response.getBody().getData());
        verify(courtService).addSchedule(1L, newSchedule);
    }
}