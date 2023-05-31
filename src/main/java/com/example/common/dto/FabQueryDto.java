package com.example.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FabQueryDto {

  @Value("${channel}")
  private String channel;

  private String codenm;

  private String funcnm;

  private String params;

  @Builder
  public FabQueryDto(String codenm, String funcnm, String params) {
    this.codenm = codenm;
    this.funcnm = funcnm;
    this.params = params;
  }
}
