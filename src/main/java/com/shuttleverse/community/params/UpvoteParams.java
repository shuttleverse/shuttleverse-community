package com.shuttleverse.community.params;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpvoteParams {

  @NotNull
  private Integer entityType;
  @NotNull
  private Integer infoType;

  public UpvoteParams(Integer entityType, Integer infoType) {
    this.entityType = entityType;
    this.infoType = infoType;
  }
}
