package com.example.OAuth2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName   : com.example.OAuth2.dto
 * fileName  : GoogleIDModel
 * author    : jiseung-gu
 * date  : 2023/07/27
 * description :
 **/
@Getter
@Setter
public class GoogleIDModel {

  @JsonProperty("credential")
  public String crdential;

  @JsonProperty("g_csrf_token")
  public String g_csrf_token;

  @Override
  public String toString() {
    return "GoogleIDModel{" +
      "crdential='" + crdential + '\'' +
      ", g_csrf_token='" + g_csrf_token + '\'' +
      '}';
  }
}
