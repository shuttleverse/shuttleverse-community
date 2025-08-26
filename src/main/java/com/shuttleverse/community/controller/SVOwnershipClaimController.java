package com.shuttleverse.community.controller;

import com.shuttleverse.community.api.SVApiResponse;
import com.shuttleverse.community.constants.SVVerificationStatus;
import com.shuttleverse.community.dto.SVOwnershipClaimResponse;
import com.shuttleverse.community.mapper.SVMapStructMapper;
import com.shuttleverse.community.model.SVOwnershipClaim;
import com.shuttleverse.community.params.SVOwnershipClaimCreationData;
import com.shuttleverse.community.service.SVOwnershipClaimService;
import com.shuttleverse.community.util.SVAuthenticationUtils;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/claim")
@RequiredArgsConstructor
public class SVOwnershipClaimController {

  private final SVOwnershipClaimService claimService;
  private final SVMapStructMapper mapper;

  @GetMapping("/all")
  @PreAuthorize("@SVAuthenticationUtils.isCurrentUserAdmin()")
  public ResponseEntity<SVApiResponse<Page<SVOwnershipClaimResponse>>> getAllClaims(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    if (!SVAuthenticationUtils.getCurrentUser().isAdmin()) {
      throw new AccessDeniedException("Unauthorized to access this resource");
    }
    Pageable pageable = PageRequest.of(page, size);
    Page<SVOwnershipClaim> claims = claimService.getClaims(pageable);
    Page<SVOwnershipClaimResponse> response = claims.map(mapper::toOwnershipClaimResponse);
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<SVApiResponse<SVOwnershipClaimResponse>> createClaim(
      @ModelAttribute SVOwnershipClaimCreationData data) {
    SVOwnershipClaim claim = claimService.createOwnershipClaim(data);
    return ResponseEntity.ok(SVApiResponse.success(mapper.toOwnershipClaimResponse(claim)));
  }

  @GetMapping("/me")
  public ResponseEntity<SVApiResponse<List<SVOwnershipClaimResponse>>> getUserClaims() {
    List<SVOwnershipClaim> claims = claimService.findAllByUserId(
        SVAuthenticationUtils.getCurrentUser().getId());
    List<SVOwnershipClaimResponse> response = claims.stream()
        .map(mapper::toOwnershipClaimResponse)
        .collect(java.util.stream.Collectors.toList());
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @PutMapping("/{id}/status/{status}")
  @PreAuthorize("@SVAuthenticationUtils.isCurrentUserAdmin()")
  public ResponseEntity<SVApiResponse<SVOwnershipClaimResponse>> updateClaimStatus(
      @PathVariable String id,
      @PathVariable String status) {
    if (!SVAuthenticationUtils.getCurrentUser().isAdmin()) {
      throw new AccessDeniedException("Unauthorized to access this resource");
    }
    SVVerificationStatus verificationStatus = SVVerificationStatus.valueOf(status.toUpperCase());
    SVOwnershipClaim updatedClaim = claimService.updateClaimStatus(UUID.fromString(id),
        verificationStatus);
    SVOwnershipClaimResponse response = mapper.toOwnershipClaimResponse(updatedClaim);
    return ResponseEntity.ok(SVApiResponse.success(response));
  }
}
