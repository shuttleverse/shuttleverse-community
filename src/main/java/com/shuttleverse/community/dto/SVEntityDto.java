package com.shuttleverse.community.dto;

import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SVEntityDto {

  protected UUID id;
  protected String name;
  protected String location;
  protected SVLocationDto locationPoint;
  protected String description;
  protected String phoneNumber;
  protected Map<String, String> otherContacts;
}
