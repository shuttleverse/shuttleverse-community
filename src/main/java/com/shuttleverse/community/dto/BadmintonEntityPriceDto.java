package com.shuttleverse.community.dto;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BadmintonEntityPriceDto {

  protected UUID id;
  protected Double price;
  protected Integer upvotes;
  protected boolean verified;
  protected UUID parentEntityId;
  protected UserResponse submittedBy;
  protected ZonedDateTime createdAt;
  protected ZonedDateTime updatedAt;
}
