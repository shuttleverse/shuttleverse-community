package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
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

import java.util.UUID;

@RestController
@RequestMapping("/court")
@RequiredArgsConstructor
public class CourtController {

  private final CourtService courtService;

  @PostMapping
  public ResponseEntity<ApiResponse<Court>> createCourt(@Validated @RequestBody Court court) {
    return ResponseEntity.ok(ApiResponse.success(courtService.createCourt(court)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Court>> getCourt(@PathVariable UUID id) {
    Court court = courtService.getCourt(id);
    return ResponseEntity.ok(ApiResponse.success(court));
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Court>> updateCourt(
      @PathVariable UUID id,
      @Validated @RequestBody Court court) {
    Court updatedCourt = courtService.updateCourt(id, court);
    return ResponseEntity.ok(ApiResponse.success(updatedCourt));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> deleteCourt(@PathVariable UUID id) {
    courtService.deleteCourt(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/{id}/schedule")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CourtSchedule>> addSchedule(
      @PathVariable UUID id,
      @Validated @RequestBody CourtSchedule schedule) {
    CourtSchedule newSchedule = courtService.addSchedule(id, schedule);
    return ResponseEntity.ok(ApiResponse.success(newSchedule));
  }

  @PostMapping("/{id}/price")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CourtPrice>> addPrice(
      @PathVariable UUID id,
      @Validated @RequestBody CourtPrice price) {
    CourtPrice newPrice = courtService.addPrice(id, price);
    return ResponseEntity.ok(ApiResponse.success(newPrice));
  }

  @PutMapping("/{id}/schedule/{scheduleId}")
  @PreAuthorize("@courtService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<CourtSchedule>> updateSchedule(
      @PathVariable UUID id,
      @PathVariable UUID scheduleId,
      @Validated @RequestBody CourtSchedule schedule) {
    return ResponseEntity.ok(
        ApiResponse.success(courtService.updateSchedule(id, scheduleId, schedule)));
  }

  @PostMapping("/{id}/upvote-schedule/{scheduleId}")
  public ResponseEntity<ApiResponse<CourtSchedule>> upvoteSchedule(
      @PathVariable UUID id,
      @PathVariable UUID scheduleId) {
    return ResponseEntity.ok(ApiResponse.success(courtService.upvoteSchedule(scheduleId)));
  }
}