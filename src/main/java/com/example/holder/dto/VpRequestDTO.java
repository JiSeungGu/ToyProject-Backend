package com.example.holder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * packageName   : com.example.holder.dto
 * fileName  : VpRequestDTO
 * author    : jiseung-gu
 * date  : 2023/04/23
 * description :
 **/
@Getter
@Setter
public class VpRequestDTO {

  @JsonProperty(value = "useiId")
  private String userId;

  @JsonProperty(value = "vc")
  private JsonElement vc;

  @JsonProperty(value = "claim")
  private List<String> claim;

  @Override
  public String toString() {
    return "VpRequestDTO{" +
      "userDid='" + userId + '\'' +
      ", vc='" + vc + '\'' +
      ", claim=" + claim +
      '}';
  }
}

