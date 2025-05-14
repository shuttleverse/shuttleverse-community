package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.model.OwnershipClaim;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.CourtService;
import java.util.UUID;
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
import static org.mockito.ArgumentMatchers.any;
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
    private UUID courtId;
    private UUID userId;
    private UUID scheduleId;

    @BeforeEach
    void setUp() {
        courtId = UUID.randomUUID();
        userId = UUID.randomUUID();
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
        when(courtService.getCourt(any(UUID.class))).thenReturn(court);

        ResponseEntity<ApiResponse<Court>> response = courtController.getCourt(courtId);

        assertTrue(response.getBody().isSuccess());
        assertEquals(court, response.getBody().getData());
        verify(courtService).getCourt(courtId);
    }

    @Test
    void updateCourt_Success() {
        when(courtService.updateCourt(any(UUID.class), any(Court.class))).thenReturn(court);

        ResponseEntity<ApiResponse<Court>> response = courtController.updateCourt(courtId, court);

        assertTrue(response.getBody().isSuccess());
        assertEquals(court, response.getBody().getData());
        verify(courtService).updateCourt(courtId, court);
    }

    @Test
    void deleteCourt_Success() {
        doNothing().when(courtService).deleteCourt(any(UUID.class));

        ResponseEntity<ApiResponse<Void>> response = courtController.deleteCourt(courtId);

        assertTrue(response.getBody().isSuccess());
        verify(courtService).deleteCourt(courtId);
    }

    @Test
    void addSchedule_Success() {
        when(courtService.addSchedule(any(UUID.class), any(CourtSchedule.class))).thenReturn(schedule);

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.addSchedule(courtId, schedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(courtService).addSchedule(courtId, schedule);
    }

    @Test
    void updateSchedule_Success() {
        when(courtService.updateSchedule(any(UUID.class), any(UUID.class), any(CourtSchedule.class)))
                .thenReturn(schedule);

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.updateSchedule(courtId, scheduleId,
                schedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(courtService).updateSchedule(courtId, scheduleId, schedule);
    }

    @Test
    void upvoteSchedule_Success() {
        when(courtService.upvoteSchedule(any(UUID.class))).thenReturn(schedule);

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.upvoteSchedule(courtId, scheduleId);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        verify(courtService).upvoteSchedule(scheduleId);
    }

    @Test
    void addCourtWithPriceAndSchedule_Success() {
        // Create court price
        CourtPrice price = new CourtPrice();
        price.setId(UUID.randomUUID());
        price.setPrice(30.0);
        price.setDuration(60); // 1 hour
        price.setUpvotes(0);
        price.setIsVerified(false);
        price.setCourt(court);

        // Create court schedule
        CourtSchedule schedule = new CourtSchedule();
        schedule.setId(UUID.randomUUID());
        schedule.setDayOfWeek(1); // Monday
        schedule.setOpenTime("09:00");
        schedule.setCloseTime("22:00");
        schedule.setUpvotes(0);
        schedule.setVerified(false);
        schedule.setSubmittedBy(user);
        schedule.setCourt(court);

        when(courtService.createCourt(any(Court.class))).thenReturn(court);
        when(courtService.addSchedule(any(UUID.class), any(CourtSchedule.class))).thenReturn(schedule);

        // Create court
        ResponseEntity<ApiResponse<Court>> courtResponse = courtController.createCourt(court);
        assertTrue(courtResponse.getBody().isSuccess());
        assertEquals(court, courtResponse.getBody().getData());

        // Add schedule
        ResponseEntity<ApiResponse<CourtSchedule>> scheduleResponse = courtController.addSchedule(courtId, schedule);
        assertTrue(scheduleResponse.getBody().isSuccess());
        assertEquals(schedule, scheduleResponse.getBody().getData());

        verify(courtService).createCourt(any(Court.class));
        verify(courtService).addSchedule(any(UUID.class), any(CourtSchedule.class));
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

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.upvoteSchedule(courtId, scheduleId);

        assertTrue(response.getBody().isSuccess());
        assertEquals(schedule, response.getBody().getData());
        assertEquals(1, response.getBody().getData().getUpvotes());
        verify(courtService).upvoteSchedule(scheduleId);
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

        when(courtService.addSchedule(any(UUID.class), any(CourtSchedule.class))).thenReturn(newSchedule);

        ResponseEntity<ApiResponse<CourtSchedule>> response = courtController.addSchedule(courtId, newSchedule);

        assertTrue(response.getBody().isSuccess());
        assertEquals(newSchedule, response.getBody().getData());
        verify(courtService).addSchedule(courtId, newSchedule);
    }
}