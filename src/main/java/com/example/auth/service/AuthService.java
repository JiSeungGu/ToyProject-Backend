package com.example.auth.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * packageName   : com.example.auth.service
 * fileName  : AuthService
 * author    : jiseung-gu
 * date  : 2023/05/08
 * description :
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final String CLIENTID = "1trejf87hrnpijkv52ds2rg89h";
  private final String REDIRECTURI = "http://localhost:9090/v1/toy/auth";

  //MY_THOUGHTS 클라이언트 보안키를 발급받지 않았기 때문에 Access Token 발급 시 사용하지 않아도 됨
  //private final String CLIENTSECERET = "";

  public String requestToken(String authorizationCode) {
    log.info("authorizationCode : {}", authorizationCode);
    //Cognito 인증 서버의 토큰 엔드포인트
    String tokenEndpoint = "https://pu-fox.auth.ap-northeast-2.amazoncognito.com/oauth2/token";


    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost httpPost = new HttpPost(tokenEndpoint);

      // 헤더 설정
      httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

      // 요청 파라미터 설정
      StringEntity params = new StringEntity("grant_type=authorization_code&client_id=" + CLIENTID + "&code=" + authorizationCode + "&redirect_uri=" + REDIRECTURI);
      httpPost.setEntity(params);

      // HTTP 요청 실행
      HttpResponse response = httpClient.execute(httpPost);

      // 응답 처리
      if (response.getStatusLine().getStatusCode() == 200) {
        String responseBody = EntityUtils.toString(response.getEntity());
        CheckUserInfo(responseBody);
        System.out.println("Token response: " + responseBody);
        return responseBody;
      } else {
        System.out.println("Failed to request token. Status code: " + response.getStatusLine().getStatusCode());
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return "error";
  }
  public void CheckUserInfo(String responsebody) throws ParseException {

    Gson gson = new Gson();
    JsonElement element=  gson.fromJson(responsebody, JsonElement.class);
    String idToken = element.getAsJsonObject().get("id_token").getAsString();
    JWTClaimsSet claimsSet = JWTClaimsSet.parse(getIdTokenClaims(idToken));
    log.info("claimsSet : {}", claimsSet.getClaims());
    log.info("claimsSet : {}", claimsSet.getClaims().get("cognito:username"));
    log.info("claimsSet : {}", claimsSet.getClaims().get("email"));
    log.info("claimsSet : {}", claimsSet.getClaims().get("email_verified"));
  }
  public Map<String, Object> getIdTokenClaims(String idToken) throws ParseException {
    JWSObject jwsObject = JWSObject.parse(idToken);
    return jwsObject.getPayload().toJSONObject();
  }

}
