package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.dto.SVCourtPriceResponse;
import com.shuttleverse.community.dto.SVCourtResponse;
import com.shuttleverse.community.dto.SVCourtScheduleResponse;
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
import com.shuttleverse.community.service.SVCourtService;
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
@RequestMapping("/court")
@RequiredArgsConstructor
public class SVCourtController {

  private final SVCourtService courtService;
  private final SVUserService userService;
  private final SVMapStructMapper mapper;

  @GetMapping
  public ResponseEntity<SVApiResponse<Page<SVCourtResponse>>> getAllCourts(
      @ModelAttribute SVEntityFilterParams params,
      @ModelAttribute SVSortParams sortParams,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    Page<SVCourt> courts = courtService.getAllCourts(params, sortParams, pageable);
    Page<SVCourtResponse> response = courts.map(mapper::toCourtResponse);
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @GetMapping("/bbox")
  public ResponseEntity<SVApiResponse<Page<SVCourtResponse>>> getCourtsByBoundingBox(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @Valid @ModelAttribute SVBoundingBoxParams params) {
    Pageable pageable = PageRequest.of(
        page,
        size
    );
    Page<SVCourt> courts = courtService.getCourtsByBoundingBox(params, pageable);
    Page<SVCourtResponse> response = courts.map(mapper::toCourtResponse);

    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @GetMapping("/within")
  public ResponseEntity<SVApiResponse<Page<SVCourtResponse>>> getCourtsByDistance(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @Valid @ModelAttribute SVWithinDistanceParams params) {
    Pageable pageable = PageRequest.of(
        page,
        size
    );

    Page<SVCourt> stringers = courtService.getCourtsWithinDistance(params, pageable);
    Page<SVCourtResponse> response = stringers.map(mapper::toCourtResponse);

    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PostMapping
  public ResponseEntity<SVApiResponse<SVCourtResponse>> createCourt(
      @Validated @RequestBody SVCourtCreationData courtCreationData,
      @AuthenticationPrincipal Jwt jwt) {

    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    SVCourt court = mapper.toCourt(courtCreationData);

    SVCourt createdCourt = courtService.createCourt(creator, court);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCourtResponse(createdCourt)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<SVApiResponse<SVCourtResponse>> getCourt(@PathVariable String id) {
    SVCourt court = courtService.getCourt(UUID.fromString(id));
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCourtResponse(court)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@SVCourtService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<SVCourtResponse>> updateCourt(
      @PathVariable String id,
      @Validated @RequestBody SVCourtCreationData data) {
    SVCourt updatedCourt = courtService.updateCourt(UUID.fromString(id), data);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCourtResponse(updatedCourt)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<SVApiResponse<Void>> deleteCourt(@PathVariable String id) {
    courtService.deleteCourt(UUID.fromString(id));
    return ResponseEntity.ok(SVApiResponse.success(null));
  }

  @PostMapping("/{id}/schedule")
  public ResponseEntity<SVApiResponse<List<SVCourtScheduleResponse>>> addSchedule(
      @PathVariable String id,
      @Validated @RequestBody List<SVCourtSchedule> schedule,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    List<SVCourtSchedule> newSchedule = courtService.addSchedule(creator, UUID.fromString(id),
        schedule);
    List<SVCourtScheduleResponse> response = newSchedule.stream()
        .map(mapper::toCourtScheduleResponse)
        .toList();
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PostMapping("/{id}/price")
  public ResponseEntity<SVApiResponse<List<SVCourtPriceResponse>>> addPrice(
      @PathVariable String id,
      @Validated @RequestBody List<SVCourtPrice> prices,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    List<SVCourtPrice> newPrices = courtService.addPrice(creator, UUID.fromString(id), prices);
    List<SVCourtPriceResponse> response = newPrices.stream()
        .map(mapper::toCourtPriceResponse)
        .toList();
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PutMapping("/{id}/schedule/{scheduleId}")
  @PreAuthorize("@SVCourtService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<SVCourtScheduleResponse>> updateSchedule(
      @PathVariable String id,
      @PathVariable String scheduleId,
      @Validated @RequestBody SVCourtSchedule schedule) {
    SVCourtSchedule updatedSchedule = courtService.updateSchedule(UUID.fromString(id),
        UUID.fromString(scheduleId),
        schedule);
    return ResponseEntity.ok(
        SVApiResponse.success(mapper.toCourtScheduleResponse(updatedSchedule)));
  }

  @PostMapping("/schedule/{scheduleId}/upvote")
  public ResponseEntity<SVApiResponse<SVCourtScheduleResponse>> upvoteSchedule(
      @PathVariable String scheduleId,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    SVCourtSchedule schedule = courtService.upvoteSchedule(UUID.fromString(scheduleId), creator);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCourtScheduleResponse(schedule)));
  }

  @PostMapping("/price/{priceId}/upvote")
  public ResponseEntity<SVApiResponse<SVCourtPriceResponse>> upvotePrice(
      @PathVariable String priceId,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    SVCourtPrice price = courtService.upvotePrice(UUID.fromString(priceId), creator);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toCourtPriceResponse(price)));
  }
}