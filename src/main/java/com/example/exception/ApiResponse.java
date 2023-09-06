package com.example.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Service
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {

  private static final String SUCCESS_STATUS = "success";
  private static final String FAIL_STATUS = "fail";
  private static final String ERROR_STATUS = "error";

  private String status;
  private T data;
  private String message;
  private int code;

  public static <T> ApiResponse<T> successResponse(T data) {
    return new ApiResponse<>(SUCCESS_STATUS, data,0, null);
  }

  public static ApiResponse<?> successWithNoContent() {
    return new ApiResponse<>(SUCCESS_STATUS, null,0, null);
  }

  public static ApiResponse<?> failResponse(BindingResult bindingResult) {
    Map<String, String> errors = new HashMap<>();

    List<ObjectError> allErrors = bindingResult.getAllErrors();
    for (ObjectError error : allErrors) {
      if (error instanceof FieldError) {
        errors.put(((FieldError) error).getField(), error.getDefaultMessage());
      } else {
        errors.put(error.getObjectName(), error.getDefaultMessage());
      }
    }
    return new ApiResponse<>(FAIL_STATUS, errors, -1,null);
  }

  public static ApiResponse<?> errorResponse(String message) {
    return new ApiResponse<>(ERROR_STATUS, null, -1,message);
  }

  private ApiResponse(String status, T data, int code,String message) {
    this.status = status;
    this.code = code;
    this.data = data;
    this.message = message;
  }
}
