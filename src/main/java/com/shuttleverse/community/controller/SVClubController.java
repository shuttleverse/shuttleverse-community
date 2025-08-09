package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.model.SVClub;
import com.shuttleverse.community.service.SVClubService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "controller.club.enabled", havingValue = "true")
public class SVClubController {

  private final SVClubService clubService;

  @GetMapping
  public ResponseEntity<SVApiResponse<Page<SVClub>>> getAllClubs(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {

    Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.Direction.fromString(sortDir),
        sortBy);

    Page<SVClub> clubs = clubService.getAllClubs(pageable);
    return ResponseEntity.ok(SVApiResponse.success(clubs));
  }

  @PostMapping
  public ResponseEntity<SVApiResponse<SVClub>> createClub(@Validated @RequestBody SVClub club) {
    return ResponseEntity.ok(SVApiResponse.success(clubService.createClub(club)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<SVApiResponse<SVClub>> getClub(@PathVariable String id) {
    return ResponseEntity.ok(SVApiResponse.success(clubService.getClub(UUID.fromString(id))));
  }

  @PutMapping("/{id}")
  @PreAuthorize("@SVClubService.isOwner(#id, authentication.principal)")
  public ResponseEntity<SVApiResponse<SVClub>> updateClub(
      @PathVariable String id,
      @Validated @RequestBody SVClub club) {
    return ResponseEntity.ok(
        SVApiResponse.success(clubService.updateClub(UUID.fromString(id), club)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@SVClubService.isOwner(#id, authentication.principal)")
  public ResponseEntity<SVApiResponse<Void>> deleteClub(@PathVariable String id) {
    clubService.deleteClub(UUID.fromString(id));
    return ResponseEntity.ok(SVApiResponse.success("Club deleted successfully", null));
  }
}