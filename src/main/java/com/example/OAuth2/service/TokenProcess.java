package com.example.OAuth2.service;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.GetPublicKeyRequest;
import com.amazonaws.services.kms.model.GetPublicKeyResult;
import com.amazonaws.services.kms.model.SignRequest;
import com.amazonaws.services.kms.model.SignResult;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.OAuth2.entity.JwtEntity;
import com.example.OAuth2.entity.SnsKeysEntity;
import com.example.OAuth2.repository.JwtTableRepository;
import com.example.common.response.CommonResult;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * packageName   : com.example.OAuth2.service
 * fileName  : JwtService
 * author    : jiseung-gu
 * date  : 2023/07/28
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProcess {
  private static final String KEY_ID = "arn:aws:kms:ap-northeast-2:905126260776:key/b4a93930-1651-4ff7-9ad1-009d1a88f2e4";
  private static final String KEY_SPEC = "RSA_4096";
  private static AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

  private final RedisService redisService;
  private final JwtTableRepository jwtTableRepository;
  private final KaKaoService kaKaoService;
  private final IOSAppService appleService;
  private final GoogleService googleService;
  private final JpaService jpaService;

  public HashMap<String, Object> PlatFormType(HttpServletRequest request) {
    HashMap<String, Object> data = new HashMap<>();
    try {
      switch (request.getHeader("type")) {
        case "Kakao": // KaKao
          // kakao body {"expiresInMillis":21599920,"id":2905806984,"expires_in":21599,"app_id":937491,"appId":937491}
          data = kaKaoService.getTokenInfo(request.getHeader("authorization").replace("Bearer ", ""));
          break;
        case "Apple": // Apple
          data = appleService.userIdFromApple(request.getHeader("authorization").replace("Bearer ", ""));
          break;
        case "Google": // Google
          data = googleService.requestUserInfo(request.getHeader("authorization").replace("Bearer ", ""));
          break;
        default:
          log.info("PlatForm  :" + request.getHeader("platformType") + " is not exist");
          break;
      }
    } catch (Exception e) {
      log.error("PlatForm  :" + request.getHeader("platformType") + " Of Authorize Error : ", e.getMessage());
    }
    return data;
  }

  public String Payload(String id, int expirationTime, String tokenType) {
    String jwt = Jwts.builder()
      .setIssuer("JiSeungGu")
      .setSubject(tokenType)
      .setAudience(id)
      //현재 시간
      .setIssuedAt(Date.from(Instant.now()))
      // 만료시간은 한달
      .setExpiration(Date.from(Instant.now().plusSeconds(expirationTime)))

      .compact();
    System.out.println("jwt : " + jwt);
    String payload = jwt.substring(jwt.indexOf('.') + 1, jwt.length() - 1);
    System.out.println("payload : " + payload);
    return payload;
  }

  public String Header() {
    //SOURCE_DESC 헤더 생성
    // JWT 헤더 생성
    Map<String, Object> header = new HashMap<>();
    header.put("alg", "RS256");
    header.put("typ", "JWT");

    Gson gson = new Gson();
    String headerJson = gson.toJson(header);
    // 헤더를 base64Url로 인코딩
//    String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
    // 헤더를 base64로 인코딩
    String encodedHeader = Base64.getEncoder().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));

    System.out.println("encodedHeader : " + encodedHeader);
    return encodedHeader;
  }

  public String Combine(String fistString, String LastString) {
    return fistString + "." + LastString;
  }

  public String GetSign(String headerpayload) {
    log.info("Method GetSign : headerpayload : {}", headerpayload);
    ByteBuffer messageBuffer = ByteBuffer.wrap(headerpayload.getBytes(StandardCharsets.UTF_8));

    SignRequest signRequest = new SignRequest();
    signRequest.setKeyId(KEY_ID);
    signRequest.setMessage(messageBuffer);
    signRequest.setSigningAlgorithm("RSASSA_PKCS1_V1_5_SHA_256");
    signRequest.setMessageType("RAW"); // RAW or DIGEST, depending on whether you want AWS to hash the message before signing

    SignResult signResult = kmsClient.sign(signRequest);
    //    String signature = Base64.encodeAsString(signResult.getSignature().array());
//    String signature = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(signResult.getSignature().array());
    String signature = Base64.getEncoder().encodeToString(signResult.getSignature().array());
    return signature;
  }

  public PublicKey ConvertPubkey(String pubkey) throws Exception {
// base64UrlPublicKey를 디코딩하여 바이트 배열로 변환
//    byte[] publicKeyBytes = Base64.getUrlDecoder().decode(pubkey);
    byte[] publicKeyBytes = Base64.getDecoder().decode(pubkey);
// 바이트 배열을 PublicKey 객체로 변환
    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA"); // 키 생성에 사용한 알고리즘("RSA", "DSA" 등)을 지정합니다.
    PublicKey decodedPublicKey = kf.generatePublic(spec);

    return decodedPublicKey;
  }

  public HashMap<String, Object> getTokenPayload(PublicKey pubkey, String token) {
    HashMap<String, Object> data = new HashMap<>();
    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) pubkey, null); // Here we don't need a private key
    JWTVerifier verifier = JWT.require(algorithm)
//      .withIssuer("TOYPROJECT")
      .build();
    try {
      DecodedJWT decodedJWT = verifier.verify(token);
      log.info(decodedJWT.getPayload());
//      data.put("code","-1");
//      data.put("message","The Access token has not expired yet. ");
      throw new CustomException(ErrorCode.NOT_EXPRIED_JWT_ACCESS_TOKEN);
//      return data;
    } catch (TokenExpiredException e) {
      String[] parts = token.split("\\.");
      // Base64 URL 디코딩을 통해 payload를 디코딩.
      byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
      String decodedPayload = new String(decodedBytes);
      data.put("payload", decodedPayload);
//          JWTVerifier expiredVerifier = JWT.require(algorithm).acceptExpiresAt(60*60*24*365).build();
//          DecodedJWT decodedJWT = expiredVerifier.verify(token);
      // 검증에 실패하면 이 블럭이 실행됩니다.
//          data.put("payload",decodedJWT.getPayload());
      return data;
    } catch (SignatureVerificationException e) {
      log.info("SignatureVerificationException : {}", e.getMessage());
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_JWT_TOKEN);
    }
  }

  public void Verify(PublicKey pubkey, String token) {
    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) pubkey, null); // Here we don't need a private key
    JWTVerifier verifier = JWT.require(algorithm)
      .withIssuer("JiSeungGu")
      .build();
    try {
      DecodedJWT decodedJWT = verifier.verify(token);
      System.out.println(decodedJWT.getPayload());
      System.out.println("Verification Success ");
      log.info("Verification Success ");
//      return "true";
      // 이후에는 추가적인 로직을 수행
    } catch (TokenExpiredException e) {
      log.info("TokenExpiredException : {}", e.getMessage());
//      return "Expired";
      throw new CustomException(ErrorCode.EXPRIED_JWT_REFRESH_TOKEN);
    } catch (SignatureVerificationException e) {
      // 검증에 실패하면 이 블럭이 실행됩니다.
      System.out.println("Verification failed: " + e.getMessage());
//      return "Signature failed";
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_JWT_TOKEN);
    } catch (JWTVerificationException e) {
      // 다른 문제가 발생하면 이 블럭이 실행됩니다.
      System.out.println("Verification failed: " + e.getMessage());
//      return "Verification failed";
      throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
  }

  public String getToken(String id, int expirationTime, String tokenType) {

    String header = Header();

    String payload = Payload(id, expirationTime, tokenType);

    String signature = GetSign(Combine(header, payload));

    String jwt = header + "." + payload + "." + signature;

    return jwt;
  }

  /* SOURCE_DESC DeviceId로 조회
   * 1. DeviceId로 조회
   * 2. 조회된 데이터가 없으면 해당 플랫폼의 userId로 조회 후 있으면 Device Id 교체 후 NICE 인증
   * 4. 해당 플랫폼의 userId도 없으면 새로 생성
   */
  public void processByDeviceId(String platForm, String userId) {
    Map<String, Function<String, Optional<SnsKeysEntity>>> platformActions = new HashMap<>();
    platformActions.put("Kakao", jpaService::processKakao);
    platformActions.put("Google", jpaService::processGoogle);
    platformActions.put("Apple", jpaService::processApple);

    platformActions.get(platForm).apply(userId).ifPresentOrElse(
      jwtEntity -> {
        log.info("userId : {} 로그인 성공", userId);
      },
      () -> {
        //유저 정보 없음.
        throw new CustomException(ErrorCode.NOT_FOUND_USER);
        //MY_THOUGHTS : api v1/toy/nice에서 가입
      }
    );
  }

  public HashMap<String, Object> processByUserId(String userId, String DeviceId, String refreshToken) {
    HashMap<String, Object> result = new HashMap<>();
    String redisRefreshToken = redisService.getValue(userId + DeviceId);
    if (redisRefreshToken == null || redisRefreshToken.isEmpty()) {
      throw new CustomException(ErrorCode.DEVICE_IS_NOT_MATCHED);

    } else if (redisRefreshToken.equals(refreshToken)) {
      String newAccessToken = this.getToken(userId, 60 * 30, "AccessToken");
      String newRefreshToken = this.getToken(userId, 60 * 60 * 24 * 7, "RefreshToken");

      redisService.setValue(userId + DeviceId, newRefreshToken);

      result.put("accessToken", newAccessToken);
      result.put("refreshToken", newRefreshToken);
    } else {
      throw new CustomException(ErrorCode.REFRESHTOKEN_IS_NOT_MATCHED);

    }
    return result;
  }


  public static void main(String[] args) throws Exception {

    String jws = Jwts.builder()
      .setId("tokenId")
      .setIssuer("TOYPROJECT")
      .setSubject("jwtAccess")
      .claim("userID", "userId")
      .claim("auth", "auth")
//    .claim("scope", "admins")
      //현재 시간
      .setIssuedAt(Date.from(Instant.now()))
      // 만료시간은 한달
      .setExpiration(Date.from(Instant.now().plusSeconds(60 * 60 * 24)))
//    .setExpiration(Date.from(Instant.now().plusSeconds(60 * 60 * 24 * 30)))
      .compact();

    System.out.println("jws :" + jws.substring(0, jws.length() - 1));
    String justPayload = jws.substring(0, jws.length() - 1);
    justPayload = justPayload.substring(justPayload.indexOf('.'), justPayload.length());
    //SOURCE_DESC 헤더 생성
    // JWT 헤더 생성
    Map<String, Object> header = new HashMap<>();
    header.put("alg", "RS256");
    header.put("typ", "JWT");

// Gson 객체 생성
    Gson gson = new Gson();
    String headerJson = gson.toJson(header);
// 헤더를 base64Url로 인코딩
//    String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
    String encodedHeader = Base64.getEncoder().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
    System.out.println(encodedHeader);
    String headerpayload = encodedHeader + justPayload;
    System.out.println("headerpayload :" + headerpayload);
    ByteBuffer messageBuffer = ByteBuffer.wrap(headerpayload.getBytes(StandardCharsets.UTF_8));

    SignRequest signRequest = new SignRequest();
    signRequest.setKeyId(KEY_ID);
    signRequest.setMessage(messageBuffer);
    signRequest.setSigningAlgorithm("RSASSA_PKCS1_V1_5_SHA_256");
    signRequest.setMessageType("RAW"); // RAW or DIGEST, depending on whether you want AWS to hash the message before signing

    SignResult signResult = kmsClient.sign(signRequest);
//    String signature = Base64.encodeAsString(signResult.getSignature().array());
//    String signature = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(signResult.getSignature().array());
    String signature = Base64.getEncoder().encodeToString(signResult.getSignature().array());
    System.out.println("Signature: " + signature);

    String token = headerpayload + "." + signature;
    System.out.println("jwt토큰 검증 ");
//    System.out.println(encodedHeader+jws.substring(jws.indexOf('.'),jws.length())+signature);
//    String token = encodedHeader+jws.substring(jws.indexOf('.'),jws.length())+signature;
    System.out.println(jws + signature);


    GetPublicKeyRequest getPublicKeyRequest = new GetPublicKeyRequest();
    getPublicKeyRequest.setKeyId(KEY_ID);
    GetPublicKeyResult getPublicKeyResult = kmsClient.getPublicKey(getPublicKeyRequest);
    ByteBuffer publicKey = getPublicKeyResult.getPublicKey();
    byte[] publicKeyBytes = new byte[publicKey.remaining()];
    publicKey.get(publicKeyBytes);
    System.out.println("Public key: " + publicKey);

    // 공개키 객체를 생성합니다.
    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    PublicKey pubkey = kf.generatePublic(spec);
//      System.out.println("publickey :"+Base64.getUrlEncoder().withoutPadding().encodeToString(pubkey.getEncoded()));

    System.out.println("token :" + token);
    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) pubkey, null); // Here we don't need a private key
    JWTVerifier verifier = JWT.require(algorithm)
      .withIssuer("TOYPROJECT")
      .build();
    try {
      DecodedJWT decodedJWT = verifier.verify(token);
      System.out.println(decodedJWT.getPayload());
      System.out.println("Verification Success ");
      // 이후에는 추가적인 로직을 수행
    } catch (JWTVerificationException e) {
      // 검증에 실패하면 이 블럭이 실행됩니다.
      System.out.println("Verification failed: " + e.getMessage());
    }
  }
}
