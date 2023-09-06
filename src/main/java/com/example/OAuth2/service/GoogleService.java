package com.example.OAuth2.service;

import com.example.common.service.ResponseService;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;

/**
 * packageName   : com.example.OAuth2
 * fileName  : GoogleService
 * author    : jiseung-gu
 * date  : 2023/07/25
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleService {

  private final ResponseService responseService;

  public HashMap<String,Object> requestUserInfo(String idToken) {
    HashMap<String,Object> data = new HashMap<>();
    String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

    //header에 accessToken을 담는다.
    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    headers.add("Authorization", "Bearer " + idToken);

    //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);

    try {
      ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
      System.out.println("response.getBody() = " + response.getBody());
//      return response.getBody();
        data.put("data", response.getBody().split(",")[0].split(":")[1].replace("\"",""));
        return data;
//      return responseService.getSingleResult(response.getBody());
//      return result;
    } catch (HttpClientErrorException e) {
      // Error handling code here - e.getStatusCode() gives the HTTP status code
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_GOOGLE_LOGIN);
//      data.put("code", "-1");
//      data.put("message", "The token is expired or cannot be authenticated.");
//      return data;
//      return responseService.getFailResult(-1, "The token is expired or cannot be authenticated.");

    }
  }

  public Payload requestUserInfo_Web(String idTokenString) throws GeneralSecurityException, IOException {
    HttpTransport transport = new NetHttpTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
      // Specify the CLIENT_ID of the app that accesses the backend:
      .setAudience(Collections.singletonList("481143605110-d9vc8qb3q7sajr3vvc9ssva1vecsnabl.apps.googleusercontent.com"))
      // Or, if multiple clients access the backend:
      //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
      .build();
    GoogleIdToken idToken = verifier.verify(idTokenString);
    System.out.println("idToken = " + idToken);
    if (idToken != null) {
      Payload payload = idToken.getPayload();
      System.out.println("payload = " + payload);
      return payload;
    }
    return null;
  }
}
