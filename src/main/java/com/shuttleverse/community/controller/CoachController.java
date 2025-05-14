package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Coach;
import com.shuttleverse.community.model.CoachSchedule;
import com.shuttleverse.community.service.CoachService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/coach")
@RequiredArgsConstructor
public class CoachController {

  private final CoachService coachService;

  @PostMapping
  public ResponseEntity<ApiResponse<Coach>> createCoach(@Validated @RequestBody Coach coach) {
    return ResponseEntity.ok(ApiResponse.success(coachService.createCoach(coach)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Coach>> getCoach(@PathVariable UUID id) {
    Coach coach = coachService.getCoach(id);
    return ResponseEntity.ok(ApiResponse.success(coach));
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Coach>> updateCoach(
      @PathVariable UUID id,
      @Validated @RequestBody Coach coach) {
    Coach updatedCoach = coachService.updateCoach(id, coach);
    return ResponseEntity.ok(ApiResponse.success(updatedCoach));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> deleteCoach(@PathVariable UUID id) {
    coachService.deleteCoach(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/{id}/schedule")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CoachSchedule>> addSchedule(
      @PathVariable UUID id,
      @Validated @RequestBody CoachSchedule schedule) {
    CoachSchedule newSchedule = coachService.addSchedule(id, schedule);
    return ResponseEntity.ok(ApiResponse.success(newSchedule));
  }

  @PutMapping("/{id}/schedule/{scheduleId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CoachSchedule>> updateSchedule(
      @PathVariable UUID id,
      @PathVariable UUID scheduleId,
      @Validated @RequestBody CoachSchedule schedule) {
    CoachSchedule updatedSchedule = coachService.updateSchedule(id, scheduleId, schedule);
    return ResponseEntity.ok(ApiResponse.success(updatedSchedule));
  }

  @PostMapping("/{id}/upvote-schedule/{scheduleId}")
  public ResponseEntity<ApiResponse<CoachSchedule>> upvoteSchedule(
      @PathVariable UUID id,
      @PathVariable UUID scheduleId) {
    return ResponseEntity.ok(ApiResponse.success(coachService.upvoteSchedule(scheduleId)));
  }
}