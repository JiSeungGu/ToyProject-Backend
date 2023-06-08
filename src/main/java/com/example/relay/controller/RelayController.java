package com.example.relay.controller;

import com.example.common.response.CommonResult;
import com.example.projectdid.did.PrivateKey;

import com.example.relay.dto.RelayDTO;
import com.example.relay.service.RelayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * fileName  : HolderController
 * author    : jiseung-gu
 * date  : 2023/01/19
 * description :
 **/
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value="/v1/toy")
public class RelayController {

  private final RelayService relayService;

    //MY_THOUGHTS SaltMap, TextMap 검증 - 테스트용
//  @PostMapping(value = "/relay")
//  public CommonResult relay(@RequestBody RelayDTO relayDTO) throws PrivateKey.BadKeyException, Exception {
//    return relayService.hashCompare(relayDTO);
//  }

  @PostMapping(value = "/relay/verify")
  public CommonResult verify(@RequestBody RelayDTO relayDTO) throws PrivateKey.BadKeyException, Exception {
    return relayService.verify(relayDTO);
  }
}

