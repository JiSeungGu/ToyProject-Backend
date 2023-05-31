package com.example.common.fabric;

import com.example.common.dto.FabQueryDto;
import com.example.common.service.FabricApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FabApiConnect {

  private final FabricApiService fabricApiService;
//  private final WebClient.Builder webClientBuilder;
//
//  @Value("${call-base-url}")
//  private String baseUrl;
//
//  @Value("${channel}")
//  private String channel;

  public  HashMap<Object, Object>  sendFabApi(FabQueryDto fabQueryDto, String apiType){
    HashMap<Object, Object> resultMap = new HashMap<Object, Object>();

    if (apiType.equals("invoke")) {
      resultMap = fabricApiService.invoke(fabQueryDto);
    }else{
      resultMap = fabricApiService.query(fabQueryDto);
    }

    return resultMap;
  }

  public HashMap<Object, Object>  connectFabricAndSetResult(FabQueryDto fabQueryDto, String apiType){
    HashMap<Object, Object> resultMap = new HashMap<Object, Object>();

    if (apiType.equals("invoke")) {
      resultMap = fabricApiService.invoke(fabQueryDto);
    }else{
      resultMap = fabricApiService.query(fabQueryDto);
    }


    return resultMap;
  }

  public HashMap<Object, Object>  connectFabricAndSetResultObject(FabQueryDto fabQueryDto, String apiType){
    HashMap<Object, Object> resultMap = new HashMap<Object, Object>();

    if (apiType.equals("invoke")) {
      resultMap = fabricApiService.invoke(fabQueryDto);
    }else{
      resultMap = fabricApiService.query(fabQueryDto);
    }
    return resultMap;
  }

}
