package com.shuttleverse.community.service;

import com.shuttleverse.community.constants.SVVerificationStatus;
import com.shuttleverse.community.model.SVOwnershipClaim;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.model.SVVerificationFile;
import com.shuttleverse.community.params.SVOwnershipClaimCreationData;
import com.shuttleverse.community.repository.SVOwnershipClaimRepository;
import com.shuttleverse.community.repository.SVVerificationFileRepository;
import com.shuttleverse.community.resolver.SVBaseEntityResolver;
import com.shuttleverse.community.util.SVAuthenticationUtils;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class SVOwnershipClaimService {

  private final SVR2StorageService storageService;
  private final SVOwnershipClaimRepository ownershipClaimRepository;
  private final SVVerificationFileRepository verificationFileRepository;
  private final SVBaseEntityResolver baseEntityResolver;
  private final SVCoachService coachService;
  private final SVCourtService courtService;
  private final SVStringerService stringerService;

  public Page<SVOwnershipClaim> getClaims(Pageable pageable) {
    return ownershipClaimRepository.findAllWithCreatorAndFiles(pageable);
  }

  public List<SVOwnershipClaim> findAllByUserId(UUID userId) {
    return ownershipClaimRepository.findAllByCreatorIdWithFilesOrderByCreatedAtDesc(userId);
  }

  @Transactional
  public SVOwnershipClaim createOwnershipClaim(SVOwnershipClaimCreationData data) {
    if (data.getFiles() == null || data.getFiles().isEmpty()) {
      return null;
    }
    SVUser creator = SVAuthenticationUtils.getCurrentUser();

    SVOwnershipClaim claim = new SVOwnershipClaim();
    claim.setEntityType(data.getEntityType());
    claim.setEntityId(data.getEntityId());
    claim.setUserNotes(data.getUserNotes());
    claim.setCreator(creator);
    claim.setStatus(SVVerificationStatus.PENDING);
    claim = ownershipClaimRepository.save(claim);

    List<SVVerificationFile> files = createVerificationFiles(data.getFiles(), claim, creator);
    claim.setFiles(files);

    return ownershipClaimRepository.save(claim);
  }

  private List<SVVerificationFile> createVerificationFiles(List<MultipartFile> files,
      SVOwnershipClaim claim, SVUser creator) {
    List<SVVerificationFile> savedFiles = new ArrayList<>();

    try {
      for (MultipartFile file : files) {
        String key =
            "claim/" + creator.getUsername() + creator.getId() + "/" + claim.getId() + "/"
                + UUID.randomUUID() + "-"
                + file.getOriginalFilename();
        String fileUrl = storageService.uploadFile(file, key);

        SVVerificationFile verificationFile = new SVVerificationFile();
        verificationFile.setClaimId(claim.getId());
        verificationFile.setFileName(file.getOriginalFilename());
        verificationFile.setFileUrl(fileUrl);

        savedFiles.add(verificationFileRepository.save(verificationFile));
      }

      return savedFiles;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public SVOwnershipClaim updateClaimStatus(UUID claimId, SVVerificationStatus status) {
    SVOwnershipClaim claim = ownershipClaimRepository.findByIdWithCreatorAndFiles(claimId);
    if (claim == null) {
      throw new RuntimeException("Claim not found");
    }

    baseEntityResolver.findById(claim.getEntityId(), claim.getEntityType())
        .setOwner(claim.getCreator());
    claim.setStatus(status);

    if (claim.getStatus() == SVVerificationStatus.APPROVED) {
      switch (claim.getEntityType()) {
        case COACH -> coachService.setInfoVerified(claim.getEntityId());
        case COURT -> courtService.setInfoVerified(claim.getEntityId());
        case STRINGER -> stringerService.setInfoVerified(claim.getEntityId());
        default -> throw new IllegalStateException("Should not get here");
      }
    }

    return ownershipClaimRepository.save(claim);
  }

  public SVOwnershipClaim getClaimById(UUID claimId) {
    SVOwnershipClaim claim = ownershipClaimRepository.findByIdWithCreatorAndFiles(claimId);
    if (claim == null) {
      throw new RuntimeException("Claim not found");
    }
    return claim;
  }

}
