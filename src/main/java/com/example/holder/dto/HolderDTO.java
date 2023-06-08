package com.example.holder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * fileName  : HolderDTO
 * author    : jiseung-gu
 * date  : 2023/01/19
 * description :
 **/
@Getter
@Setter
public class HolderDTO {

  @JsonProperty(value = "user_did")
  private String userDid;

  @JsonProperty(value = "user_id")
  private String userId;

  @JsonProperty(value = "user_passwd")
  private String userPasswd;

  @JsonProperty(value = "user_name")
  private String userName;

  @JsonProperty(value = "user_phone_no")
  private String userPhoneNo;


  @Override
  public String toString() {
    return "HolderDTO{" +
      "userDid='" + userDid + '\'' +
      ", userId='" + userId + '\'' +
      ", userPasswd='" + userPasswd + '\'' +
      ", userName='" + userName + '\'' +
      ", userPhoneNo='" + userPhoneNo + '\'' +
      '}';
  }
}
