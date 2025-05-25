package com.shuttleverse.community.dto;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BadmintonEntityScheduleDto {

  protected UUID id;
  protected Integer dayOfWeek;
  protected String startTime;
  protected String endTime;
  protected Integer upvotes;
  protected boolean isVerified;
  protected UUID parentEntityId;
  protected UserResponse submittedBy;
  protected ZonedDateTime createdAt;
  protected ZonedDateTime updatedAt;
}
