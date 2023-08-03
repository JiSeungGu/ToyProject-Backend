package com.example.OAuth2.service;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

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

  public ResponseEntity<String> requestUserInfo(String idToken) {
    String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

    //header에 accessToken을 담는다.
    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    //headers.add("Authorization","Bearer "+oAuthToken.getAccess_token());
    headers.add("Authorization", "Bearer " + idToken);

    //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
    ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
    System.out.println("response.getBody() = " + response.getBody());
    return response;
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
    if (idToken != null) {
      Payload payload = idToken.getPayload();
      return payload;
    }
    return null;
  }
}
