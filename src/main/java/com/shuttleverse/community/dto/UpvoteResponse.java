package com.shuttleverse.community.dto;

import com.shuttleverse.community.constants.BadmintonEntityType;
import com.shuttleverse.community.constants.BadmintonInfoType;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpvoteResponse {

  private UUID upvoteId;
  private UserResponse upvoteCreator;
  private BadmintonEntityType entityType;
  private BadmintonInfoType infoType;
  private Object entity;
  private ZonedDateTime createdAt;
}
