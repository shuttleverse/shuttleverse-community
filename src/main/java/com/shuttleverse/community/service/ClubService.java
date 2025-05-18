package com.shuttleverse.community.service;

import com.shuttleverse.community.model.Club;
import com.shuttleverse.community.repository.ClubRepository;
import com.shuttleverse.community.util.SpecificationBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

  public Club getClub(UUID id) {
    return clubRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Club not found with id: " + id));
  }

  public Page<Club> getAllClubs(Map<String, String> filters, Pageable pageable) {
    Specification<Club> spec = SpecificationBuilder.buildSpecification(filters);
    return clubRepository.findAll(spec, pageable);
  }

  @Transactional
  public Club updateClub(UUID id, Club club) {
    Club existingClub = getClub(id);
    if (!isOwner(id, existingClub.getOwnerId())) {
      throw new AccessDeniedException("Only the owner can update club information");
    }
    club.setId(id);
    return clubRepository.save(club);
  }

  @Transactional
  public void deleteClub(UUID id) {
    Club club = getClub(id);
    if (!isOwner(id, club.getOwnerId())) {
      throw new AccessDeniedException("Only the owner can delete the club");
    }
    clubRepository.delete(club);
  }

  public boolean isOwner(UUID clubId, UUID userId) {
    Club club = getClub(clubId);
    return club.getOwnerId() != null && club.getOwnerId().equals(userId);
  }
}