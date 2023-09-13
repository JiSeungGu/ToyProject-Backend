package com.example.exception;


import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.exception.dto.ExceptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

  private final ResponseService responseService;

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<?>> handleExceptions(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.errorResponse(exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<?>> handleValidationExceptions(BindingResult bindingResult) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failResponse(bindingResult));
  }
//
//  @ExceptionHandler(TokenValidException.class)
//  public ResponseEntity<ApiResponse<?>> handleTokenValidationException(TokenValidException exception) {
//    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.errorResponse(exception.getMessage()));
//  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<?> ResponseServiceException(CustomException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ExceptionDto(e.getErrorCode()));
  }
}

