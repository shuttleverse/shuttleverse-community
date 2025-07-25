package com.shuttleverse.community.service;

import com.shuttleverse.community.constants.BadmintonEntityType;
import com.shuttleverse.community.constants.BadmintonInfoType;
import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachPrice;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.params.BoundingBoxParams;
import com.shuttleverse.community.params.WithinDistanceParams;
import com.shuttleverse.community.repository.CoachPriceRepository;
import com.shuttleverse.community.repository.CoachRepository;
import com.shuttleverse.community.repository.CoachScheduleRepository;
import com.shuttleverse.community.util.AuthenticationUtils;
import com.shuttleverse.community.util.SpecificationBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoachService {

  private final CoachRepository coachRepository;
  private final CoachScheduleRepository scheduleRepository;
  private final CoachPriceRepository priceRepository;
  private final CoachPriceRepository coachPriceRepository;
  private final UpvoteService upvoteService;

  @Transactional
  public Coach createCoach(Coach coach, User creator) {
    coach.setCreator(creator);
    return coachRepository.save(coach);
  }

  public Coach getCoach(UUID id) {
    return coachRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Coach not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public Page<Coach> getAllCoaches(Map<String, String> filters, Pageable pageable) {
    Specification<Coach> spec = SpecificationBuilder.buildSpecification(filters);
    return coachRepository.findAll(spec, pageable);
  }

  @Transactional(readOnly = true)
  public Page<Coach> getCourtsByBoundingBox(BoundingBoxParams params, Pageable pageable) {
    return coachRepository.findWithinBounds(params.getMinLon(), params.getMinLat(),
        params.getMaxLon(), params.getMaxLat(), pageable);
  }

  @Transactional(readOnly = true)
  public Page<Coach> getCoachesWithinDistance(WithinDistanceParams params, Pageable pageable) {
    return coachRepository.findWithinDistance(params.getLocation(), params.getDistance(), pageable);
  }

  @Transactional
  public Coach updateCoach(UUID id, Coach coach) {
    if (!isOwner(id, coach.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update coach information");
    }
    coach.setId(id);
    return coachRepository.save(coach);
  }

  @Transactional
  public void deleteCoach(UUID id) {
    Coach coach = getCoach(id);
    if (!isOwner(id, coach.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can delete the coach");
    }
    coachRepository.delete(coach);
  }

  @Transactional
  public List<CoachSchedule> addSchedule(User creator, UUID coachId,
      List<CoachSchedule> schedules) {
    getCoach(coachId);

    for (CoachSchedule schedule : schedules) {
      schedule.setSubmittedBy(creator);
      schedule.setCoachId(coachId);
    }
    return scheduleRepository.saveAll(schedules);
  }

  @Transactional
  public CoachSchedule updateSchedule(UUID coachId, UUID scheduleId, CoachSchedule schedule) {
    Coach coach = getCoach(coachId);
    if (!isOwner(coachId, coach.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update schedule");
    }
    schedule.setId(scheduleId);
    schedule.setCoachId(coachId);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public CoachSchedule upvoteSchedule(UUID scheduleId) {
    CoachSchedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
    schedule.setUpvotes(schedule.getUpvotes() + 1);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public CoachSchedule upvoteSchedule(UUID scheduleId, User creator) {
    CoachSchedule schedule = this.upvoteSchedule(scheduleId);

    upvoteService.addUpvote(BadmintonEntityType.COACH, BadmintonInfoType.SCHEDULE, scheduleId,
        creator);

    return schedule;
  }

  @Transactional
  public CoachPrice updatePrice(UUID coachId, UUID priceId, CoachPrice price) {
    Coach coach = getCoach(coachId);
    if (!isOwner(coachId, coach.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update price");
    }
    price.setId(priceId);
    price.setCoachId(coachId);
    return priceRepository.save(price);
  }

  @Transactional
  public CoachPrice upvotePrice(UUID priceId) {
    CoachPrice coachPrice = coachPriceRepository.findById(priceId)
        .orElseThrow(() -> new EntityNotFoundException("Price not found"));
    coachPrice.setUpvotes(coachPrice.getUpvotes() + 1);
    return coachPriceRepository.save(coachPrice);
  }

  @Transactional
  public CoachPrice upvotePrice(UUID priceId, User creator) {
    CoachPrice coachPrice = this.upvotePrice(priceId);
    upvoteService.addUpvote(BadmintonEntityType.COACH, BadmintonInfoType.PRICE, priceId, creator);

    return coachPrice;
  }

  @Transactional
  public List<CoachPrice> addPrice(User creator, UUID coachId, List<CoachPrice> prices) {
    for (CoachPrice price : prices) {
      price.setSubmittedBy(creator);
      price.setCoachId(coachId);
    }
    return priceRepository.saveAll(prices);
  }

  public boolean isSessionUserOwner(String coachId) {
    UUID coachUuid = UUID.fromString(coachId);
    Coach coach = getCoach(coachUuid);
    UUID userId = AuthenticationUtils.getCurrentUser().getId();
    return coach.getOwner().getId().equals(userId);
  }

  private boolean isOwner(UUID coachId, UUID userId) {
    Coach coach = getCoach(coachId);
    return coach.getOwner().getId().equals(userId);
  }
}