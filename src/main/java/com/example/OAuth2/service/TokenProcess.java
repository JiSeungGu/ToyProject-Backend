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
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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


  public String Payload(){
    String jwt =  Jwts.builder()
      .setId("tokenId")
      .setIssuer("JiSeungGu")
      .setSubject("jwtAccess")
      .claim("userID", "userId")
      .claim("auth","auth")
//    .claim("scope", "admins")
      //현재 시간
      .setIssuedAt(Date.from(Instant.now()))
      // 만료시간은 한달
      .setExpiration(Date.from(Instant.now().plusSeconds(60*60*24)))
//    .setExpiration(Date.from(Instant.now().plusSeconds(60 * 60 * 24 * 30)))
      .compact();
    System.out.println("jwt : " + jwt);
    String payload = jwt.substring(jwt.indexOf('.')+1,jwt.length()-1);
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
    String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
    System.out.println("encodedHeader : " + encodedHeader);
    return encodedHeader;
  }

  public String Combine(String fistString, String LastString) {
    return fistString+"."+LastString;
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
    String signature = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(signResult.getSignature().array());
    return signature;
  }

  public PublicKey ConvertPubkey(String pubkey) throws Exception {
// base64UrlPublicKey를 디코딩하여 바이트 배열로 변환
    byte[] publicKeyBytes = Base64.getUrlDecoder().decode(pubkey);

// 바이트 배열을 PublicKey 객체로 변환
    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA"); // 키 생성에 사용한 알고리즘("RSA", "DSA" 등)을 지정합니다.
    PublicKey decodedPublicKey = kf.generatePublic(spec);

    return decodedPublicKey;
  }

  public Boolean Verify(PublicKey pubkey, String token) {
    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) pubkey, null); // Here we don't need a private key
    JWTVerifier verifier = JWT.require(algorithm)
      .withIssuer("TOYPROJECT")
      .build();
    try {
      DecodedJWT decodedJWT = verifier.verify(token);
      System.out.println(decodedJWT.getPayload());
      System.out.println("Verification Success ");

      return true;
      // 이후에는 추가적인 로직을 수행
    } catch (JWTVerificationException e){
      // 검증에 실패하면 이 블럭이 실행됩니다.
      System.out.println("Verification failed: " + e.getMessage());
      return false;
    }
  }
  public static void main(String[] args) throws Exception {

    String jws = Jwts.builder()
      .setId("tokenId")
      .setIssuer("TOYPROJECT")
      .setSubject("jwtAccess")
      .claim("userID", "userId")
      .claim("auth","auth")
//    .claim("scope", "admins")
      //현재 시간
      .setIssuedAt(Date.from(Instant.now()))
      // 만료시간은 한달
      .setExpiration(Date.from(Instant.now().plusSeconds(60*60*24)))
//    .setExpiration(Date.from(Instant.now().plusSeconds(60 * 60 * 24 * 30)))
      .compact();

    System.out.println("jws :"+jws.substring(0,jws.length()-1));
    String justPayload = jws.substring(0,jws.length()-1);
    justPayload = justPayload.substring(justPayload.indexOf('.'),justPayload.length());
    //SOURCE_DESC 헤더 생성
    // JWT 헤더 생성
    Map<String, Object> header = new HashMap<>();
    header.put("alg", "RS256");
    header.put("typ", "JWT");

// Gson 객체 생성
    Gson gson = new Gson();
    String headerJson = gson.toJson(header);
// 헤더를 base64Url로 인코딩
    String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
    System.out.println(encodedHeader);
    String headerpayload = encodedHeader+justPayload;
    System.out.println("headerpayload :" +headerpayload);
    ByteBuffer messageBuffer = ByteBuffer.wrap(headerpayload.getBytes(StandardCharsets.UTF_8));

    SignRequest signRequest = new SignRequest();
    signRequest.setKeyId(KEY_ID);
    signRequest.setMessage(messageBuffer);
    signRequest.setSigningAlgorithm("RSASSA_PKCS1_V1_5_SHA_256");
    signRequest.setMessageType("RAW"); // RAW or DIGEST, depending on whether you want AWS to hash the message before signing

    SignResult signResult = kmsClient.sign(signRequest);
//    String signature = Base64.encodeAsString(signResult.getSignature().array());
    String signature = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(signResult.getSignature().array());

    System.out.println("Signature: " + signature);

   String token = headerpayload+"."+signature;
    System.out.println("jwt토큰 검증 ");
//    System.out.println(encodedHeader+jws.substring(jws.indexOf('.'),jws.length())+signature);
//    String token = encodedHeader+jws.substring(jws.indexOf('.'),jws.length())+signature;
    System.out.println(jws+signature);


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
      System.out.println("publickey :"+Base64.getUrlEncoder().withoutPadding().encodeToString(pubkey.getEncoded()));
      System.out.println("token :"+token);
      Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) pubkey, null); // Here we don't need a private key
      JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer("TOYPROJECT")
        .build();
      try {
      DecodedJWT decodedJWT = verifier.verify(token);
      System.out.println(decodedJWT.getPayload());
      System.out.println("Verification Success ");
      // 이후에는 추가적인 로직을 수행
    } catch (JWTVerificationException e){
      // 검증에 실패하면 이 블럭이 실행됩니다.
      System.out.println("Verification failed: " + e.getMessage());
    }
  }
}
