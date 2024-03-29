package com.example.OAuth2.service;


import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.google.gson.*;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;

/**
 * packageName   : com.example.OAuth2
 * fileName  : IOSAppService
 * author    : jiseung-gu
 * date  : 2023/07/18
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class IOSAppService {

  private final ResponseService responseService;

  public HashMap<String, Object> userIdFromApple(String idToken) throws Exception {
    HashMap<String, Object> data = new HashMap<>();
    StringBuffer result = new StringBuffer();
    try {
      URL url = new URL("https://appleid.apple.com/auth/keys");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      String line = "";

      while ((line = br.readLine()) != null) {
        result.append(line);
      }
    } catch (IOException e) {
      log.info("IOException , FAILED_TO_VALIDATE_APPLE_LOGIN");
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_APPLE_LOGIN);

    }

    JsonParser parser = new JsonParser();
    JsonObject keys = (JsonObject) parser.parse(result.toString());
    JsonArray keyArray = (JsonArray) keys.get("keys");
    System.out.println(keyArray.toString());

    //클라이언트로부터 가져온 identity token String decode
    String jwtToken = new String(Base64.getDecoder().decode(idToken));
    System.out.println("jwtToken :" + jwtToken);
    String[] decodeArray = jwtToken.split("\\.");
//    String[] decodeArray = idToken.split("\\.");

    System.out.println(decodeArray[0]);
    String header = new String(Base64.getDecoder().decode(decodeArray[0]));
//    System.out.println(header);
    //apple에서 제공해주는 kid값과 일치하는지 알기 위해
    JsonElement kid = ((JsonObject) parser.parse(header)).get("kid");
    JsonElement alg = ((JsonObject) parser.parse(header)).get("alg");
    System.out.println(kid.toString());
    System.out.println(alg.toString());
    //써야하는 Element (kid, alg 일치하는 element)
    JsonObject avaliableObject = null;
    for (int i = 0; i < keyArray.size(); i++) {
      JsonObject appleObject = (JsonObject) keyArray.get(i);
      JsonElement appleKid = appleObject.get("kid");
      JsonElement appleAlg = appleObject.get("alg");

      if (Objects.equals(appleKid, kid) && Objects.equals(appleAlg, alg)) {
        avaliableObject = appleObject;
        System.out.println(avaliableObject.toString());
        break;
      }
    }
    //일치하는 공개키 없음
    if (ObjectUtils.isEmpty(avaliableObject)) {
//      throw new Exception("Failed to validate apple login");
//      return responseService.getFailResult(-1, "Failed to validate apple login");
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_APPLE_LOGIN);
    }
    PublicKey publicKey = this.getPublicKey(avaliableObject);

    try {
      Claims userInfo = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jwtToken).getBody();
      JsonObject userInfoObject = (JsonObject) parser.parse(new Gson().toJson(userInfo));
      JsonElement appleAlg = userInfoObject.get("sub");
      String userId = appleAlg.getAsString();
      log.info("userId : {}", userId);

      data.put("data", userId);
      return data;
//    return responseService.getSingleResult(appleAlg);

    } catch (ExpiredJwtException e) {
      // Handle the exception that the JWT token is expired.
      log.error("JWT token is expired", e);
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_APPLE_LOGIN);
      // return responseService.getFailResult(-1, "JWT token is expired");
      // throw new CustomException("JWT token is expired", HttpStatus.UNAUTHORIZED);

    } catch (SignatureException e) {
      // Handle the exception that the JWT token signature is invalid.
      log.error("JWT token signature is invalid", e);
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_APPLE_LOGIN);
//      return responseService.getFailResult(-1, "JWT token signature is invalid");
//      throw new CustomException("JWT token signature is invalid", HttpStatus.UNAUTHORIZED);

    } catch (InvalidClaimException e) {
      //알수없는 토큰 JWT문자열 자체가 잘못되었거나 토큰을 해석할 수 없는 경우
      log.error("JWT token is invalid", e);
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_APPLE_LOGIN);
//      return responseService.getFailResult(-1, "JWT token is invalid");
    }
  }

  public PublicKey getPublicKey(JsonObject object) {
    String nStr = object.get("n").toString();
    String eStr = object.get("e").toString();

    byte[] nBytes = Base64.getUrlDecoder().decode(nStr.substring(1, nStr.length() - 1));
    byte[] eBytes = Base64.getUrlDecoder().decode(eStr.substring(1, eStr.length() - 1));

    BigInteger n = new BigInteger(1, nBytes);
    BigInteger e = new BigInteger(1, eBytes);

    try {
      RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
      return publicKey;
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }
}
