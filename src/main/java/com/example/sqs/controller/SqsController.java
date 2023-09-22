package com.example.sqs.controller;

import com.example.common.response.CommonResult;
import com.example.common.service.ResponseService;
import com.example.sqs.service.SqsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName   : com.example.sqs.controller
 * fileName  : SqsController
 * author    : jiseung-gu
 * date  : 2023/09/21
 * description :
 **/

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/sqs")
public class SqsController {

  private final ResponseService responseService;
  private final SqsService sqsService;

  public CommonResult sqs() {

    return responseService.getSingleResult("sqs Test Success");
  }
}
