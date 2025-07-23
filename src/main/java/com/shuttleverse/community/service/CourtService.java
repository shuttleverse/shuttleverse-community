package com.shuttleverse.community.service;

import com.shuttleverse.community.constants.BadmintonEntityType;
import com.shuttleverse.community.constants.BadmintonInfoType;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.params.BoundingBoxParams;
import com.shuttleverse.community.params.WithinDistanceParams;
import com.shuttleverse.community.repository.CourtPriceRepository;
import com.shuttleverse.community.repository.CourtRepository;
import com.shuttleverse.community.repository.CourtScheduleRepository;
import com.shuttleverse.community.util.AuthenticationUtils;
import com.shuttleverse.community.util.SpecificationBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourtService {

  private final CourtRepository courtRepository;
  private final CourtScheduleRepository scheduleRepository;
  private final CourtPriceRepository priceRepository;
  private final CourtPriceRepository courtPriceRepository;
  private final UpvoteService upvoteService;

  @Transactional
  public Court createCourt(User creator, Court court) {
    court.setCreator(creator);
    return courtRepository.save(court);
  }

  public Court getCourt(UUID id) {
    return courtRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Court not found with id: " + id));
  }

  public Page<Court> getCourtsByBoundingBox(BoundingBoxParams params, Pageable pageable) {
    return courtRepository.findWithinBounds(params.getMinLon(), params.getMinLat(),
        params.getMaxLon(), params.getMaxLat(), pageable);
  }

  public Page<Court> getCourtsWithinDistance(WithinDistanceParams params, Pageable pageable) {
    return courtRepository.findWithinDistance(params.getLocation(), params.getDistance(), pageable);
  }

  public Page<Court> getAllCourts(Map<String, String> filters, Pageable pageable) {
    Specification<Court> spec = SpecificationBuilder.buildSpecification(filters);
    return courtRepository.findAll(spec, pageable);
  }

  @Transactional
  public Court updateCourt(UUID id, Court court) {
    if (!isOwner(id, court.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update court information");
    }
    court.setId(id);
    return courtRepository.save(court);
  }

  @Transactional
  public void deleteCourt(UUID id) {
    Court court = getCourt(id);
    if (!isOwner(id, court.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can delete the court");
    }
    courtRepository.delete(court);
  }

  @Transactional
  public List<CourtSchedule> addSchedule(User creator, UUID courtId, List<CourtSchedule> schedule) {
    getCourt(courtId);

    for (CourtSchedule courtSchedule : schedule) {
      courtSchedule.setSubmittedBy(creator);
      courtSchedule.setCourtId(courtId);
    }

    return scheduleRepository.saveAll(schedule);
  }

  @Transactional
  public CourtSchedule updateSchedule(UUID courtId, UUID scheduleId, CourtSchedule schedule) {
    Court court = getCourt(courtId);
    if (!isOwner(courtId, court.getOwner().getId())) {
      throw new AccessDeniedException("Only the owner can update schedule");
    }
    schedule.setId(scheduleId);
    schedule.setCourtId(courtId);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public CourtSchedule upvoteSchedule(UUID scheduleId) {
    CourtSchedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
    schedule.setUpvotes(schedule.getUpvotes() + 1);
    return scheduleRepository.save(schedule);
  }

  @Transactional
  public CourtSchedule upvoteSchedule(UUID scheduleId, User creator) {
    CourtSchedule courtSchedule = this.upvoteSchedule(scheduleId);
    upvoteService.addUpvote(BadmintonEntityType.COURT, BadmintonInfoType.SCHEDULE, scheduleId,
        creator);
    return courtSchedule;
  }

  @Transactional
  public List<CourtPrice> addPrice(User creator, UUID courtId, List<CourtPrice> prices) {
    getCourt(courtId);

    for (CourtPrice price : prices) {
      price.setSubmittedBy(creator);
      price.setCourtId(courtId);
    }
    return priceRepository.saveAll(prices);
  }

  @Transactional
  public CourtPrice updatePrice(UUID courtId, UUID priceId, CourtPrice price) {
    if (!isOwner(courtId, price.getSubmittedBy().getId())) {
      throw new AccessDeniedException("Only the owner can update price");
    }
    price.setId(priceId);
    price.setCourtId(courtId);
    return priceRepository.save(price);
  }

  @Transactional
  public CourtPrice upvotePrice(UUID priceId) {
    CourtPrice courtPrice = courtPriceRepository.findById(priceId)
        .orElseThrow(() -> new EntityNotFoundException("Price not found"));
    courtPrice.setUpvotes(courtPrice.getUpvotes() + 1);
    return courtPriceRepository.save(courtPrice);
  }

  @Transactional
  public CourtPrice upvotePrice(UUID priceId, User creator) {
    CourtPrice courtPrice = this.upvotePrice(priceId);

    upvoteService.addUpvote(BadmintonEntityType.COURT, BadmintonInfoType.PRICE, priceId, creator);

    return courtPrice;
  }

  public boolean isSessionUserOwner(String courtId) {
    UUID courtUuid = UUID.fromString(courtId);
    Court court = getCourt(courtUuid);
    UUID userId = AuthenticationUtils.getCurrentUser().getId();
    return court.getOwner().getId().equals(userId);
  }

  private boolean isOwner(UUID courtId, UUID userId) {
    Court court = getCourt(courtId);
    return court.getOwner().getId().equals(userId);
  }
}