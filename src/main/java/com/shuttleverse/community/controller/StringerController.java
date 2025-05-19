package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Stringer;
import com.shuttleverse.community.model.StringerPrice;
import com.shuttleverse.community.model.User;
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
@RequestMapping("/stringer")
@RequiredArgsConstructor
public class StringerController {

  private final StringerService stringerService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<ApiResponse<Page<Stringer>>> getAllStringers(
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

    Page<Stringer> stringers = stringerService.getAllStringers(filters, pageable);
    return ResponseEntity.ok(ApiResponse.success(stringers));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Stringer>> createStringer(
      @Validated @RequestBody Stringer stringer,
      @AuthenticationPrincipal Jwt jwt) {

    String sub = jwt.getSubject();
    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return ResponseEntity.ok(
        ApiResponse.success(stringerService.createStringer(stringer, creator)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Stringer>> getStringer(@PathVariable String id) {
    return ResponseEntity.ok(ApiResponse.success(stringerService.getStringer(UUID.fromString(id))));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@stringerService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Stringer>> updateStringer(
      @PathVariable String id,
      @Validated @RequestBody Stringer stringer) {
    return ResponseEntity.ok(
        ApiResponse.success(stringerService.updateStringer(UUID.fromString(id), stringer)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@stringerService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Void>> deleteStringer(@PathVariable String id) {
    stringerService.deleteStringer(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success("Stringer deleted successfully", null));
  }

  @PostMapping("/{id}/price")
  public ResponseEntity<ApiResponse<List<StringerPrice>>> addPrice(
      @PathVariable String id,
      @Validated @RequestBody List<StringerPrice> prices,
      @AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getSubject();

    User creator = userService.findBySub(sub)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    return ResponseEntity.ok(
        ApiResponse.success(stringerService.addPrice(creator, UUID.fromString(id), prices)));
  }

  @PutMapping("/{id}/price/{priceId}")
  @PreAuthorize("@stringerService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<StringerPrice>> updatePrice(
      @PathVariable String id,
      @PathVariable String priceId,
      @Validated @RequestBody StringerPrice price) {
    return ResponseEntity
        .ok(ApiResponse.success(
            stringerService.updatePrice(UUID.fromString(id), UUID.fromString(priceId), price)));
  }

  @PostMapping("/upvote-price/{priceId}")
  public ResponseEntity<ApiResponse<StringerPrice>> upvotePrice(
      @PathVariable String priceId) {
    return ResponseEntity.ok(
        ApiResponse.success(stringerService.upvotePrice(UUID.fromString(priceId))));
  }
}