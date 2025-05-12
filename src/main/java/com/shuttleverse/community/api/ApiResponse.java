package com.shuttleverse.community.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

  private boolean success;
  private String message;
  private T data;
  private String errorCode;

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "Success", data, null);
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data, null);
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null, "ERROR");
  }

  public static <T> ApiResponse<T> error(String message, String errorCode) {
    return new ApiResponse<>(false, message, null, errorCode);
  }
}