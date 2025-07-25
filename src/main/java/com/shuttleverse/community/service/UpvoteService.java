package com.shuttleverse.community.service;

import com.shuttleverse.community.constants.BadmintonEntityType;
import com.shuttleverse.community.constants.BadmintonInfoType;
import com.shuttleverse.community.dto.UpvoteResponse;
import com.shuttleverse.community.mapper.MapStructMapper;
import com.shuttleverse.community.model.Upvote;
import com.shuttleverse.community.model.User;
import com.shuttleverse.community.repository.CoachPriceRepository;
import com.shuttleverse.community.repository.CoachScheduleRepository;
import com.shuttleverse.community.repository.CourtPriceRepository;
import com.shuttleverse.community.repository.CourtScheduleRepository;
import com.shuttleverse.community.repository.StringerPriceRepository;
import com.shuttleverse.community.repository.UpvoteRepository;
import com.shuttleverse.community.util.SpecificationBuilder;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpvoteService {

  private final UpvoteRepository upvoteRepository;
  private final CourtPriceRepository courtPriceRepository;
  private final CourtScheduleRepository courtScheduleRepository;
  private final CoachScheduleRepository coachScheduleRepository;
  private final CoachPriceRepository coachPriceRepository;
  private final StringerPriceRepository stringerPriceRepository;
  private final MapStructMapper mapper;

  @Transactional
  public void addUpvote(BadmintonEntityType badmintonEntityType,
      BadmintonInfoType badmintonInfoType, UUID entityId, User creator) {
    if (upvoteRepository.findByUpvoteCreatorAndEntityId(creator, entityId).isPresent()) {
      throw new IllegalStateException("Upvote already exists");
    }

    Upvote upvote = new Upvote();
    upvote.setEntityType(badmintonEntityType);
    upvote.setInfoType(badmintonInfoType);
    upvote.setEntityId(entityId);
    upvote.setUpvoteCreator(creator);
    upvoteRepository.save(upvote);
  }

  @Transactional(readOnly = true)
  public Page<UpvoteResponse> getAllUpvotes(Map<String, String> filters, Pageable pageable) {
    Specification<Upvote> spec = SpecificationBuilder.buildSpecification(filters);
    Page<Upvote> upvotes = upvoteRepository.findAll(spec, pageable);
    return upvotes.map(this::toUpvoteResponse);
  }

  private UpvoteResponse toUpvoteResponse(Upvote upvote) {
    return UpvoteResponse.builder()
        .upvoteId(upvote.getUpvoteId())
        .entityType(upvote.getEntityType())
        .infoType(upvote.getInfoType())
        .entity(this.findEntityByType(upvote.getEntityType(), upvote.getInfoType(),
            upvote.getEntityId()))
        .upvoteCreator(mapper.userToUserDto(upvote.getUpvoteCreator()))
        .createdAt(upvote.getCreatedAt())
        .build();
  }

  private Object findEntityByType(BadmintonEntityType entityType,
      BadmintonInfoType infoType, UUID entityId) {
    return getRepositoryByType(entityType, infoType).findById(entityId);
  }

  private JpaRepository<?, UUID> getRepositoryByType(BadmintonEntityType entityType,
      BadmintonInfoType infoType) {
    Map<String, JpaRepository<?, UUID>> repoMap = Map.of(
        "COURT_SCHEDULE", courtScheduleRepository,
        "COURT_PRICE", courtPriceRepository,
        "STRINGER_PRICE", stringerPriceRepository,
        "COACH_SCHEDULE", coachScheduleRepository,
        "COACH_PRICE", coachPriceRepository);

    String key = entityType + "_" + infoType;
    JpaRepository<?, UUID> repository = repoMap.get(key);

    if (repository == null) {
      throw new IllegalArgumentException(
          String.format("No repository found for %s with %s", entityType, infoType));
    }

    return repository;
  }
}
