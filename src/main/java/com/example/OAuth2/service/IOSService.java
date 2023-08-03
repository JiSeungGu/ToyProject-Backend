package com.example.OAuth2.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName   : com.example.kakao
 * fileName  : IOSService
 * author    : jiseung-gu
 * date  : 2023/07/12
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class IOSService {
  private String appleUrl = "https://appleid.apple.com";
  private String appleKeyId = "2665DT435H"; // Key 생성할 때 만들어진 Key ID
  private String appleKeyIdPath = "keys/AuthKey_2665DT435H.p8"; // Key 생성할 때 다운로드한 p8 파일 경로
  private String appleKey = "com.test.jiseunggu"; // Service IDs 생성할 때 등록한 identifer
  private String BundleID ="JiSeungGu"; // App 생성할 때 등록한 Bundle ID
  private String appleTeamId = "2N99BTJQ7J"; // App 생성할 때 등록된 App ID Prefix

  public ResponseEntity<Object> appleLogin(String code) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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
    Reader reader = new StringReader(keyPath);
    PEMParser pemParser = new PEMParser(reader);
    JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
    PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
    PrivateKey privateKey = jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
    System.out.println(privateKey.getAlgorithm());
    System.out.println(privateKey.getEncoded().toString());
    System.out.println("keyPath : " + keyPath);

    //SOURCE_DESC
    // Privatekey를 이용하여 clientSecretKey생성
    // headerParams 적재
    Map<String, Object> headerParamsMap = new HashMap<>();
    headerParamsMap.put("kid", appleKeyId);
    headerParamsMap.put("alg", "ES256");

    // clientSecretKey 생성
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

// webClient 설정
    WebClient webClient =
      WebClient
        .builder()
        .baseUrl(appleUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

    // token api 호출
    Map tokenResponse =
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

    System.out.println("idToken : " + idToken);

    return null;
  }
}

