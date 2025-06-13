package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.dto.CourtCreationData;
import com.shuttleverse.community.dto.CourtPriceResponse;
import com.shuttleverse.community.dto.CourtResponse;
import com.shuttleverse.community.dto.CourtScheduleResponse;
import com.shuttleverse.community.mapper.MapStructMapper;
import com.shuttleverse.community.model.Court;
import com.shuttleverse.community.model.CourtPrice;
import com.shuttleverse.community.model.CourtSchedule;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.service.CourtService;
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
@RequestMapping("/court")
@RequiredArgsConstructor
public class CourtController {

  private final CourtService courtService;
  private final UserService userService;
  private final MapStructMapper mapper;

  @GetMapping
  public ResponseEntity<ApiResponse<Page<CourtResponse>>> getAllCourts(
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

    Page<Court> courts = courtService.getAllCourts(filters, pageable);
    Page<CourtResponse> response = courts.map(mapper::toCourtResponse);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<CourtResponse>> createCourt(
      @Validated @RequestBody CourtCreationData courtCreationData,
      @AuthenticationPrincipal Jwt jwt) {

    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Court court = mapper.toCourt(courtCreationData);

    Court createdCourt = courtService.createCourt(creator, court);
    return ResponseEntity.ok(ApiResponse.success(mapper.toCourtResponse(createdCourt)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CourtResponse>> getCourt(@PathVariable String id) {
    Court court = courtService.getCourt(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success(mapper.toCourtResponse(court)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<CourtResponse>> updateCourt(
      @PathVariable String id,
      @Validated @RequestBody Court court) {
    Court updatedCourt = courtService.updateCourt(UUID.fromString(id), court);
    return ResponseEntity.ok(ApiResponse.success(mapper.toCourtResponse(updatedCourt)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> deleteCourt(@PathVariable String id) {
    courtService.deleteCourt(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/{id}/schedule")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<CourtScheduleResponse>>> addSchedule(
      @PathVariable String id,
      @Validated @RequestBody List<CourtSchedule> schedule,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    List<CourtSchedule> newSchedule = courtService.addSchedule(creator, UUID.fromString(id),
        schedule);
    List<CourtScheduleResponse> response = newSchedule.stream()
        .map(mapper::toCourtScheduleResponse)
        .toList();
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/{id}/price")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<CourtPriceResponse>>> addPrice(
      @PathVariable String id,
      @Validated @RequestBody List<CourtPrice> prices,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    List<CourtPrice> newPrices = courtService.addPrice(creator, UUID.fromString(id), prices);
    List<CourtPriceResponse> response = newPrices.stream()
        .map(mapper::toCourtPriceResponse)
        .toList();
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/{id}/schedule/{scheduleId}")
  @PreAuthorize("@courtService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<CourtScheduleResponse>> updateSchedule(
      @PathVariable String id,
      @PathVariable String scheduleId,
      @Validated @RequestBody CourtSchedule schedule) {
    CourtSchedule updatedSchedule = courtService.updateSchedule(UUID.fromString(id),
        UUID.fromString(scheduleId),
        schedule);
    return ResponseEntity.ok(ApiResponse.success(mapper.toCourtScheduleResponse(updatedSchedule)));
  }

  @PostMapping("/schedule/{scheduleId}/upvote")
  public ResponseEntity<ApiResponse<CourtScheduleResponse>> upvoteSchedule(
      @PathVariable String scheduleId) {
    CourtSchedule schedule = courtService.upvoteSchedule(UUID.fromString(scheduleId));
    return ResponseEntity.ok(ApiResponse.success(mapper.toCourtScheduleResponse(schedule)));
  }

  @PostMapping("/price/{priceId}/upvote")
  public ResponseEntity<ApiResponse<CourtPriceResponse>> upvotePrice(
      @PathVariable String priceId) {
    CourtPrice price = courtService.upvotePrice(UUID.fromString(priceId));
    return ResponseEntity.ok(ApiResponse.success(mapper.toCourtPriceResponse(price)));
  }
}