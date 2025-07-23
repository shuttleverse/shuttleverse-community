package com.shuttleverse.community.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shuttleverse.community.SVBaseTest;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
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
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CourtServiceTest extends SVBaseTest {

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
  private UUID courtId;
  private UUID scheduleId;
  private UUID priceId;

  @BeforeEach
  void setUp() {
    courtId = UUID.randomUUID();
    scheduleId = UUID.randomUUID();
    priceId = UUID.randomUUID();

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
    schedule.setCourtId(courtId);

    price = new CourtPrice();
    price.setId(priceId);
    price.setPrice(30.0);
    price.setDuration(60);
    price.setUpvotes(0);
    price.setIsVerified(false);
    price.setCourtId(courtId);
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
    UUID courtId = UUID.fromString("99e4fbf4-bb57-4484-bc7e-9999926bcf01");
    Court court = new Court();
    court.setId(courtId);
    court.setOwner(user);
    when(courtRepository.findById(courtId)).thenReturn(Optional.of(court));
    when(scheduleRepository.saveAll(any())).thenReturn(List.of(schedule));

    List<CourtSchedule> result = courtService.addSchedule(user, courtId, List.of(schedule));

    verify(courtRepository).findById(courtId);
    verify(scheduleRepository).saveAll(any());
    assertEquals(1, result.size());
    assertEquals(schedule, result.get(0));
  }

  @Test
  void addPrice_Success() {
    UUID courtId = UUID.fromString("82f6bd6a-4df1-455d-88b7-c645d1de8ce8");
    Court court = new Court();
    court.setId(courtId);
    court.setOwner(user);
    when(courtRepository.findById(courtId)).thenReturn(Optional.of(court));
    when(priceRepository.saveAll(any())).thenReturn(List.of(price));

    List<CourtPrice> result = courtService.addPrice(user, courtId, List.of(price));

    verify(courtRepository).findById(courtId);
    verify(priceRepository).saveAll(any());
    assertEquals(1, result.size());
    assertEquals(price, result.get(0));
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
}