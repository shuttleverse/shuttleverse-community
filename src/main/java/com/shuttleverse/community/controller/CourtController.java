package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.service.CourtService;
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
@RequestMapping("/api/v1/courts")
@RequiredArgsConstructor
public class CourtController {

  private final CourtService courtService;

  @PostMapping
  public ResponseEntity<ApiResponse<Court>> createCourt(@Validated @RequestBody Court court) {
    return ResponseEntity.ok(ApiResponse.success(courtService.createCourt(court)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Court>> getCourt(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(courtService.getCourt(id)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@courtService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Court>> updateCourt(
      @PathVariable Long id,
      @Validated @RequestBody Court court) {
    return ResponseEntity.ok(ApiResponse.success(courtService.updateCourt(id, court)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@courtService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Void>> deleteCourt(@PathVariable Long id) {
    courtService.deleteCourt(id);
    return ResponseEntity.ok(ApiResponse.success("Court deleted successfully", null));
  }

  @PostMapping("/{id}/schedule")
  public ResponseEntity<ApiResponse<CourtSchedule>> addSchedule(
      @PathVariable Long id,
      @Validated @RequestBody CourtSchedule schedule) {
    return ResponseEntity.ok(ApiResponse.success(courtService.addSchedule(id, schedule)));
  }

  @PutMapping("/{id}/schedule/{scheduleId}")
  @PreAuthorize("@courtService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<CourtSchedule>> updateSchedule(
      @PathVariable Long id,
      @PathVariable Long scheduleId,
      @Validated @RequestBody CourtSchedule schedule) {
    return ResponseEntity.ok(
        ApiResponse.success(courtService.updateSchedule(id, scheduleId, schedule)));
  }

  @PostMapping("/{id}/upvote-schedule/{scheduleId}")
  public ResponseEntity<ApiResponse<CourtSchedule>> upvoteSchedule(
      @PathVariable Long id,
      @PathVariable Long scheduleId) {
    return ResponseEntity.ok(ApiResponse.success(courtService.upvoteSchedule(scheduleId)));
  }
}