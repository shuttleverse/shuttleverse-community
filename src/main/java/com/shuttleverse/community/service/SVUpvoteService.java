package com.shuttleverse.community.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.dto.SVUpvoteResponse;
import com.shuttleverse.community.mapper.SVMapStructMapper;
import com.shuttleverse.community.model.SVUpvote;
import com.shuttleverse.community.model.SVUser;
import com.shuttleverse.community.params.UpvoteParams;
import com.shuttleverse.community.query.SVQueryModel;
import com.shuttleverse.community.repository.SVUpvoteRepository;
import com.shuttleverse.community.resolver.SVUpvotableEntityResolver;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SVUpvoteService {

  private final SVUpvoteRepository upvoteRepository;
  private final SVUpvotableEntityResolver upvotableEntityResolver;
  private final SVMapStructMapper mapper;

  @Transactional
  public void addUpvote(SVEntityType badmintonEntityType,
      SVInfoType badmintonInfoType, UUID entityId, SVUser creator) {
    if (upvoteRepository.findByUpvoteCreatorAndEntityId(creator, entityId).isPresent()) {
      throw new IllegalStateException("Upvote already exists");
    }

    SVUpvote upvote = new SVUpvote();
    upvote.setEntityType(badmintonEntityType);
    upvote.setInfoType(badmintonInfoType);
    upvote.setEntityId(entityId);
    upvote.setUpvoteCreator(creator);
    upvoteRepository.save(upvote);
  }

  @Transactional(readOnly = true)
  public Page<SVUpvoteResponse> getAllUpvotes(SVUser user, UpvoteParams params, Pageable pageable) {
    SVEntityType entityType = SVEntityType.fromOrdinal(params.getEntityType());
    SVInfoType infoType = SVInfoType.fromOrdinal(params.getInfoType());
    BooleanExpression predicate = SVQueryModel.upvote.upvoteCreator.id.eq(user.getId())
        .and(SVQueryModel.upvote.entityType.eq(entityType))
        .and(SVQueryModel.upvote.infoType.eq(infoType));

    Page<SVUpvote> upvotes = upvoteRepository.findAll(predicate, pageable);
    return upvotes.map(this::toUpvoteResponse);
  }

  private SVUpvoteResponse toUpvoteResponse(SVUpvote upvote) {
    return SVUpvoteResponse.builder()
        .upvoteId(upvote.getUpvoteId())
        .entityType(upvote.getEntityType())
        .infoType(upvote.getInfoType())
        .entity(upvotableEntityResolver.findById(upvote.getEntityId(), upvote.getEntityType(),
            upvote.getInfoType()))
        .upvoteCreator(mapper.userToUserDto(upvote.getUpvoteCreator()))
        .createdAt(upvote.getCreatedAt())
        .build();
  }
}
