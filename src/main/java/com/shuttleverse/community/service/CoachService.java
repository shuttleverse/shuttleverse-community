package com.shuttleverse.community.service;

import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.repository.CoachRepository;
import com.shuttleverse.community.repository.CoachScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoachService {

  private final CoachRepository coachRepository;
  private final CoachScheduleRepository scheduleRepository;

  @Transactional
  public Coach createCoach(Coach coach) {
    return coachRepository.save(coach);
  }

  public Coach getCoach(Long id) {
    return coachRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Coach not found with id: " + id));
  }

  @Transactional
  public Coach updateCoach(Long id, Coach coach) {
    if (!isOwner(id, coach.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update coach information");
    }
    coach.setId(id);
    return coachRepository.save(coach);
  }

  @Transactional
  public void deleteCoach(Long id) {
    Coach coach = getCoach(id);
    if (!isOwner(id, coach.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can delete the coach");
    }
    coachRepository.delete(coach);
  }

  @Transactional
  public CoachSchedule addSchedule(Long coachId, CoachSchedule schedule) {
    Coach coach = getCoach(coachId);
    schedule.setCoach(coach);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public CoachSchedule updateSchedule(Long coachId, Long scheduleId, CoachSchedule schedule) {
    Coach coach = getCoach(coachId);
    if (!isOwner(coachId, coach.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update schedule");
    }
    schedule.setId(scheduleId);
    schedule.setCoach(coach);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public CoachSchedule upvoteSchedule(Long scheduleId) {
    CoachSchedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
    schedule.setUpvotes(schedule.getUpvotes() + 1);
    return scheduleRepository.save(schedule);
  }

  public boolean isOwner(Long coachId, Long userId) {
    Coach coach = getCoach(coachId);
    return coach.getOwner() != null && coach.getOwner().getId().equals(userId);
  }
}