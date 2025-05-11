package com.shuttleverse.community.service;

import com.shuttleverse.community.model.Club;
import com.shuttleverse.community.repository.ClubRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClubService {

  private final ClubRepository clubRepository;

  @Transactional
  public Club createClub(Club club) {
    return clubRepository.save(club);
  }

  public Club getClub(Long id) {
    return clubRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Club not found with id: " + id));
  }

  @Transactional
  public Club updateClub(Long id, Club club) {
    Club existingClub = getClub(id);
    if (!isOwner(id, existingClub.getOwnerId())) {
      throw new AccessDeniedException("Only the owner can update club information");
    }
    club.setId(id);
    return clubRepository.save(club);
  }

  @Transactional
  public void deleteClub(Long id) {
    Club club = getClub(id);
    if (!isOwner(id, club.getOwnerId())) {
      throw new AccessDeniedException("Only the owner can delete the club");
    }
    clubRepository.delete(club);
  }

  public boolean isOwner(Long clubId, Long userId) {
    Club club = getClub(clubId);
    return club.getOwnerId() != null && club.getOwnerId().equals(userId);
  }
}