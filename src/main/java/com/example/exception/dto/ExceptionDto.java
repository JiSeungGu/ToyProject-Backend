package com.example.exception.dto;

import com.example.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ExceptionDto {

  private String result;
  private ErrorCode errorCode;
  private String message;
  private String code;
  public ExceptionDto(ErrorCode errorCode) {
    this.result = "ERROR";
    this.code = "-1";
    this.errorCode = errorCode;
//    this.message = errorCode.getMessage();
  }

}
