package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.dto.SVStringerCreationData;
import com.shuttleverse.community.dto.SVStringerPriceResponse;
import com.shuttleverse.community.dto.SVStringerResponse;
import com.shuttleverse.community.mapper.SVMapStructMapper;
import com.shuttleverse.community.model.SVStringer;
import com.shuttleverse.community.model.SVStringerPrice;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.params.SVBoundingBoxParams;
import com.shuttleverse.community.params.SVEntityFilterParams;
import com.shuttleverse.community.params.SVSortParams;
import com.shuttleverse.community.params.SVWithinDistanceParams;
import com.shuttleverse.community.service.SVStringerService;
import com.shuttleverse.community.service.SVUserService;
import com.shuttleverse.community.util.SVAuthenticationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/stringer")
@RequiredArgsConstructor
public class SVStringerController {

  private final SVStringerService stringerService;
  private final SVUserService userService;
  private final SVMapStructMapper mapper;

  @GetMapping("/{id}")
  public ResponseEntity<SVApiResponse<SVStringerResponse>> getStringer(@PathVariable String id) {
    SVStringer stringer = stringerService.getStringer(UUID.fromString(id));
    return ResponseEntity.ok(SVApiResponse.success(mapper.toStringerResponse(stringer)));
  }

  @GetMapping
  public ResponseEntity<SVApiResponse<Page<SVStringerResponse>>> getAllStringers(
      @ModelAttribute SVEntityFilterParams params,
      @ModelAttribute SVSortParams sortParams,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<SVStringer> stringers = stringerService.getAllStringers(params, sortParams, pageable);
    Page<SVStringerResponse> response = stringers.map(mapper::toStringerResponse);
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @GetMapping("/bbox")
  public ResponseEntity<SVApiResponse<Page<SVStringerResponse>>> getCourtsByBoundingBox(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @Valid @ModelAttribute SVBoundingBoxParams params) {
    Pageable pageable = PageRequest.of(
        page,
        size
    );
    Page<SVStringer> stringers = stringerService.getCourtsByBoundingBox(params, pageable);
    Page<SVStringerResponse> response = stringers.map(mapper::toStringerResponse);

    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @GetMapping("/within")
  public ResponseEntity<SVApiResponse<Page<SVStringerResponse>>> getCourtsByDistance(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @Valid @ModelAttribute SVWithinDistanceParams params) {
    Pageable pageable = PageRequest.of(
        page,
        size
    );

    Page<SVStringer> stringers = stringerService.getStringersWithinDistance(params, pageable);
    Page<SVStringerResponse> response = stringers.map(mapper::toStringerResponse);

    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PostMapping
  public ResponseEntity<SVApiResponse<SVStringerResponse>> createStringer(
      @Validated @RequestBody SVStringerCreationData stringerCreationData) {
    SVUser creator = SVAuthenticationUtils.getCurrentUser();

    SVStringer stringer = mapper.toStringer(stringerCreationData);
    SVStringer createdStringer = stringerService.createStringer(stringer, creator);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toStringerResponse(createdStringer)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@SVStringerService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<SVStringerResponse>> updateStringer(
      @PathVariable String id,
      @Validated @RequestBody SVStringer stringer) {
    SVStringer updatedStringer = stringerService.updateStringer(UUID.fromString(id), stringer);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toStringerResponse(updatedStringer)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@SVStringerService.isSessionUserOwner(#id)")
  public ResponseEntity<SVApiResponse<Void>> deleteStringer(@PathVariable String id) {
    stringerService.deleteStringer(UUID.fromString(id));
    return ResponseEntity.ok(SVApiResponse.success(null));
  }

  @PostMapping("/{id}/price")
  public ResponseEntity<SVApiResponse<List<SVStringerPriceResponse>>> addPrice(
      @PathVariable String id,
      @Validated @RequestBody List<SVStringerPrice> prices,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    List<SVStringerPrice> newPrices = stringerService.addPrice(creator, UUID.fromString(id),
        prices);
    List<SVStringerPriceResponse> response = newPrices.stream()
        .map(mapper::toStringerPriceResponse)
        .toList();
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PutMapping("/{id}/price/{priceId}")
  public ResponseEntity<SVApiResponse<SVStringerPriceResponse>> updatePrice(
      @PathVariable String id,
      @PathVariable String priceId,
      @Validated @RequestBody SVStringerPrice price) {
    SVStringerPrice updatedPrice = stringerService.updatePrice(UUID.fromString(id),
        UUID.fromString(priceId), price);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toStringerPriceResponse(updatedPrice)));
  }

  @PostMapping("/price/{priceId}/upvote")
  public ResponseEntity<SVApiResponse<SVStringerPriceResponse>> upvotePrice(
      @PathVariable String priceId,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();
    SVUser creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    SVStringerPrice price = stringerService.upvotePrice(UUID.fromString(priceId), creator);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toStringerPriceResponse(price)));
  }
}