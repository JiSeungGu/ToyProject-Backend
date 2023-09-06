package com.example.exception.controller;

import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.exception.ApiResponse;
import com.example.exception.service.ExceptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName   : com.example.exception.controller
 * fileName  : ExceptionController
 * author    : jiseung-gu
 * date  : 2023/08/21
 * description :
 **/
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value="/v1/toy")
public class ExceptionController {

  private final ApiResponse apiResponse;
  private final ExceptionService exceptionService;
  private final ResponseService responseService;
  @GetMapping("/exception")
  public CommonResult Onlyjwt(){
    return responseService.getSingleResult(exceptionService.test());
  }
}
