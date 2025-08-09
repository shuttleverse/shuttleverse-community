package com.shuttleverse.community.dto;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVInfoType;
import com.shuttleverse.community.model.SVUpvotable;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SVUpvoteResponse {

  private UUID upvoteId;
  private SVUserResponse upvoteCreator;
  private SVEntityType entityType;
  private SVInfoType infoType;
  private SVUpvotable entity;
  private ZonedDateTime createdAt;
}
