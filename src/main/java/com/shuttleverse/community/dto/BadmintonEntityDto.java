package com.shuttleverse.community.dto;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BadmintonEntityDto {

  protected UUID id;
  protected String name;
  protected String location;
  protected String description;
  protected String phoneNumber;
  protected String otherContacts;
  protected UserResponse creator;
  protected UserResponse owner;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
}
