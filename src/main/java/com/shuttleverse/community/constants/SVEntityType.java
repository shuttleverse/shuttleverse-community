package com.shuttleverse.community.constants;

import jakarta.ws.rs.BadRequestException;
import lombok.Getter;

@Getter
public enum SVEntityType {
  COURT,
  STRINGER,
  COACH;

  public static SVEntityType fromOrdinal(int ordinal) {
    try {
      return values()[ordinal];
    } catch (IndexOutOfBoundsException e) {
      throw new BadRequestException("Invalid entity type: " + ordinal);
    }
  }
}
