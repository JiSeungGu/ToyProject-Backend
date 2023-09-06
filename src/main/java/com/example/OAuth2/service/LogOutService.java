package com.example.OAuth2.service;

import com.example.common.response.CommonResult;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * packageName   : com.example.OAuth2.service
 * fileName  : LogOutService
 * author    : jiseung-gu
 * date  : 2023/08/28
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class LogOutService {
  // 계정 연결 해제, 이메일 수신 중단,이메일 수신 활성화,동의 철회, 계정 삭제
  private static final String UNLINK_FROM_APPS = "unlink_from_apps";
  private static final String EMAIL_DISABLED = "email-disabled";
  private static final String EMAIL_ENABLED = "email-enabled";
  private static final String CONSENT_REVOKED = "consent-revoked";
  private static final String ACCOUNT_DELETE = "account-delete";

  public CommonResult AppleLogout(String payload){
//    log.info("AppleLogOut payload : {}", payload);
//    byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
//    String decodedPayload = new String(decodedBytes);
//
//    log.info("AppleLogOut decodedPayload : {}", decodedPayload);
    log.info("AppleLogOut payload : {}", payload);
    try {
      log.info("AppleLogOut referrer_type : {} " + JsonParser.parseString(payload).getAsJsonObject().get("events").getAsJsonObject().get("type").getAsString());
      String userId = JsonParser.parseString(payload).getAsJsonObject().get("events").getAsJsonObject().get("sub").getAsString();

      log.info("AppleLogOut userId : {}", userId);
    } catch (Exception e) {
      log.error("AppleLogOut error : {}", e.getMessage());
    }
    return null;
  }

  public CommonResult GoogleLogout(String payload) {
    log.info("GoogleLogOut payload : {}", payload);
    return null;
  }

  public CommonResult KakaoLogout(String user_id, String referrer_type) {

    if(referrer_type.equals(UNLINK_FROM_APPS)) {
      log.info("KakaoLogOut payload : {}", user_id);

    }  else {
      log.info("KakaoLogOut referrer_type : {}", referrer_type);
    }

    return null;
  }
}
