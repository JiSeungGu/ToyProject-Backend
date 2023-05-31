package com.example.auth.controller;

import com.example.auth.service.AuthService;
import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName   : com.example.auth.controller
 * fileName  : AuthController
 * author    : jiseung-gu
 * date  : 2023/05/08
 * description :
 **/
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value="/v1/toy")
public class AuthController {

  private final ResponseService responseService;
  private final AuthService authService;

  @GetMapping("/auth")
  public CommonResult auth(@RequestParam("code") String code) {
    return responseService.getSingleResult(authService.requestToken(code));
  }
}
