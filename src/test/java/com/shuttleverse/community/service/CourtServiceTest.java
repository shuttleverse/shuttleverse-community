package com.shuttleverse.community.service;

import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.repository.CourtRepository;
import com.shuttleverse.community.repository.CourtScheduleRepository;
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
class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private CourtScheduleRepository scheduleRepository;

    @InjectMocks
    private CourtService courtService;

    private Court court;
    private CourtSchedule schedule;
    private CourtPrice price;
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
        schedule.setCloseTime("22:00");
        schedule.setUpvotes(0);
        schedule.setVerified(false);
        schedule.setSubmittedBy(user);
        schedule.setCourt(court);

        price = new CourtPrice();
        price.setId(1L);
        price.setPrice(30.0);
        price.setDuration(60);
        price.setUpvotes(0);
        price.setIsVerified(false);
        price.setCourt(court);
    }

    @Test
    void createCourt_Success() {
        when(courtRepository.save(any(Court.class))).thenReturn(court);

        Court result = courtService.createCourt(court);

        assertNotNull(result);
        assertEquals(court.getId(), result.getId());
        assertEquals(court.getName(), result.getName());
        verify(courtRepository).save(any(Court.class));
    }

    @Test
    void getCourt_Success() {
        when(courtRepository.findById(anyLong())).thenReturn(Optional.of(court));

        Court result = courtService.getCourt(1L);

        assertNotNull(result);
        assertEquals(court.getId(), result.getId());
        assertEquals(court.getName(), result.getName());
        verify(courtRepository).findById(1L);
    }

    @Test
    void getCourt_NotFound() {
        when(courtRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courtService.getCourt(1L));
        verify(courtRepository).findById(1L);
    }

    @Test
    void addSchedule_Success() {
        when(courtRepository.findById(anyLong())).thenReturn(Optional.of(court));
        when(scheduleRepository.save(any(CourtSchedule.class))).thenReturn(schedule);

        CourtSchedule result = courtService.addSchedule(1L, schedule);

        assertNotNull(result);
        assertEquals(schedule.getId(), result.getId());
        assertEquals(schedule.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(schedule.getOpenTime(), result.getOpenTime());
        assertEquals(schedule.getCloseTime(), result.getCloseTime());
        verify(courtRepository).findById(1L);
        verify(scheduleRepository).save(any(CourtSchedule.class));
    }

    @Test
    void upvoteSchedule_Success() {
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any(CourtSchedule.class))).thenReturn(schedule);

        CourtSchedule result = courtService.upvoteSchedule(1L);

        assertNotNull(result);
        assertEquals(1, result.getUpvotes());
        verify(scheduleRepository).findById(1L);
        verify(scheduleRepository).save(any(CourtSchedule.class));
    }

    @Test
    void upvoteSchedule_NotFound() {
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courtService.upvoteSchedule(1L));
        verify(scheduleRepository).findById(1L);
    }

    @Test
    void updateSchedule_Success() {
        when(courtRepository.findById(anyLong())).thenReturn(Optional.of(court));
        when(scheduleRepository.save(any(CourtSchedule.class))).thenReturn(schedule);

        CourtSchedule result = courtService.updateSchedule(1L, 1L, schedule);

        assertNotNull(result);
        assertEquals(schedule.getId(), result.getId());
        assertEquals(schedule.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(schedule.getOpenTime(), result.getOpenTime());
        assertEquals(schedule.getCloseTime(), result.getCloseTime());
        verify(courtRepository, times(2)).findById(1L);
        verify(scheduleRepository).save(any(CourtSchedule.class));
    }

    @Test
    void isOwner_Success() {
        when(courtRepository.findById(anyLong())).thenReturn(Optional.of(court));

        boolean result = courtService.isOwner(1L, 1L);

        assertTrue(result);
        verify(courtRepository).findById(1L);
    }

    @Test
    void isOwner_Failure() {
        when(courtRepository.findById(anyLong())).thenReturn(Optional.of(court));

        boolean result = courtService.isOwner(1L, 2L);

        assertFalse(result);
        verify(courtRepository).findById(1L);
    }
}