package com.example.exception;

import lombok.Getter;


//SOURCE_DESC 익셉션 핸들러 처리
@Getter
public class CustomException extends RuntimeException{

  private String result;
  private ErrorCode errorCode;
//  private String message;

  public CustomException(ErrorCode errorCode) {
    this.result = "ERROR";
    this.errorCode= errorCode;
//    this.message = errorCode.getMessage();
  }
}
