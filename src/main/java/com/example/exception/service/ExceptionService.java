package com.example.exception.service;

import com.example.exception.ErrorCode;
import com.example.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  Logger logger = LoggerFactory.getLogger(this.getClass());

  public void throwException() {
    throw new RuntimeException("RuntimeException");
  }
  public String test() {
    if(true) {
      logger.error("FAILED_TO_VALIDATE_APPLE_LOGIN");
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_APPLE_LOGIN);
    }
    return "test";
  }
}
