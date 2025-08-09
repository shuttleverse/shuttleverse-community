package com.shuttleverse.community.service;

import com.shuttleverse.community.model.SVClub;
import com.shuttleverse.community.repository.SVClubRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SVClubService {

  private final SVClubRepository clubRepository;

  @Transactional
  public SVClub createClub(SVClub club) {
    return clubRepository.save(club);
  }

  public SVClub getClub(UUID id) {
    return clubRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Club not found with id: " + id));
  }

  public Page<SVClub> getAllClubs(Pageable pageable) {
    return clubRepository.findAll(pageable);
  }

  @Transactional
  public SVClub updateClub(UUID id, SVClub club) {
    SVClub existingClub = getClub(id);
    if (!isOwner(id, existingClub.getOwnerId())) {
      throw new AccessDeniedException("Only the owner can update club information");
    }
    club.setId(id);
    return clubRepository.save(club);
  }

  @Transactional
  public void deleteClub(UUID id) {
    SVClub club = getClub(id);
    if (!isOwner(id, club.getOwnerId())) {
      throw new AccessDeniedException("Only the owner can delete the club");
    }
    clubRepository.delete(club);
  }

  public boolean isOwner(UUID clubId, UUID userId) {
    SVClub club = getClub(clubId);
    return club.getOwnerId() != null && club.getOwnerId().equals(userId);
  }
}