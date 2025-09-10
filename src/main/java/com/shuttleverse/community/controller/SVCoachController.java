package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.dto.SVCoachPriceResponse;
import com.shuttleverse.community.dto.SVCoachResponse;
import com.shuttleverse.community.dto.SVCoachScheduleResponse;
import com.shuttleverse.community.dto.SVEntityPriceCreationData;
import com.shuttleverse.community.dto.SVEntityScheduleCreationData;
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
import com.shuttleverse.community.service.SVCoachService;
import com.shuttleverse.community.service.SVUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/coach")
@RequiredArgsConstructor
public class SVCoachController {

  private final SVCoachService coachService;
  private final SVUserService userService;
  private final SVMapStructMapper mapper;

  @GetMapping("/{id}")
  public ResponseEntity<SVApiResponse<SVCoachResponse>> getCoach(@PathVariable String id) {
    SVCoach coach = coachService.getCoach(UUID.fromString(id));
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCoachResponse(coach)));
  }

  @GetMapping
  public ResponseEntity<SVApiResponse<Page<SVCoachResponse>>> getAllCoaches(
      @ModelAttribute SVEntityFilterParams params,
      @ModelAttribute SVSortParams sortParams,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    Page<SVCoach> coaches = coachService.getAllCoaches(params, sortParams, pageable);
    Page<SVCoachResponse> response = coaches.map(mapper::toCoachResponse);
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @GetMapping("/bbox")
  public ResponseEntity<SVApiResponse<Page<SVCoachResponse>>> getCoachesByBoundingBox(
      @Valid @ModelAttribute SVBoundingBoxParams params,
      Pageable pageable) {
    Page<SVCoach> courts = coachService.getCoachesByBoundingBox(params, pageable);
    Page<SVCoachResponse> response = courts.map(mapper::toCoachResponse);

    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @GetMapping("/within")
  public ResponseEntity<SVApiResponse<Page<SVCoachResponse>>> getCourtsByDistance(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @Valid @ModelAttribute SVWithinDistanceParams params) {
    Pageable pageable = PageRequest.of(
        page,
        size);

    Page<SVCoach> stringers = coachService.getCoachesWithinDistance(params, pageable);
    Page<SVCoachResponse> response = stringers.map(mapper::toCoachResponse);

    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PostMapping
  public ResponseEntity<SVApiResponse<SVCoachResponse>> createCoach(
      @Validated @RequestBody SVCoachCreationData coachCreationData,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    SVCoach coach = mapper.toCoach(coachCreationData);
    SVCoach createdCoach = coachService.createCoach(coach, creator);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCoachResponse(createdCoach)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@SVCoachService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<SVCoachResponse>> updateCoach(
      @PathVariable String id,
      @Validated @RequestBody SVCoachCreationData coachCreationData) {
    SVCoach updatedCoach = coachService.updateCoach(UUID.fromString(id), coachCreationData);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCoachResponse(updatedCoach)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@SVCoachService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<Void>> deleteCoach(@PathVariable String id) {
    coachService.deleteCoach(UUID.fromString(id));
    return ResponseEntity.ok(SVApiResponse.success(null));
  }

  @PostMapping("/{id}/schedule")
  public ResponseEntity<SVApiResponse<List<SVCoachScheduleResponse>>> addSchedule(
      @PathVariable String id,
      @Validated @RequestBody List<SVEntityScheduleCreationData> schedules,
      @AuthenticationPrincipal Jwt jwt) {
    if (coachService.isVerified(id)) {
      throw new AccessDeniedException("Cannot add to verified coach");
    }

    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    List<SVCoachSchedule> addedSchedules = schedules.stream().map(mapper::toCoachSchedule).toList();
    List<SVCoachSchedule> newSchedules = coachService.addSchedule(creator, UUID.fromString(id),
        addedSchedules);
    List<SVCoachScheduleResponse> response = newSchedules.stream()
        .map(mapper::toCoachScheduleResponse)
        .toList();
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PutMapping("/{id}/schedule/{scheduleId}")
  @PreAuthorize("@SVCoachService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<SVCoachScheduleResponse>> updateSchedule(
      @PathVariable String id,
      @PathVariable String scheduleId,
      @Validated @RequestBody SVCoachSchedule schedule) {
    SVCoachSchedule updatedSchedule = coachService.updateSchedule(UUID.fromString(id),
        UUID.fromString(scheduleId),
        schedule);
    return ResponseEntity.ok(
        SVApiResponse.success(mapper.toCoachScheduleResponse(updatedSchedule)));
  }

  @PostMapping("/{id}/price")
  public ResponseEntity<SVApiResponse<List<SVCoachPriceResponse>>> addPrice(
      @PathVariable String id,
      @Validated @RequestBody List<SVEntityPriceCreationData> prices,
      @AuthenticationPrincipal Jwt jwt) {
    if (coachService.isVerified(id)) {
      throw new AccessDeniedException("Cannot add to verified coach");
    }

    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    List<SVCoachPrice> addedPrices = prices.stream().map(mapper::toCoachPrice).toList();
    List<SVCoachPrice> newPrices = coachService.addPrice(creator, UUID.fromString(id), addedPrices);
    List<SVCoachPriceResponse> response = newPrices.stream()
        .map(mapper::toCoachPriceResponse)
        .toList();
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PutMapping("/{id}/price/{priceId}")
  @PreAuthorize("@SVCoachService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<SVCoachPriceResponse>> updatePrice(
      @PathVariable String id,
      @PathVariable String priceId,
      @Validated @RequestBody SVCoachPrice price) {
    SVCoachPrice updatedPrice = coachService.updatePrice(UUID.fromString(id),
        UUID.fromString(priceId), price);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCoachPriceResponse(updatedPrice)));
  }

  @PostMapping("/price/{priceId}/upvote")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SVApiResponse<SVCoachPriceResponse>> upvotePrice(
      @PathVariable String priceId,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    SVCoachPrice price = coachService.upvotePrice(UUID.fromString(priceId), creator);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCoachPriceResponse(price)));
  }

  @PostMapping("/schedule/{scheduleId}/upvote")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SVApiResponse<SVCoachScheduleResponse>> upvoteSchedule(
      @PathVariable String scheduleId,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    SVCoachSchedule schedule = coachService.upvoteSchedule(UUID.fromString(scheduleId), creator);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCoachScheduleResponse(schedule)));
  }

  @PutMapping("/{id}/schedules")
  @PreAuthorize("@SVCoachService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<List<SVCoachScheduleResponse>>> updateAllSchedules(
      @PathVariable String id,
      @Validated @RequestBody List<SVEntityScheduleCreationData> scheduleCreationData) {
    List<SVCoachSchedule> schedules = scheduleCreationData.stream()
        .map(mapper::toCoachSchedule)
        .toList();
    List<SVCoachSchedule> updatedSchedules = coachService.updateAllSchedules(UUID.fromString(id),
        schedules);
    List<SVCoachScheduleResponse> response = updatedSchedules.stream()
        .map(mapper::toCoachScheduleResponse)
        .toList();
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PutMapping("/{id}/prices")
  @PreAuthorize("@SVCoachService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<List<SVCoachPriceResponse>>> updateAllPrices(
      @PathVariable String id,
      @Validated @RequestBody List<SVEntityPriceCreationData> prices) {
    List<SVCoachPrice> newPrices = prices.stream().map(mapper::toCoachPrice).toList();
    List<SVCoachPrice> updatedPrices = coachService.updateAllPrices(UUID.fromString(id), newPrices);
    List<SVCoachPriceResponse> response = updatedPrices.stream()
        .map(mapper::toCoachPriceResponse)
        .toList();
    return ResponseEntity.ok(SVApiResponse.success(response));
  }
}