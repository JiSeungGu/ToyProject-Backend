package com.example.OAuth2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

  @JsonProperty("userID")
  private String userID;

  @JsonProperty("grantedScopes")
  private List<String> grantedScopes;

  @JsonProperty("accessToken")
  private String accessToken;

  @JsonProperty("refreshToken")
  private String refreshToken;

  @JsonProperty("idToken")
  private String idToken;

  @Override
  public String toString() {
    return "GoogleIDModel{" +
      "userID='" + userID + '\'' +
      ", grantedScopes=" + grantedScopes +
      ", accessToken='" + accessToken + '\'' +
      ", refreshToken='" + refreshToken + '\'' +
      ", idToken='" + idToken + '\'' +
      '}';
  }
}
