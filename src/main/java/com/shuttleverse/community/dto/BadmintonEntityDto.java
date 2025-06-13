package com.shuttleverse.community.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BadmintonEntityDto {

  protected UUID id;
  protected String name;
  protected String location;
  protected LocationDto locationPoint;
  protected String description;
  protected String phoneNumber;
  protected String otherContacts;
}
