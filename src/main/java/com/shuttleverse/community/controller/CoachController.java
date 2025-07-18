package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.dto.CoachCreationData;
import com.shuttleverse.community.dto.CoachPriceResponse;
import com.shuttleverse.community.dto.CoachResponse;
import com.shuttleverse.community.dto.CoachScheduleResponse;
import com.shuttleverse.community.mapper.MapStructMapper;
import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachPrice;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.params.BoundingBoxParams;
import com.shuttleverse.community.params.WithinDistanceParams;
import com.shuttleverse.community.service.CoachService;
import com.shuttleverse.community.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/coach")
@RequiredArgsConstructor
public class CoachController {

  private final CoachService coachService;
  private final UserService userService;
  private final MapStructMapper mapper;

  @GetMapping
  public ResponseEntity<ApiResponse<Page<CoachResponse>>> getAllCoaches(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir,
      @RequestParam Map<String, String> params) {

    Map<String, String> filters = new HashMap<>(params);
    filters.remove("page");
    filters.remove("size");
    filters.remove("sortBy");
    filters.remove("sortDir");

    Pageable pageable = PageRequest.of(
        page,
        size,
        Direction.fromString(sortDir),
        sortBy);

    Page<Coach> coaches = coachService.getAllCoaches(filters, pageable);
    Page<CoachResponse> response = coaches.map(mapper::toCoachResponse);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/bbox")
  public ResponseEntity<ApiResponse<Page<CoachResponse>>> getCoachesByBoundingBox(
      BoundingBoxParams params,
      Pageable pageable) {
    Page<Coach> courts = coachService.getCourtsByBoundingBox(params, pageable);
    Page<CoachResponse> response = courts.map(mapper::toCoachResponse);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/within")
  public ResponseEntity<ApiResponse<Page<CoachResponse>>> getCourtsByDistance(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @ModelAttribute WithinDistanceParams params) {
    Pageable pageable = PageRequest.of(
        page,
        size
    );

    Page<Coach> stringers = coachService.getCoachesWithinDistance(params, pageable);
    Page<CoachResponse> response = stringers.map(mapper::toCoachResponse);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<CoachResponse>> createCoach(
      @Validated @RequestBody CoachCreationData coachCreationData,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Coach coach = mapper.toCoach(coachCreationData);
    Coach createdCoach = coachService.createCoach(coach, creator);
    return ResponseEntity.ok(ApiResponse.success(mapper.toCoachResponse(createdCoach)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CoachResponse>> getCoach(@PathVariable String id) {
    Coach coach = coachService.getCoach(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success(mapper.toCoachResponse(coach)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CoachResponse>> updateCoach(
      @PathVariable String id,
      @Validated @RequestBody Coach coach) {
    Coach updatedCoach = coachService.updateCoach(UUID.fromString(id), coach);
    return ResponseEntity.ok(ApiResponse.success(mapper.toCoachResponse(updatedCoach)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> deleteCoach(@PathVariable String id) {
    coachService.deleteCoach(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/{id}/schedule")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<CoachScheduleResponse>>> addSchedule(
      @PathVariable String id,
      @Validated @RequestBody List<CoachSchedule> schedules, @AuthenticationPrincipal Jwt jwt) {

    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    List<CoachSchedule> newSchedules = coachService.addSchedule(creator, UUID.fromString(id),
        schedules);
    List<CoachScheduleResponse> response = newSchedules.stream()
        .map(mapper::toCoachScheduleResponse)
        .toList();
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/{id}/schedule/{scheduleId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CoachScheduleResponse>> updateSchedule(
      @PathVariable String id,
      @PathVariable String scheduleId,
      @Validated @RequestBody CoachSchedule schedule) {
    CoachSchedule updatedSchedule = coachService.updateSchedule(UUID.fromString(id),
        UUID.fromString(scheduleId),
        schedule);
    return ResponseEntity.ok(ApiResponse.success(mapper.toCoachScheduleResponse(updatedSchedule)));
  }

  @PostMapping("/{id}/price")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<CoachPriceResponse>>> addPrice(
      @PathVariable String id,
      @Validated @RequestBody List<CoachPrice> prices,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    List<CoachPrice> newPrices = coachService.addPrice(creator, UUID.fromString(id), prices);
    List<CoachPriceResponse> response = newPrices.stream()
        .map(mapper::toCoachPriceResponse)
        .toList();
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/{id}/price/{priceId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CoachPriceResponse>> updatePrice(
      @PathVariable String id,
      @PathVariable String priceId,
      @Validated @RequestBody CoachPrice price) {
    CoachPrice updatedPrice = coachService.updatePrice(UUID.fromString(id),
        UUID.fromString(priceId), price);
    return ResponseEntity.ok(ApiResponse.success(mapper.toCoachPriceResponse(updatedPrice)));
  }

  @PostMapping("/price/{priceId}/upvote")
  public ResponseEntity<ApiResponse<CoachPriceResponse>> upvotePrice(
      @PathVariable String priceId,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    CoachPrice price = coachService.upvotePrice(UUID.fromString(priceId), creator);
    return ResponseEntity.ok(ApiResponse.success(mapper.toCoachPriceResponse(price)));
  }

  @PostMapping("/schedule/{scheduleId}/upvote")
  public ResponseEntity<ApiResponse<CoachScheduleResponse>> upvoteSchedule(
      @PathVariable String scheduleId,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    CoachSchedule schedule = coachService.upvoteSchedule(UUID.fromString(scheduleId), creator);
    return ResponseEntity.ok(ApiResponse.success(mapper.toCoachScheduleResponse(schedule)));
  }
}