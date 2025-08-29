package com.shuttleverse.community.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.constants.SVSortType;
import com.shuttleverse.community.dto.SVLocationDto;
import com.shuttleverse.community.mapper.SVMapStructMapper;
import com.shuttleverse.community.model.SVCourt;
import com.shuttleverse.community.model.SVCourtPrice;
import com.shuttleverse.community.model.SVCourtSchedule;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.params.SVBoundingBoxParams;
import com.shuttleverse.community.params.SVCourtCreationData;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SVCourtService {

  private final SVMapStructMapper mapper;
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

    BooleanExpression predicate = SVQueryModel.court.id.in(ids);

    if (params.getIsVerified() != null) {
      predicate = predicate.and(SVQueryModel.court.owner.isNotNull());
    }

    if (sortParams.getSortType() != SVSortType.LOCATION) {
      Pageable sortedPageable = PageRequest.of(
          pageable.getPageNumber(),
          pageable.getPageSize(),
          sortParams.getSortDirection().toPagableDirection(),
          sortParams.getSortType().toPageableProperty());
      return courtRepository.findAll(predicate, sortedPageable);
    }

    JPAQuery<SVCourt> query = queryFactory.getQuery(SVQueryModel.court, predicate)
        .orderBy(SVQueryUtils.orderByDistance(SVQueryModel.court.locationPoint,
            mapper.locationDtoToPoint(
                new SVLocationDto(sortParams.getLongitude(), sortParams.getLatitude())),
            sortParams.getSortDirection()));

    Long queryCount = queryFactory.getQueryCount(SVQueryModel.court, predicate);

    return PageableExecutionUtils.getPage(
        query.offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch(),
        pageable,
        () -> queryCount);
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
  public SVCourt updateCourt(UUID id, SVCourtCreationData data) {
    SVCourt court = courtRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

    mapper.updateCourtFromDto(data, court);

    return courtRepository.save(court);
  }

  @Transactional
  public void deleteCourt(UUID id) {
    SVCourt court = getCourt(id);
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

  @Transactional
  public void setInfoVerified(UUID courtId) {
    List<SVCourtPrice> prices = priceRepository.findAllByCourtId(courtId);
    List<SVCourtSchedule> schedules = scheduleRepository.findAllByCourtId(courtId);

    for (SVCourtPrice price : prices) {
      price.setIsVerified(true);
    }
    for (SVCourtSchedule schedule : schedules) {
      schedule.setIsVerified(true);
    }
    priceRepository.saveAll(prices);
    scheduleRepository.saveAll(schedules);
  }

  public boolean isSessionUserOwner(String courtId) {
    UUID courtUuid = UUID.fromString(courtId);
    SVCourt court = getCourt(courtUuid);
    SVUser user = SVAuthenticationUtils.getCurrentUser();
    return court.getOwner().getId().equals(user.getId()) || user.isAdmin();
  }

  public boolean isVerified(String courtId) {
    UUID courtUuid = UUID.fromString(courtId);
    SVCourt court = getCourt(courtUuid);

    return court.getOwner() != null;
  }

  @Transactional
  public List<SVCourtSchedule> updateAllSchedules(UUID courtId,
      List<SVCourtSchedule> newSchedules) {
    List<SVCourtSchedule> existingSchedules = scheduleRepository.findAllByCourtId(courtId);
    List<SVCourtSchedule> result = new ArrayList<>();

    Map<String, SVCourtSchedule> existingSchedulesMap = new HashMap<>();
    for (SVCourtSchedule schedule : existingSchedules) {
      String key =
          schedule.getDayOfWeek() + "_" + schedule.getOpenTime() + "_" + schedule.getCloseTime();
      log.warn(key);
      existingSchedulesMap.put(key, schedule);
    }

    log.warn(" ____ ");
    for (SVCourtSchedule newSchedule : newSchedules) {
      newSchedule.setCourtId(courtId);

      String key = newSchedule.getDayOfWeek() + "_" + newSchedule.getOpenTime() + "_"
          + newSchedule.getCloseTime();
      SVCourtSchedule existingSchedule = existingSchedulesMap.get(key);
      log.warn(key);

      if (existingSchedule != null) {
        result.add(existingSchedule);
        existingSchedulesMap.remove(key);
      } else {
        newSchedule.setSubmittedBy(SVAuthenticationUtils.getCurrentUser());
        result.add(scheduleRepository.save(newSchedule));
      }
    }

    if (!existingSchedulesMap.isEmpty()) {
      scheduleRepository.deleteAll(existingSchedulesMap.values());
      for (SVCourtSchedule schedule : existingSchedulesMap.values()) {
        upvoteService.deleteUpvoteByEntityId(schedule.getId());
      }
    }

    return result;
  }

  @Transactional
  public List<SVCourtPrice> updateAllPrices(UUID courtId, List<SVCourtPrice> newPrices) {
    List<SVCourtPrice> existingPrices = priceRepository.findAllByCourtId(courtId);
    List<SVCourtPrice> result = new ArrayList<>();

    Map<String, SVCourtPrice> existingPricesMap = new HashMap<>();
    for (SVCourtPrice price : existingPrices) {
      String key = price.getMinPrice() + "_" + price.getMaxPrice() + "_"
          + price.getDuration() + "_" + price.getDurationUnit() + "_"
          + (price.getDescription() != null ? price.getDescription() : "");
      existingPricesMap.put(key, price);
    }

    for (SVCourtPrice newPrice : newPrices) {
      newPrice.setCourtId(courtId);

      String key = newPrice.getMinPrice() + "_" + newPrice.getMaxPrice() + "_"
          + newPrice.getDuration() + "_" + newPrice.getDurationUnit() + "_"
          + (newPrice.getDescription() != null ? newPrice.getDescription() : "");
      SVCourtPrice existingPrice = existingPricesMap.get(key);

      if (existingPrice != null) {
        result.add(existingPrice);
        existingPricesMap.remove(key);
      } else {
        newPrice.setSubmittedBy(SVAuthenticationUtils.getCurrentUser());
        result.add(priceRepository.save(newPrice));
      }
    }

    if (!existingPricesMap.isEmpty()) {
      priceRepository.deleteAll(existingPricesMap.values());
      for (SVCourtPrice price : existingPricesMap.values()) {
        upvoteService.deleteUpvoteByEntityId(price.getId());
      }
    }

    return result;
  }
}