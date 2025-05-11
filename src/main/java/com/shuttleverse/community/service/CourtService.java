package com.shuttleverse.community.service;

import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.repository.CourtRepository;
import com.shuttleverse.community.repository.CourtScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourtService {

  private final CourtRepository courtRepository;
  private final CourtScheduleRepository scheduleRepository;

  @Transactional
  public Court createCourt(Court court) {
    return courtRepository.save(court);
  }

  public Court getCourt(Long id) {
    return courtRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Court not found with id: " + id));
  }

  @Transactional
  public Court updateCourt(Long id, Court court) {
    if (!isOwner(id, court.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update court information");
    }
    court.setId(id);
    return courtRepository.save(court);
  }

  @Transactional
  public void deleteCourt(Long id) {
    Court court = getCourt(id);
    if (!isOwner(id, court.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can delete the court");
    }
    courtRepository.delete(court);
  }

  @Transactional
  public CourtSchedule addSchedule(Long courtId, CourtSchedule schedule) {
    Court court = getCourt(courtId);
    schedule.setCourt(court);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public CourtSchedule updateSchedule(Long courtId, Long scheduleId, CourtSchedule schedule) {
    Court court = getCourt(courtId);
    if (!isOwner(courtId, court.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update schedule");
    }
    schedule.setId(scheduleId);
    schedule.setCourt(court);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public CourtSchedule upvoteSchedule(Long scheduleId) {
    CourtSchedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
    schedule.setUpvotes(schedule.getUpvotes() + 1);
    return scheduleRepository.save(schedule);
  }


  public boolean isOwner(Long courtId, Long userId) {
    Court court = getCourt(courtId);
    return court.getOwner() != null && court.getOwner().getId().equals(userId);
  }
}