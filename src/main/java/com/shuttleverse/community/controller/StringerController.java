package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.dto.StringerCreationData;
import com.shuttleverse.community.dto.StringerPriceResponse;
import com.shuttleverse.community.dto.StringerResponse;
import com.shuttleverse.community.mapper.MapStructMapper;
import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.params.BoundingBoxParams;
import com.shuttleverse.community.params.WithinDistanceParams;
import com.shuttleverse.community.service.StringerService;
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
@RequestMapping("/stringer")
@RequiredArgsConstructor
public class StringerController {

  private final StringerService stringerService;
  private final UserService userService;
  private final MapStructMapper mapper;

  @GetMapping
  public ResponseEntity<ApiResponse<Page<StringerResponse>>> getAllStringers(
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

    Page<Stringer> stringers = stringerService.getAllStringers(filters, pageable);
    Page<StringerResponse> response = stringers.map(mapper::toStringerResponse);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/bbox")
  public ResponseEntity<ApiResponse<Page<StringerResponse>>> getCourtsByBoundingBox(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @ModelAttribute BoundingBoxParams params) {
    Pageable pageable = PageRequest.of(
        page,
        size
    );
    Page<Stringer> stringers = stringerService.getCourtsByBoundingBox(params, pageable);
    Page<StringerResponse> response = stringers.map(mapper::toStringerResponse);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/within")
  public ResponseEntity<ApiResponse<Page<StringerResponse>>> getCourtsByDistance(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @ModelAttribute WithinDistanceParams params) {
    Pageable pageable = PageRequest.of(
        page,
        size
    );

    Page<Stringer> stringers = stringerService.getStringersWithinDistance(params, pageable);
    Page<StringerResponse> response = stringers.map(mapper::toStringerResponse);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<StringerResponse>> createStringer(
      @Validated @RequestBody StringerCreationData stringerCreationData,
      @AuthenticationPrincipal Jwt jwt) {

    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Stringer stringer = mapper.toStringer(stringerCreationData);
    Stringer createdStringer = stringerService.createStringer(stringer, creator);
    return ResponseEntity.ok(ApiResponse.success(mapper.toStringerResponse(createdStringer)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<StringerResponse>> getStringer(@PathVariable String id) {
    Stringer stringer = stringerService.getStringer(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success(mapper.toStringerResponse(stringer)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<StringerResponse>> updateStringer(
      @PathVariable String id,
      @Validated @RequestBody Stringer stringer) {
    Stringer updatedStringer = stringerService.updateStringer(UUID.fromString(id), stringer);
    return ResponseEntity.ok(ApiResponse.success(mapper.toStringerResponse(updatedStringer)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> deleteStringer(@PathVariable String id) {
    stringerService.deleteStringer(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/{id}/price")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<StringerPriceResponse>>> addPrice(
      @PathVariable String id,
      @Validated @RequestBody List<StringerPrice> prices,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    List<StringerPrice> newPrices = stringerService.addPrice(creator, UUID.fromString(id), prices);
    List<StringerPriceResponse> response = newPrices.stream()
        .map(mapper::toStringerPriceResponse)
        .toList();
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/{id}/price/{priceId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<StringerPriceResponse>> updatePrice(
      @PathVariable String id,
      @PathVariable String priceId,
      @Validated @RequestBody StringerPrice price) {
    StringerPrice updatedPrice = stringerService.updatePrice(UUID.fromString(id),
        UUID.fromString(priceId), price);
    return ResponseEntity.ok(ApiResponse.success(mapper.toStringerPriceResponse(updatedPrice)));
  }

  @PostMapping("/price/{priceId}/upvote")
  public ResponseEntity<ApiResponse<StringerPriceResponse>> upvotePrice(
      @PathVariable String priceId,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    StringerPrice price = stringerService.upvotePrice(UUID.fromString(priceId), creator);
    return ResponseEntity.ok(ApiResponse.success(mapper.toStringerPriceResponse(price)));
  }
}