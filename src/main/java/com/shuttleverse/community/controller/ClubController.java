package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Club;
import com.shuttleverse.community.service.ClubService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {

  private final ClubService clubService;

  @GetMapping
  public ResponseEntity<ApiResponse<Page<Club>>> getAllClubs(
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

    Page<Club> clubs = clubService.getAllClubs(filters, pageable);
    return ResponseEntity.ok(ApiResponse.success(clubs));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Club>> createClub(@Validated @RequestBody Club club) {
    return ResponseEntity.ok(ApiResponse.success(clubService.createClub(club)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Club>> getClub(@PathVariable String id) {
    return ResponseEntity.ok(ApiResponse.success(clubService.getClub(UUID.fromString(id))));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@clubService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Club>> updateClub(
      @PathVariable String id,
      @Validated @RequestBody Club club) {
    return ResponseEntity.ok(ApiResponse.success(clubService.updateClub(UUID.fromString(id), club)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@clubService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Void>> deleteClub(@PathVariable String id) {
    clubService.deleteClub(UUID.fromString(id));
    return ResponseEntity.ok(ApiResponse.success("Club deleted successfully", null));
  }
}