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

@RestController
@RequestMapping("/api/v1/coaches")
@RequiredArgsConstructor
public class CoachController {

  private final CoachService coachService;

  @PostMapping
  public ResponseEntity<ApiResponse<Coach>> createCoach(@Validated @RequestBody Coach coach) {
    return ResponseEntity.ok(ApiResponse.success(coachService.createCoach(coach)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Coach>> getCoach(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(coachService.getCoach(id)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@coachService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Coach>> updateCoach(
      @PathVariable Long id,
      @Validated @RequestBody Coach coach) {
    return ResponseEntity.ok(ApiResponse.success(coachService.updateCoach(id, coach)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@coachService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Void>> deleteCoach(@PathVariable Long id) {
    coachService.deleteCoach(id);
    return ResponseEntity.ok(ApiResponse.success("Coach deleted successfully", null));
  }

  @PostMapping("/{id}/schedule")
  public ResponseEntity<ApiResponse<CoachSchedule>> addSchedule(
      @PathVariable Long id,
      @Validated @RequestBody CoachSchedule schedule) {
    return ResponseEntity.ok(ApiResponse.success(coachService.addSchedule(id, schedule)));
  }

  @PutMapping("/{id}/schedule/{scheduleId}")
  @PreAuthorize("@coachService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<CoachSchedule>> updateSchedule(
      @PathVariable Long id,
      @PathVariable Long scheduleId,
      @Validated @RequestBody CoachSchedule schedule) {
    return ResponseEntity.ok(
        ApiResponse.success(coachService.updateSchedule(id, scheduleId, schedule)));
  }

  @PostMapping("/{id}/upvote-schedule/{scheduleId}")
  public ResponseEntity<ApiResponse<CoachSchedule>> upvoteSchedule(
      @PathVariable Long id,
      @PathVariable Long scheduleId) {
    return ResponseEntity.ok(ApiResponse.success(coachService.upvoteSchedule(scheduleId)));
  }
}