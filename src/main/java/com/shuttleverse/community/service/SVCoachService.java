package com.shuttleverse.community.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.constants.SVSortType;
import com.shuttleverse.community.dto.SVLocationDto;
import com.shuttleverse.community.mapper.SVMapStructMapper;
import com.shuttleverse.community.model.SVCoach;
import com.shuttleverse.community.model.SVCoachPrice;
import com.shuttleverse.community.model.SVCoachSchedule;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.params.SVBoundingBoxParams;
import com.shuttleverse.community.params.SVCoachCreationData;
import com.shuttleverse.community.params.SVEntityFilterParams;
import com.shuttleverse.community.params.SVSortParams;
import com.shuttleverse.community.params.SVWithinDistanceParams;
import com.shuttleverse.community.query.SVQueryFactory;
import com.shuttleverse.community.query.SVQueryModel;
import com.shuttleverse.community.repository.SVCoachPriceRepository;
import com.shuttleverse.community.repository.SVCoachRepository;
import com.shuttleverse.community.repository.SVCoachScheduleRepository;
import com.shuttleverse.community.util.SVAuthenticationUtils;
import com.shuttleverse.community.util.SVQueryUtils;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SVCoachService {

  private final SVMapStructMapper mapper;
  private final SVCoachRepository coachRepository;
  private final SVCoachScheduleRepository scheduleRepository;
  private final SVCoachPriceRepository priceRepository;
  private final SVCoachPriceRepository coachPriceRepository;
  private final SVUpvoteService upvoteService;
  private final SVQueryFactory<SVCoach> queryFactory;

  @Transactional
  public SVCoach createCoach(SVCoach coach, SVUser creator) {
    coach.setCreator(creator);
    return coachRepository.save(coach);
  }

  public SVCoach getCoach(UUID id) {
    return coachRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Coach not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public Page<SVCoach> getAllCoaches(SVEntityFilterParams params, SVSortParams sortParams,
      Pageable pageable) {
    List<UUID> ids = findCoachesByPriceAndSchedule(params);

    BooleanExpression predicate = SVQueryModel.coach.id.in(ids);

    if (params.getIsVerified() != null) {
      predicate = predicate.and(SVQueryModel.coach.owner.isNotNull());
    }

    if (sortParams.getSortType() != SVSortType.LOCATION) {
      Pageable sortedPageable = PageRequest.of(
          pageable.getPageNumber(),
          pageable.getPageSize(),
          sortParams.getSortDirection().toPagableDirection(),
          sortParams.getSortType().toPageableProperty()
      );
      return coachRepository.findAll(predicate, sortedPageable);
    }

    JPAQuery<SVCoach> query = queryFactory.getQuery(SVQueryModel.coach, predicate)
        .orderBy(SVQueryUtils.orderByDistance(SVQueryModel.coach.locationPoint,
            mapper.locationDtoToPoint(new SVLocationDto(sortParams.getLongitude(),
                sortParams.getLatitude())), sortParams.getSortDirection()));

    Long totalCount = queryFactory.getQueryCount(SVQueryModel.coach, predicate);

    return PageableExecutionUtils.getPage(
        query.offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch(),
        pageable,
        () -> totalCount
    );
  }

  private List<UUID> findCoachesByPriceAndSchedule(SVEntityFilterParams params) {
    Set<UUID> priceResults = new HashSet<>();
    Set<UUID> scheduleResults = new HashSet<>();
    BooleanExpression pricePredicate = SVQueryModel.coachPrice.minPrice.goe(params.getMinPrice())
        .and(SVQueryModel.coachPrice.maxPrice.loe(params.getMaxPrice()));
    priceRepository.findAll(pricePredicate).forEach(price -> {
      priceResults.add(price.getCoachId());
    });
    if (params.getDaysOfWeek() != null && !params.getDaysOfWeek().isEmpty()) {
      BooleanExpression schedulePredicate = SVQueryModel.coachSchedule.dayOfWeek.in(
          params.getDaysOfWeek());
      scheduleRepository.findAll(schedulePredicate).forEach(schedule -> {
        scheduleResults.add(schedule.getCoachId());
      });
    }

    Set<UUID> result = new HashSet<>(priceResults);
    result.retainAll(scheduleResults);

    return !scheduleResults.isEmpty() ? new ArrayList<>(result) : new ArrayList<>(priceResults);
  }

  @Transactional(readOnly = true)
  public Page<SVCoach> getCoachesByBoundingBox(SVBoundingBoxParams params, Pageable pageable) {
    return coachRepository.findWithinBounds(params.getMinLon(), params.getMinLat(),
        params.getMaxLon(), params.getMaxLat(), pageable);
  }

  @Transactional(readOnly = true)
  public Page<SVCoach> getCoachesWithinDistance(SVWithinDistanceParams params, Pageable pageable) {
    return coachRepository.findWithinDistance(params.getLocation(), params.getDistance(), pageable);
  }

  @Transactional
  public SVCoach updateCoach(UUID id, SVCoachCreationData data) {
    SVCoach coach = coachRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

    mapper.updateCoachFromDto(data, coach);

    return coachRepository.save(coach);
  }

  @Transactional
  public void deleteCoach(UUID id) {
    SVCoach coach = getCoach(id);
    coachRepository.delete(coach);
  }

  @Transactional
  public List<SVCoachSchedule> addSchedule(SVUser creator, UUID coachId,
      List<SVCoachSchedule> schedules) {
    getCoach(coachId);

    for (SVCoachSchedule schedule : schedules) {
      schedule.setSubmittedBy(creator);
      schedule.setCoachId(coachId);
    }
    return scheduleRepository.saveAll(schedules);
  }

  @Transactional
  public SVCoachSchedule updateSchedule(UUID coachId, UUID scheduleId, SVCoachSchedule schedule) {
    SVCoach coach = getCoach(coachId);
    if (!isOwner(coachId, coach.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update schedule");
    }
    schedule.setId(scheduleId);
    schedule.setCoachId(coachId);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public SVCoachSchedule upvoteSchedule(UUID scheduleId) {
    SVCoachSchedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
    schedule.setUpvotes(schedule.getUpvotes() + 1);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public SVCoachSchedule upvoteSchedule(UUID scheduleId, SVUser creator) {
    SVCoachSchedule schedule = this.upvoteSchedule(scheduleId);

    upvoteService.addUpvote(SVEntityType.COACH, SVInfoType.SCHEDULE, scheduleId,
        creator);

    return schedule;
  }

  @Transactional
  public SVCoachPrice updatePrice(UUID coachId, UUID priceId, SVCoachPrice price) {
    SVCoach coach = getCoach(coachId);
    if (!isOwner(coachId, coach.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update price");
    }
    price.setId(priceId);
    price.setCoachId(coachId);
    return priceRepository.save(price);
  }

  @Transactional
  public SVCoachPrice upvotePrice(UUID priceId) {
    SVCoachPrice coachPrice = coachPriceRepository.findById(priceId)
        .orElseThrow(() -> new EntityNotFoundException("Price not found"));
    coachPrice.setUpvotes(coachPrice.getUpvotes() + 1);
    return coachPriceRepository.save(coachPrice);
  }

  @Transactional
  public SVCoachPrice upvotePrice(UUID priceId, SVUser creator) {
    SVCoachPrice coachPrice = this.upvotePrice(priceId);
    upvoteService.addUpvote(SVEntityType.COACH, SVInfoType.PRICE, priceId, creator);

    return coachPrice;
  }

  @Transactional
  public List<SVCoachPrice> addPrice(SVUser creator, UUID coachId, List<SVCoachPrice> prices) {
    for (SVCoachPrice price : prices) {
      price.setSubmittedBy(creator);
      price.setCoachId(coachId);
    }
    return priceRepository.saveAll(prices);
  }

  public boolean isSessionUserOwner(String coachId) {
    UUID coachUuid = UUID.fromString(coachId);
    SVCoach coach = getCoach(coachUuid);
    UUID userId = SVAuthenticationUtils.getCurrentUser().getId();
    return coach.getOwner().getId().equals(userId);
  }

  private boolean isOwner(UUID coachId, UUID userId) {
    SVCoach coach = getCoach(coachId);
    return coach.getOwner().getId().equals(userId);
  }
}