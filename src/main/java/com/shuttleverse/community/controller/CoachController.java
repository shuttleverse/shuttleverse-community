package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachPrice;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.model.User;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping
  public ResponseEntity<ApiResponse<Page<Coach>>> getAllCoaches(
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
        Sort.Direction.fromString(sortDir),
        sortBy);

    Page<Coach> coaches = coachService.getAllCoaches(filters, pageable);
    return ResponseEntity.ok(ApiResponse.success(coaches));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Coach>> createCoach(
      @Validated @RequestBody Coach coach,
      @AuthenticationPrincipal Jwt jwt) {

    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return ResponseEntity.ok(ApiResponse.success(coachService.createCoach(coach, creator)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Coach>> getCoach(@PathVariable String id) {
    Coach coach = coachService.getCoach(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success(coach));
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Coach>> updateCoach(
      @PathVariable String id,
      @Validated @RequestBody Coach coach) {
    Coach updatedCoach = coachService.updateCoach(UUID.fromString(id), coach);
    return ResponseEntity.ok(ApiResponse.success(updatedCoach));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> deleteCoach(@PathVariable String id) {
    coachService.deleteCoach(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/{id}/schedule")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<CoachSchedule>>> addSchedule(
      @PathVariable String id,
      @Validated @RequestBody List<CoachSchedule> schedules, @AuthenticationPrincipal Jwt jwt) {

    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    List<CoachSchedule> newSchedules = coachService.addSchedule(creator, UUID.fromString(id),
        schedules);
    return ResponseEntity.ok(ApiResponse.success(newSchedules));
  }

  @PutMapping("/{id}/schedule/{scheduleId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CoachSchedule>> updateSchedule(
      @PathVariable String id,
      @PathVariable String scheduleId,
      @Validated @RequestBody CoachSchedule schedule) {
    CoachSchedule updatedSchedule = coachService.updateSchedule(UUID.fromString(id),
        UUID.fromString(scheduleId),
        schedule);
    return ResponseEntity.ok(ApiResponse.success(updatedSchedule));
  }

  @PostMapping("/{id}/price")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<CoachPrice>>> addPrice(
      @PathVariable String id,
      @Validated @RequestBody List<CoachPrice> prices,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    return ResponseEntity.ok(
        ApiResponse.success(coachService.addPrice(creator, UUID.fromString(id), prices)));
  }

  @PutMapping("/{id}/price/{priceId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CoachPrice>> updatePrice(
      @PathVariable String id,
      @PathVariable String priceId,
      @Validated @RequestBody CoachPrice price) {
    return ResponseEntity.ok(
        ApiResponse.success(
            coachService.updatePrice(UUID.fromString(id), UUID.fromString(priceId), price)));
  }

  @PostMapping("/price/{priceId}/upvote")
  public ResponseEntity<ApiResponse<CoachPrice>> upvotePrice(
      @PathVariable String priceId) {
    return ResponseEntity.ok(
        ApiResponse.success(coachService.upvotePrice(UUID.fromString(priceId))));
  }

  @PostMapping("/schedule/{scheduleId}/upvote")
  public ResponseEntity<ApiResponse<CoachSchedule>> upvoteSchedule(
      @PathVariable String scheduleId) {
    return ResponseEntity.ok(
        ApiResponse.success(coachService.upvoteSchedule(UUID.fromString(scheduleId))));
  }
}