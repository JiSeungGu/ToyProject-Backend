package com.example.OAuth2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName   : com.example.OAuth2.dto
 * fileName  : AppleIDModel
 * author    : jiseung-gu
 * date  : 2023/07/13
 * description :
 **/
@Getter
@Setter
public class AppleIDModel {

  @JsonProperty("identityToken")
  private String identityToken;

  @JsonProperty("user")
  private String user;

  @JsonProperty("authorizationCode")
  private String authorizationCode;

  @JsonProperty("state")
  private String state;


  @Override
  public String toString() {
    return "AppleIDModel{" +
      "identityToken='" + identityToken + '\'' +
      ", user='" + user + '\'' +
      ", authorizationCode='" + authorizationCode + '\'' +
      ", state='" + state + '\'' +
      '}';
  }
}
