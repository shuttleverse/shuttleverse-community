package com.shuttleverse.community.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

  private UUID id;

  private String username;

  private String bio;

}
