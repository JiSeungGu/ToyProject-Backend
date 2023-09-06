package com.example.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName   : com.example.exception
 * fileName  : ErrorCode
 * author    : jiseung-gu
 * date  : 2023/08/21
 * description :
 **/
@AllArgsConstructor
@Getter
public enum ErrorCode {
  FAILED_TO_VALIDATE_APPLE_LOGIN(HttpStatus.OK, "애플 로그인 검증에 실패하였습니다."),
  FAILED_TO_VALIDATE_GOOGLE_LOGIN(HttpStatus.OK, "구글 로그인 검증에 실패하였습니다."),
  FAILED_TO_VALIDATE_KAKAO_LOGIN(HttpStatus.OK, "카카오 로그인 검증에 실패하였습니다."),
  FAILED_TO_VALIDATE_NAVER_LOGIN(HttpStatus.OK, "네이버 로그인 검증에 실패하였습니다."),

  FAILED_TO_GENERATE_JWT_TOKEN(HttpStatus.FORBIDDEN, "JWT 토큰 생성에 실패하였습니다."),
  FAILED_TO_VALIDATE_JWT_TOKEN(HttpStatus.FORBIDDEN, "JWT 토큰 검증에 실패하였습니다."),

  EXPRIED_JWT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access 토큰이 만료되었습니다."),
  NOT_EXPRIED_JWT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access 토큰이 만료되지 않았습니다."),
  EXPRIED_JWT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 만료되었습니다."),

  INVALID_JWT_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 JWT 토큰입니다."),

  REFRESHTOKEN_IS_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "최근에 발급받은 토큰과 일치하지 않습니다."),
  DEVICE_IS_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "최근에 발급받은 토큰의 디바이스 정보와 일치하지 않습니다."),

  FAILED_TO_GET_USER_INFO(HttpStatus.OK, "사용자 정보를 가져오는데 실패하였습니다."),

  NOT_FOUND_USER(HttpStatus.OK, "사용자를 찾을 수 없습니다.");

  private HttpStatus status;
  private String message;
}
