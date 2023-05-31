package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FabTransation implements Serializable {

  private String txid;

  private String resmsg;

  private String status;

  private int vaildcd;

  private long blockno;

  @Builder
  public FabTransation(String txid, String resmsg) {
    this.txid = txid;
    this.resmsg = resmsg;
    this.status = "E"; // E: Endorsing 완료 , C: Commiting 완료;
    this.vaildcd = -1;
    this.blockno = 0;
  }

  public FabTransation updVaildCd(int vaildcd, long blockno) {
    this.status = "C";
    this.vaildcd = vaildcd;
    this.blockno = blockno;
    return this;
  }
}
