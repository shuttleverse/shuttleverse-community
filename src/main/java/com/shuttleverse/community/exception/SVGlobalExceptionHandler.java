package com.shuttleverse.community.exception;

import com.shuttleverse.community.api.SVApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SVGlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<SVApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex) {
    log.info(ex.getMessage(), ex);

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(SVApiResponse.error(ex.getMessage(), "ENTITY_NOT_FOUND"));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<SVApiResponse<Void>> handleValidationError(
      ConstraintViolationException ex) {
    log.info(ex.getMessage(), ex);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(SVApiResponse.error(ex.getMessage(), "VALIDATION_ERROR"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<SVApiResponse<Void>> handleValidationError(
      MethodArgumentNotValidException ex) {
    FieldError firstError = ex.getBindingResult().getFieldErrors().get(0);
    log.info(firstError.getField(), firstError.getDefaultMessage());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(SVApiResponse.error(firstError.getDefaultMessage(), "VALIDATION_ERROR"));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<SVApiResponse<Void>> handleBadRequestError(
      BadRequestException ex) {
    log.info(ex.getMessage(), ex);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(SVApiResponse.error(ex.getMessage(), "VALIDATION_ERROR"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<SVApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
    log.info(ex.getMessage(), ex);

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(SVApiResponse.error("Access denied", "ACCESS_DENIED"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<SVApiResponse<Void>> handleGenericError(Exception ex) {
    log.info(ex.getMessage(), ex);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(SVApiResponse.error(ex.getMessage(), "INTERNAL_ERROR"));
  }
}