package com.shuttleverse.community.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.constants.SVSortType;
import com.shuttleverse.community.model.SVCourt;
import com.shuttleverse.community.model.SVCourtPrice;
import com.shuttleverse.community.model.SVCourtSchedule;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.params.SVBoundingBoxParams;
import com.shuttleverse.community.params.SVEntityFilterParams;
import com.shuttleverse.community.params.SVSortParams;
import com.shuttleverse.community.params.SVWithinDistanceParams;
import com.shuttleverse.community.query.SVQueryFactory;
import com.shuttleverse.community.query.SVQueryModel;
import com.shuttleverse.community.repository.SVCourtPriceRepository;
import com.shuttleverse.community.repository.SVCourtRepository;
import com.shuttleverse.community.repository.SVCourtScheduleRepository;
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
public class SVCourtService {

  private final SVCourtRepository courtRepository;
  private final SVCourtScheduleRepository scheduleRepository;
  private final SVCourtPriceRepository priceRepository;
  private final SVUpvoteService upvoteService;
  private final SVQueryFactory<SVCourt> queryFactory;

  @Transactional
  public SVCourt createCourt(SVUser creator, SVCourt court) {
    court.setCreator(creator);
    return courtRepository.save(court);
  }

  public SVCourt getCourt(UUID id) {
    return courtRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Court not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public Page<SVCourt> getCourtsByBoundingBox(SVBoundingBoxParams params, Pageable pageable) {
    return courtRepository.findWithinBounds(params.getMinLon(), params.getMinLat(),
        params.getMaxLon(), params.getMaxLat(), pageable);
  }

  @Transactional(readOnly = true)
  public Page<SVCourt> getCourtsWithinDistance(SVWithinDistanceParams params, Pageable pageable) {
    return courtRepository.findWithinDistance(params.getLocation(), params.getDistance(), pageable);
  }

  @Transactional(readOnly = true)
  public Page<SVCourt> getAllCourts(SVEntityFilterParams params, SVSortParams sortParams,
      Pageable pageable) {

    List<UUID> ids = findCourtsByPriceAndSchedule(params);

    BooleanExpression predicate = SVQueryModel.court.id.in(ids).and(
        params.getIsVerified() ? SVQueryModel.court.owner.isNotNull()
            : SVQueryModel.court.owner.isNull());

    if (sortParams.getSortType() != SVSortType.LOCATION) {
      Pageable sortedPageable = PageRequest.of(
          pageable.getPageNumber(),
          pageable.getPageSize(),
          sortParams.getSortDirection().toPagableDirection(),
          sortParams.getSortType().toPageableProperty()
      );
      return courtRepository.findAll(predicate, sortedPageable);
    }

    JPAQuery<SVCourt> query = queryFactory.getQuery(SVQueryModel.court, predicate)
        .orderBy(SVQueryUtils.orderByDistance(SVQueryModel.court.locationPoint,
            sortParams.getLocation(), sortParams.getSortDirection()));

    Long queryCount = queryFactory.getQueryCount(SVQueryModel.court, predicate);

    return PageableExecutionUtils.getPage(
        query.offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch(),
        pageable,
        () -> queryCount
    );
  }

  private List<UUID> findCourtsByPriceAndSchedule(SVEntityFilterParams params) {
    Set<UUID> scheduleResults = new HashSet<>();
    Set<UUID> priceResults = new HashSet<>();
    if (params.getDaysOfWeek() != null && !params.getDaysOfWeek().isEmpty()) {
      BooleanExpression schedulePredicate = SVQueryModel.courtSchedule.dayOfWeek.in(
          params.getDaysOfWeek());
      scheduleRepository.findAll(schedulePredicate).forEach(schedule -> {
        scheduleResults.add(schedule.getCourtId());
      });
    }

    BooleanExpression pricePredicate = SVQueryModel.courtPrice.minPrice.goe(params.getMinPrice())
        .and(SVQueryModel.courtPrice.maxPrice.loe(params.getMaxPrice()));
    priceRepository.findAll(pricePredicate).forEach(price -> {
      priceResults.add(price.getCourtId());
    });

    Set<UUID> result = new HashSet<>(priceResults);
    result.retainAll(scheduleResults);

    return !scheduleResults.isEmpty() ? new ArrayList<>(result) : new ArrayList<>(priceResults);
  }

  @Transactional
  public SVCourt updateCourt(UUID id, SVCourt court) {
    if (!isOwner(id, court.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update court information");
    }
    court.setId(id);
    return courtRepository.save(court);
  }

  @Transactional
  public void deleteCourt(UUID id) {
    SVCourt court = getCourt(id);
    if (!isOwner(id, court.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can delete the court");
    }
    courtRepository.delete(court);
  }

  @Transactional
  public List<SVCourtSchedule> addSchedule(SVUser creator, UUID courtId,
      List<SVCourtSchedule> schedule) {
    getCourt(courtId);

    for (SVCourtSchedule courtSchedule : schedule) {
      courtSchedule.setSubmittedBy(creator);
      courtSchedule.setCourtId(courtId);
    }

    return scheduleRepository.saveAll(schedule);
  }

  @Transactional
  public SVCourtSchedule updateSchedule(UUID courtId, UUID scheduleId, SVCourtSchedule schedule) {
    SVCourt court = getCourt(courtId);
    if (!isOwner(courtId, court.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update schedule");
    }
    schedule.setId(scheduleId);
    schedule.setCourtId(courtId);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public SVCourtSchedule upvoteSchedule(UUID scheduleId) {
    SVCourtSchedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
    schedule.setUpvotes(schedule.getUpvotes() + 1);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public SVCourtSchedule upvoteSchedule(UUID scheduleId, SVUser creator) {
    SVCourtSchedule courtSchedule = this.upvoteSchedule(scheduleId);
    upvoteService.addUpvote(SVEntityType.COURT, SVInfoType.SCHEDULE, scheduleId,
        creator);
    return courtSchedule;
  }

  @Transactional
  public List<SVCourtPrice> addPrice(SVUser creator, UUID courtId, List<SVCourtPrice> prices) {
    getCourt(courtId);

    for (SVCourtPrice price : prices) {
      price.setSubmittedBy(creator);
      price.setCourtId(courtId);
    }
    return priceRepository.saveAll(prices);
  }

  @Transactional
  public SVCourtPrice updatePrice(UUID courtId, UUID priceId, SVCourtPrice price) {
    if (!isOwner(courtId, price.getSubmittedBy().getId())) {
      throw new AccessDeniedException("Only the owner can update price");
    }
    price.setId(priceId);
    price.setCourtId(courtId);
    return priceRepository.save(price);
  }

  @Transactional
  public SVCourtPrice upvotePrice(UUID priceId) {
    SVCourtPrice courtPrice = priceRepository.findById(priceId)
        .orElseThrow(() -> new EntityNotFoundException("Price not found"));
    courtPrice.setUpvotes(courtPrice.getUpvotes() + 1);
    return priceRepository.save(courtPrice);
  }

  @Transactional
  public SVCourtPrice upvotePrice(UUID priceId, SVUser creator) {
    SVCourtPrice courtPrice = this.upvotePrice(priceId);

    upvoteService.addUpvote(SVEntityType.COURT, SVInfoType.PRICE, priceId, creator);

    return courtPrice;
  }

  public boolean isSessionUserOwner(String courtId) {
    UUID courtUuid = UUID.fromString(courtId);
    SVCourt court = getCourt(courtUuid);
    UUID userId = SVAuthenticationUtils.getCurrentUser().getId();
    return court.getOwner().getId().equals(userId);
  }

  private boolean isOwner(UUID courtId, UUID userId) {
    SVCourt court = getCourt(courtId);
    return court.getOwner().getId().equals(userId);
  }
}