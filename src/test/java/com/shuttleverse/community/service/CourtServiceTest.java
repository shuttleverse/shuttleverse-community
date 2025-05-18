package com.shuttleverse.community.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.repository.CourtPriceRepository;
import com.shuttleverse.community.repository.CourtRepository;
import com.shuttleverse.community.repository.CourtScheduleRepository;
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

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

  @Mock
  private CourtRepository courtRepository;

  @Mock
  private CourtScheduleRepository scheduleRepository;

  @Mock
  private CourtPriceRepository priceRepository;

  @InjectMocks
  private CourtService courtService;

  private Court court;
  private CourtSchedule schedule;
  private CourtPrice price;
  private User user;
  private UUID courtId;
  private UUID userId;
  private UUID scheduleId;
  private UUID priceId;

  @BeforeEach
  void setUp() {
    courtId = UUID.randomUUID();
    userId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();
    priceId = UUID.randomUUID();

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
    schedule.setCloseTime("22:00");
    schedule.setUpvotes(0);
    schedule.setVerified(false);
    schedule.setSubmittedBy(user);
    schedule.setCourt(court);

    price = new CourtPrice();
    price.setId(priceId);
    price.setPrice(30.0);
    price.setDuration(60);
    price.setUpvotes(0);
    price.setIsVerified(false);
    price.setCourt(court);
  }

  @Test
  void createCourt_Success() {
    when(courtRepository.save(any(Court.class))).thenReturn(court);

    Court result = courtService.createCourt(user, court);

    assertNotNull(result);
    assertEquals(court.getId(), result.getId());
    assertEquals(court.getName(), result.getName());
    assertEquals(user, result.getCreator());
    verify(courtRepository).save(any(Court.class));
  }

  @Test
  void getCourt_Success() {
    when(courtRepository.findById(any(UUID.class))).thenReturn(Optional.of(court));

    Court result = courtService.getCourt(courtId);

    assertNotNull(result);
    assertEquals(court.getId(), result.getId());
    assertEquals(court.getName(), result.getName());
    verify(courtRepository).findById(courtId);
  }

  @Test
  void getCourt_NotFound() {
    when(courtRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> courtService.getCourt(courtId));
    verify(courtRepository).findById(courtId);
  }

  @Test
  void addSchedule_Success() {
    when(courtRepository.findById(any(UUID.class))).thenReturn(Optional.of(court));
    when(scheduleRepository.saveAll(anyList())).thenReturn(List.of(schedule));

    List<CourtSchedule> schedules = List.of(schedule);
    List<CourtSchedule> results = courtService.addSchedule(courtId, schedules);

    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals(schedule.getId(), results.get(0).getId());
    assertEquals(schedule.getDayOfWeek(), results.get(0).getDayOfWeek());
    assertEquals(schedule.getOpenTime(), results.get(0).getOpenTime());
    assertEquals(schedule.getCloseTime(), results.get(0).getCloseTime());
    verify(courtRepository).findById(courtId);
    verify(scheduleRepository).saveAll(anyList());
  }

  @Test
  void addPrice_Success() {
    when(courtRepository.findById(any(UUID.class))).thenReturn(Optional.of(court));
    when(priceRepository.saveAll(anyList())).thenReturn(List.of(price));

    List<CourtPrice> prices = List.of(price);
    List<CourtPrice> results = courtService.addPrice(courtId, prices);

    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals(price.getId(), results.get(0).getId());
    assertEquals(price.getPrice(), results.get(0).getPrice());
    assertEquals(price.getDuration(), results.get(0).getDuration());
    verify(courtRepository).findById(courtId);
    verify(priceRepository).saveAll(anyList());
  }

  @Test
  void upvoteSchedule_Success() {
    when(scheduleRepository.findById(any(UUID.class))).thenReturn(Optional.of(schedule));
    when(scheduleRepository.save(any(CourtSchedule.class))).thenReturn(schedule);

    CourtSchedule result = courtService.upvoteSchedule(scheduleId);

    assertNotNull(result);
    assertEquals(1, result.getUpvotes());
    verify(scheduleRepository).findById(scheduleId);
    verify(scheduleRepository).save(any(CourtSchedule.class));
  }

  @Test
  void upvoteSchedule_NotFound() {
    when(scheduleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> courtService.upvoteSchedule(scheduleId));
    verify(scheduleRepository).findById(scheduleId);
  }

  @Test
  void updateSchedule_Success() {
    when(courtRepository.findById(any(UUID.class))).thenReturn(Optional.of(court));
    when(scheduleRepository.save(any(CourtSchedule.class))).thenReturn(schedule);

    CourtSchedule result = courtService.updateSchedule(courtId, scheduleId, schedule);

    assertNotNull(result);
    assertEquals(schedule.getId(), result.getId());
    assertEquals(schedule.getDayOfWeek(), result.getDayOfWeek());
    assertEquals(schedule.getOpenTime(), result.getOpenTime());
    assertEquals(schedule.getCloseTime(), result.getCloseTime());
    verify(courtRepository, times(2)).findById(courtId);
    verify(scheduleRepository).save(any(CourtSchedule.class));
  }

  @Test
  void isOwner_Success() {
    when(courtRepository.findById(any(UUID.class))).thenReturn(Optional.of(court));

    boolean result = courtService.isOwner(courtId, userId);

    assertTrue(result);
    verify(courtRepository).findById(courtId);
  }

  @Test
  void isOwner_Failure() {
    when(courtRepository.findById(any(UUID.class))).thenReturn(Optional.of(court));

    boolean result = courtService.isOwner(courtId, UUID.randomUUID());

    assertFalse(result);
    verify(courtRepository).findById(courtId);
  }
}