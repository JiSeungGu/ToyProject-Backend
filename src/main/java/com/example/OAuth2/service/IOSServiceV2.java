package com.example.OAuth2.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName   : com.example.OAuth2
 * fileName  : IOSServiceV2
 * author    : jiseung-gu
 * date  : 2023/07/17
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class IOSServiceV2 {
  private String appleUrl = "https://appleid.apple.com";
  private String appleKeyId = "2665DT435H"; // Key 생성할 때 만들어진 Key ID
  private String appleKeyIdPath = "keys/AuthKey_2665DT435H.p8"; // Key 생성할 때 다운로드한 p8 파일 경로
  private String appleKey = "com.test.jiseunggu"; // Service IDs 생성할 때 등록한 identifer
  private String BundleID ="JiSeungGu"; // App 생성할 때 등록한 Bundle ID
//  private String appleTeamId = "2N99BTJQ7J"; // App 생성할 때 등록된 App ID Prefix
  private String appleTeamId = "93YL8E7YZM"; // App 생성할 때 등록된 App ID Prefix

  public ResponseEntity<Object> appleLogin(String code) throws Exception {
    System.out.println("code 확인 :"+code);
    /**
     * appleKeyId를 이용하여 privateKey 생성
     */

    // appleKeyId에 담겨있는 정보 가져오기
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(appleKeyIdPath);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String readLine = null;
    StringBuilder stringBuilder = new StringBuilder();
    while ((readLine = bufferedReader.readLine()) != null) {
      stringBuilder.append(readLine);
      stringBuilder.append("\n");
    }
    String keyPath = stringBuilder.toString();

    // privateKey 생성하기
    Reader reader = new StringReader(keyPath);
    PEMParser pemParser = new PEMParser(reader);
    JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
    PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
    PrivateKey privateKey = jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);

    /**
     * privateKey를 이용하여 clientSecretKey 생성
     */

    // headerParams 적재
    Map<String, Object> headerParamsMap = new HashMap<>();
    headerParamsMap.put("kid", appleKeyId);
    headerParamsMap.put("alg", "ES256");

    String clientSecretKey = Jwts
    .builder()
    .setHeaderParams(headerParamsMap)
    .setIssuer(appleTeamId)
    .setIssuedAt(new Date(System.currentTimeMillis()))
    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 30)) // 만료 시간 (30초)
    .setAudience(appleUrl)
    .setSubject(appleKey)
    .signWith(SignatureAlgorithm.ES256, privateKey)
    .compact();
  System.out.println("clientSecretKey :" + clientSecretKey);

    /**
     * code값을 이용하여 token정보 가져오기
     */

    // webClient 설정
    WebClient webClient =
      WebClient
        .builder()
        .baseUrl(appleUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

      // token api 호출
      Map<String, Object> tokenResponse =
        webClient
          .post()
          .uri(uriBuilder -> uriBuilder
            .path("/auth/token")
            .queryParam("grant_type", "authorization_code")
            .queryParam("client_id", appleKey)
            .queryParam("client_secret", clientSecretKey)
            .queryParam("code", code)
            .build())
          .retrieve()
          .bodyToMono(Map.class)
          .block();

      String idToken = (String) tokenResponse.get("id_token");
      System.out.println("IDTOKEN :"+idToken);


    return null;
  }



  public String getClientSecret() throws Exception {

    // Time in seconds when the token will expire
    long exp = System.currentTimeMillis() / 1000 + 3600;

    Map<String, Object> headers = new HashMap<>();
    headers.put("kid", appleKeyId);
    headers.put("alg", "ES256");

//    ECPrivateKey ecPrivateKey = getPrivateKeyFromP8(appleKeyIdPath);
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(appleKeyIdPath);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String readLine = null;
    StringBuilder stringBuilder = new StringBuilder();
    while ((readLine = bufferedReader.readLine()) != null) {
      stringBuilder.append(readLine);
      stringBuilder.append("\n");
    }
    String keyPath = stringBuilder.toString();

    // privateKey 생성하기
    Reader reader = new StringReader(keyPath);
    PEMParser pemParser = new PEMParser(reader);
    JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
    PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
    PrivateKey privateKey = jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);

    String token = Jwts.builder()
      .setHeader(headers)
      .setIssuer(appleTeamId)
      .setAudience(appleKey)
      .setExpiration(new Date(exp * 1000))
      .signWith(SignatureAlgorithm.ES256, privateKey)
      .compact();

    return token;
  }

  private ECPrivateKey getPrivateKeyFromP8(String filename) throws Exception {
    Path path = Paths.get(filename);
    byte[] bytes = Files.readAllBytes(path);

    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
    KeyFactory kf = KeyFactory.getInstance("EC");
    return (ECPrivateKey) kf.generatePrivate(keySpec);
  }
}
