//package com.example.common.controller;
//
//import com.example.common.dto.FabQueryDto;
//import com.example.common.response.CommonResult;
//import com.example.common.service.ResponseService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import javax.net.ssl.SSLException;
//
//@Slf4j
//@RequiredArgsConstructor
//@RestController
//@RequestMapping(value = "/v1")
//public class FabricApiController {
//
//  private final ResponseService responseService;
//  private final FabricApiService fabricApiService;
//
//  @PostMapping(value = "/invoke")
//  public CommonResult invoke(@RequestBody FabQueryDto fabQueryDto) throws SSLException {
//    return responseService.getSingleResult(fabricApiService.invoke(fabQueryDto));
//  }
//
//  @GetMapping(value = "/query")
//  public CommonResult query(@ModelAttribute FabQueryDto fabQueryDto) throws SSLException {
//    return responseService.getSingleResult(fabricApiService.query(fabQueryDto));
//  }
//
//  @GetMapping(value = "/{transactionId}")
//  public CommonResult transactionInfo(@PathVariable String transactionId) {
//    return fabricApiService.transactionInfo(transactionId);
//  }
//
//}
