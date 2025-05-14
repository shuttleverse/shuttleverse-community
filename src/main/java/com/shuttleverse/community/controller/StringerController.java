package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import com.shuttleverse.community.service.StringerService;
import java.util.UUID;
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
@RequestMapping("/stringer")
@RequiredArgsConstructor
public class StringerController {

  private final StringerService stringerService;

  @PostMapping
  public ResponseEntity<ApiResponse<Stringer>> createStringer(
      @Validated @RequestBody Stringer stringer) {
    return ResponseEntity.ok(ApiResponse.success(stringerService.createStringer(stringer)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Stringer>> getStringer(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(stringerService.getStringer(id)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@stringerService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Stringer>> updateStringer(
      @PathVariable UUID id,
      @Validated @RequestBody Stringer stringer) {
    return ResponseEntity.ok(ApiResponse.success(stringerService.updateStringer(id, stringer)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@stringerService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Void>> deleteStringer(@PathVariable UUID id) {
    stringerService.deleteStringer(id);
    return ResponseEntity.ok(ApiResponse.success("Stringer deleted successfully", null));
  }

  @PostMapping("/{id}/price")
  public ResponseEntity<ApiResponse<StringerPrice>> addPrice(
      @PathVariable UUID id,
      @Validated @RequestBody StringerPrice price) {
    return ResponseEntity.ok(ApiResponse.success(stringerService.addPrice(id, price)));
  }

  @PutMapping("/{id}/price/{priceId}")
  @PreAuthorize("@stringerService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<StringerPrice>> updatePrice(
      @PathVariable UUID id,
      @PathVariable UUID priceId,
      @Validated @RequestBody StringerPrice price) {
    return ResponseEntity.ok(ApiResponse.success(stringerService.updatePrice(id, priceId, price)));
  }

  @PostMapping("/{id}/upvote-price/{priceId}")
  public ResponseEntity<ApiResponse<StringerPrice>> upvotePrice(
      @PathVariable UUID id,
      @PathVariable UUID priceId) {
    return ResponseEntity.ok(ApiResponse.success(stringerService.upvotePrice(priceId)));
  }
}