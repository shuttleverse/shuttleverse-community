package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.ApiResponse;
import com.shuttleverse.community.model.Club;
import com.shuttleverse.community.service.ClubService;
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
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {

  private final ClubService clubService;

  @PostMapping
  public ResponseEntity<ApiResponse<Club>> createClub(@Validated @RequestBody Club club) {
    return ResponseEntity.ok(ApiResponse.success(clubService.createClub(club)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Club>> getClub(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(clubService.getClub(id)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@clubService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Club>> updateClub(
      @PathVariable UUID id,
      @Validated @RequestBody Club club) {
    return ResponseEntity.ok(ApiResponse.success(clubService.updateClub(id, club)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@clubService.isOwner(#id, authentication.principal)")
  public ResponseEntity<ApiResponse<Void>> deleteClub(@PathVariable UUID id) {
    clubService.deleteClub(id);
    return ResponseEntity.ok(ApiResponse.success("Club deleted successfully", null));
  }
}