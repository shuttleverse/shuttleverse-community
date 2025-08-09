package com.shuttleverse.community.constants;

import jakarta.ws.rs.BadRequestException;
import lombok.Getter;

@Getter
public enum SVInfoType {
  SCHEDULE,
  PRICE;

  public static SVInfoType fromOrdinal(int ordinal) {
    try {
      return values()[ordinal];
    } catch (IndexOutOfBoundsException e) {
      throw new BadRequestException("Invalid info type: " + ordinal);
    }
  }
}
