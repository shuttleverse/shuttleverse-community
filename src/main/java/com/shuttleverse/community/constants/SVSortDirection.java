package com.shuttleverse.community.constants;

import jakarta.ws.rs.BadRequestException;
import org.springframework.data.domain.Sort.Direction;

public enum SVSortDirection {
  ASC, DESC;

  public static SVSortDirection fromString(String value) {
    try {
      return SVSortDirection.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid sort type");
    }
  }

  public Direction toPagableDirection() {
    return switch (this) {
      case ASC -> Direction.ASC;
      case DESC -> Direction.DESC;
    };
  }
}
