package com.example.OAuth2.controller;

import com.example.OAuth2.*;
import com.example.OAuth2.dto.AppleIDModel;
import com.example.OAuth2.dto.GoogleIDModel;
import com.example.OAuth2.service.*;
import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
//  private final IOSServiceV2 iosServiceV2;
  private final IOSAppService iosAppService;
  private final ResponseService responseService;
  private final GoogleService googleService;

  private final JwtService jwtService;

  //MY_THOUGHTS : jwt생성전에 각 로그인 플랫폼에 대한 인증 및 정보를 가져와야함.
  @GetMapping("/jwt")
  public CommonResult jwt() throws Exception {
    return responseService.getSingleResult(jwtService.jwt());
  }

  @GetMapping("/jwtVerify")
  public CommonResult jwtVerify(String token) throws Exception {
    log.info("Receive Token : {}",token);
    return responseService.getSingleResult(jwtService.verify(token));
  }

  @GetMapping("/pubKey")
    public CommonResult pubKey() throws Exception {
      return responseService.getSingleResult(jwtService.GetPubKey());
  }


//  @GetMapping("/kakao")
//  public CommonResult kakaoLogin(@RequestParam("code") String code) {
//    System.out.println("code = " + code);
//    String accessToken = kaKaoService.getKakaoAccessToken(code);
//    System.out.println("accessToken = " + accessToken);
////    System.out.println(kaKaoService.getTokenInfo(accessToken));
//    return responseService.getSingleResult(kaKaoService.getTokenInfo(accessToken));
//  }

  @GetMapping("/kakao_app")
  public CommonResult kakaoLogin_app(@RequestParam("accessToken") String accessToken) {
//    System.out.println("code = " + code);
//    String accessToken = kaKaoService.getKakaoAccessToken(code);
    System.out.println("accessToken = " + accessToken);
    System.out.println(kaKaoService.getTokenInfo(accessToken));
    Map<String,Object> map = new HashMap<>();
    map.put("info",kaKaoService.getTokenInfo(accessToken));
    return responseService.getSingleResult(map);
  }

  @GetMapping("/kakao")
  public CommonResult kakaoLogin_Web(@RequestParam("code") String code) {
    System.out.println("code = " + code);
    String accessToken = kaKaoService.getKakaoAccessToken(code);
    System.out.println("accessToken = " + accessToken);
  //    System.out.println(kaKaoService.getTokenInfo(accessToken));
    Map<String,Object> map = new HashMap<>();
    map.put("info",kaKaoService.getTokenInfo(accessToken));
    return responseService.getSingleResult(map);
  }
//  @PostMapping("/ios")
//  public CommonResult iosLogin(@RequestParam("authorizationCode")  String code) throws Exception {
//    System.out.println("CALL IOS");
//    System.out.println(code);
//    iosServiceV2.appleLogin(code);
//    return responseService.getSingleResult("Ios");
//  }
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
    log.info("model.toString" +model.toString());
//    iosAppService.userIdFromApple(model.getIdentityToken());
    return responseService.getSingleResult(iosAppService.userIdFromApple(model.getIdentityToken()));
  }

  @PostMapping("/google_app")
  public CommonResult google_app(@RequestBody GoogleIDModel googleIDModel) throws IOException {
    System.out.println("CALL GOOGLE");
    System.out.println(googleIDModel.toString());
    return responseService.getSingleResult(googleService.requestUserInfo(googleIDModel.getCrdential()));
  }

  @PostMapping("/google")
  public CommonResult google_web(@RequestParam("credential") String credential,
                                 @RequestParam("g_csrf_token") String g_csrf_token) throws IOException, GeneralSecurityException {
    System.out.println("CALL GOOGLE");
    System.out.println("credential   :"+ credential);
    System.out.println("g_csrf_token :"+ g_csrf_token);
    return responseService.getSingleResult(googleService.requestUserInfo_Web(credential));
  }
}

//    public CommonResult google(HttpServletRequest request, @RequestBody String body) {
