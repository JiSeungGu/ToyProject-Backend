package com.example.OAuth2.service;

import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.util.HashMap;

/**
 * packageName   : com.example.kakao
 * fileName  : KaKaoService
 * author    : jiseung-gu
 * date  : 2023/07/11
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class KaKaoService {
  private static final String CLIENT_ID = "a661d0e07489c796acfad0346cf92321";
//  private static final String REDIRECT_URI = "http://localhost:8080/v1/toy/kakao";
  private static final String REDIRECT_URI = "https://www.fufuanfox.com/v1/toy/kakao_web";
  private static final String KAKAO_OAUTH_URL = "https://kauth.kakao.com/oauth/authorize?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code";
  private static final String requestURL = "https://kauth.kakao.com/oauth/token";
  private static final String getTokenInfoURL = "https://kapi.kakao.com/v1/user/access_token_info";
  public String getKakaoAccessToken(String code) {
    String accessToken = "";
    String refreshToken = "";

    try {
      URL url = new URL(requestURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod("POST");
      // setDoOutput()은 OutputStream으로 POST 데이터를 넘겨 주겠다는 옵션이다.
      // POST 요청을 수행하려면 setDoOutput()을 true로 설정한다.
      conn.setDoOutput(true);

      // POST 요청에서 필요한 파라미터를 OutputStream을 통해 전송
      BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
      String sb = "grant_type=authorization_code" +
        "&client_id=" +CLIENT_ID + // REST_API_KEY
        "&redirect_uri=" +REDIRECT_URI+ // REDIRECT_URI
        "&code=" + code;
      bufferedWriter.write(sb);
      bufferedWriter.flush();

      int responseCode = conn.getResponseCode();
      System.out.println("responseCode : " + responseCode);

      // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line = "";
      StringBuilder result = new StringBuilder();

      while ((line = bufferedReader.readLine()) != null) {
        result.append(line);
      }
      System.out.println("response body : " + result);

      JsonElement element = JsonParser.parseString(result.toString());

      accessToken = element.getAsJsonObject().get("access_token").getAsString();
      refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

      System.out.println("accessToken : " + accessToken);
      System.out.println("refreshToken : " + refreshToken);

      bufferedReader.close();
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return accessToken;
  }

  public HashMap<String,Object> getTokenInfo(String token) {
    HashMap<String, Object> data = new HashMap<>();
    try {

    URL url = new URL(getTokenInfoURL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

//    conn.setRequestMethod("GET");
    // setDoOutput()은 OutputStream으로 POST 데이터를 넘겨 주겠다는 옵션이다.
    // POST 요청을 수행하려면 setDoOutput()을 true로 설정한다.
//    conn.setDoOutput(true);

    // POST 요청에서 필요한 파라미터를 OutputStream을 통해 전송
//    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
//    String sb = "Authorization= Bearer " +token;
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization","Bearer "+token);
//    bufferedWriter.write(sb);
//    bufferedWriter.flush();

    int responseCode = conn.getResponseCode();
    System.out.println("responseCode : " + responseCode);

    // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line = "";
    StringBuilder result = new StringBuilder();

    while ((line = bufferedReader.readLine()) != null) {
      result.append(line);
    }

    System.out.println("response body : " + result);

    JsonElement element = JsonParser.parseString(result.toString());

    data.put("data",element.getAsJsonObject().get("id").getAsString());
    return data;
//    return result.toString();
    }catch(IOException e){
      throw new CustomException(ErrorCode.FAILED_TO_VALIDATE_KAKAO_LOGIN);
    }
  }
}

