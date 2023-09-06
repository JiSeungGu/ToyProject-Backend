package com.example.OAuth2.controller;

import com.example.OAuth2.*;
import com.example.OAuth2.dto.AppleIDModel;
import com.example.OAuth2.dto.GoogleIDModel;
import com.example.OAuth2.dto.NiceDto;
import com.example.OAuth2.service.*;
import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName   : com.example.kakao
 * fileName  : KaKaoController
 * author    : jiseung-gu
 * date  : 2023/07/11
 * description :
 **/
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value="/v1/toy")
public class LoginController {

  private final KaKaoService kaKaoService;
  private final IOSService iosService;
  private final IOSServiceV2 iosServiceV2;
  private final IOSAppService iosAppService;
  private final ResponseService responseService;
  private final GoogleService googleService;
  private final LogOutService logOutService;

  private final JwtService jwtService;
  private final RedisService redisService;

  @GetMapping("/Onlyjwt")
  public CommonResult Onlyjwt(){
    return responseService.getSingleResult(jwtService.Onlyjwt());
  }
  @GetMapping("input")
  public CommonResult input() {
    return responseService.getSingleResult("input value Test Success");
  }
  //SOURCE_DESC : jwt 토큰 발급
  @GetMapping("/jwt")
  public CommonResult jwt(HttpServletRequest request) throws Exception {
    HashMap<String,Object> result = jwtService.jwt(request);

      return responseService.getSingleResult(result);
  }

  //SOURCE_DESC : RefreshToken 재발급
  @GetMapping("/refreshToken")
  public CommonResult refreshToken(HttpServletRequest request) throws Exception {
    HashMap<String,Object> result = jwtService.refreshToken(request);

      return responseService.getSingleResult(result);
  }

  //SOURCE_DESC : 플랫폼 기기 로그아웃
  @PostMapping("/apple-logout")
  public CommonResult applelogout(@RequestBody String body) throws Exception {

    log.info("Received request body: {}", body);
    return responseService.getSingleResult(logOutService.AppleLogout(body));

//    return responseService.getSingleResult(logOutService.AppleLogout(payload));
  }

  @PostMapping("/google-logout")
  public CommonResult googlelogout(@RequestBody String payload) throws Exception {
    return responseService.getSingleResult(logOutService.GoogleLogout(payload));
  }

  @PostMapping("/kakao-logout")
  public CommonResult kakaologout(@RequestBody String user_id,
                                  @RequestBody String referrer_type) throws Exception {
    return responseService.getSingleResult(logOutService.KakaoLogout(user_id,referrer_type));
  }

  //SOURCE_DESC : NICE 본인 인증
  @PostMapping("/nice")
  public CommonResult nice(@RequestBody NiceDto niceDto) throws Exception {
    return responseService.getSingleResult(jwtService.Nice(niceDto));
  }

  //SOURCE_DESC : jwt 토큰 검증 (gateway에서 검증)
  @GetMapping("/jwtVerify")
  public CommonResult jwtVerify(String token) throws Exception {
    log.info("Receive Token : {}",token);
    return responseService.getSingleResult(jwtService.verify(token));
  }

  @GetMapping("/pubKey")
    public CommonResult pubKey() throws Exception {
      return responseService.getSingleResult(jwtService.GetPubKey());
  }


  @GetMapping("/redisPost")
    public void redis(@RequestParam String key,
                              @RequestParam String value) throws Exception {
    log.info("key : {}",key);
    log.info("value : {}",value);
    redisService.setValue(key,value);
  }
  @GetMapping("/redisGet")
  public CommonResult redis(@RequestParam String key) throws Exception {
    log.info("key : {}",key);
    return responseService.getSingleResult(redisService.getValue(key));
  }


//  @GetMapping("/kakao")
//  public CommonResult kakaoLogin(@RequestParam("code") String code) {
//    System.out.println("code = " + code);
//    String accessToken = kaKaoService.getKakaoAccessToken(code);
//    System.out.println("accessToken = " + accessToken);
////    System.out.println(kaKaoService.getTokenInfo(accessToken));
//    return responseService.getSingleResult(kaKaoService.getTokenInfo(accessToken));
//  }

  @GetMapping("/kakao")
  public CommonResult kakaoLogin_app(@RequestParam("accessToken") String accessToken) {
//    System.out.println("code = " + code);
//    String accessToken = kaKaoService.getKakaoAccessToken(code);
    System.out.println("accessToken = " + accessToken);
    System.out.println(kaKaoService.getTokenInfo(accessToken));
    Map<String,Object> map = new HashMap<>();
    map.put("info",kaKaoService.getTokenInfo(accessToken));
    return responseService.getSingleResult(map);
  }

  @GetMapping("/kakao_web")
  public CommonResult kakaoLogin_Web(@RequestParam("code") String code) {
    System.out.println("code = " + code);
    String accessToken = kaKaoService.getKakaoAccessToken(code);
    System.out.println("accessToken = " + accessToken);
  //    System.out.println(kaKaoService.getTokenInfo(accessToken));
    Map<String,Object> map = new HashMap<>();
    map.put("info",kaKaoService.getTokenInfo(accessToken));
    return responseService.getSingleResult(map);
  }
  @PostMapping("/ios_web")
  public CommonResult iosLogin(@RequestParam("authorizationCode")  String code) throws Exception {
    System.out.println("CALL IOS");
    System.out.println(code);
    iosServiceV2.appleLogin(code);
    return responseService.getSingleResult("Ios");
  }
//
//  @PostMapping("/ios3")
//  public CommonResult iosLogin3(@RequestBody AppleIDModel model) throws Exception {
//    System.out.println("CALL IOS");
//    System.out.println(model.getAuthorizationCode());
//    iosServiceV2.appleLogin(model.getAuthorizationCode());
//    return responseService.getSingleResult("Ios");
//  }
  @PostMapping("/appios")
  public CommonResult appIos(@RequestBody AppleIDModel model) throws Exception {
    System.out.println("CALL APPIOS");
    log.info(model.toString());
//    iosAppService.userIdFromApple(model.getIdentityToken());
    return responseService.getSingleResult(iosAppService.userIdFromApple(model.getIdentityToken()));
  }

  @PostMapping("/google")
  public CommonResult google_app(@RequestBody String accessToken) throws IOException {
    System.out.println("CALL GOOGLE");
    System.out.println("accessToken :"+ accessToken);
//    System.out.println(googleIDModel.toString());
    return responseService.getSingleResult(googleService.requestUserInfo(accessToken));
  }


  @PostMapping("/google2")
  public CommonResult google_app2(@RequestBody GoogleIDModel googleIDModel) throws IOException {
    System.out.println("CALL GOOGLE");
    System.out.println("accessToken :"+ googleIDModel.getAccessToken());
//    System.out.println(googleIDModel.toString());
    return responseService.getSingleResult(googleService.requestUserInfo(googleIDModel.getAccessToken()));
  }

  @PostMapping("/google_web")
  public CommonResult google_web(@RequestParam("credential") String credential,
                                 @RequestParam("g_csrf_token") String g_csrf_token) throws IOException, GeneralSecurityException {
    System.out.println("CALL GOOGLE");
    System.out.println("credential   :"+ credential);
    System.out.println("g_csrf_token :"+ g_csrf_token);
    return responseService.getSingleResult(googleService.requestUserInfo_Web(credential));
  }
}

//    public CommonResult google(HttpServletRequest request, @RequestBody String body) {
