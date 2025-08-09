package com.shuttleverse.community.constants;

import jakarta.ws.rs.BadRequestException;

public enum SVSortType {
  NAME,
  LOCATION;

  public static SVSortType fromString(String value) {
    try {
      return SVSortType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid sort type");
    }
  }

  public String toPageableProperty() {
    return name().toLowerCase();
  }
}