package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FabricData {

  private int rescd;
  private String resmsg;

  private int validcd;
  private int blockno;
  private String txid;

  private Object value;

}
