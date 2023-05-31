package com.example.common.response;

import com.example.common.entity.FabricData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FabricResult {
  // 응답 성공여부 : true/false
  // private boolean success;

  // 응답 코드 번호 : >= 0 정상, < 0 비정상
  private int code;

  // 응답 메시지
  private String msg;

  @JsonProperty(value = "data")
  private FabricData bodydata;

}
