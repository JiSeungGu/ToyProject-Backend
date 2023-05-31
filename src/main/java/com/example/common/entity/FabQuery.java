package com.example.common.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FabQuery {

  private String channel;

  private String codenm;

  private String funcnm;

  private String params;

  @Builder
  public FabQuery(String channel, String codenm, String funcnm, String params) {
    this.channel = channel;
    this.codenm = codenm;
    this.funcnm = funcnm;
    this.params = params;
  }
}
