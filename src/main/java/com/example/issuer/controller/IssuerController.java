package com.example.issuer.controller;

import com.example.common.response.CommonResult;
import com.example.issuer.dto.IssuerDTO;
import com.example.issuer.service.IssuerService;
import com.example.projectdid.did.PrivateKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * packageName   : net.sejongtelecom.demo.domain.holder.controller
 * fileName  : HolderController
 * author    : jiseung-gu
 * date  : 2023/01/19
 * description :
 **/
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value="/v1/toy")
public class IssuerController {

  private final IssuerService issuerService;


  //COMPLETED VC 생성
  @GetMapping(value="/issuer")
  public CommonResult createVC(@RequestParam("holderId") String holderId) throws Exception, PrivateKey.BadKeyException {
    return issuerService.VcCreate(holderId);

  }


}
