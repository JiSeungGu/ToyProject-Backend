package com.example.exception.service;

import com.example.exception.ErrorCode;
import com.example.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * packageName   : com.example.exception.service
 * fileName  : ExceptionService
 * author    : jiseung-gu
 * date  : 2023/08/21
 * description :
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class ExceptionService {

  public void throwException() {
    throw new RuntimeException("RuntimeException");
  }
  public String test() {
    if(true) {
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_APPLE_LOGIN);
    }
    return "test";
  }
}
