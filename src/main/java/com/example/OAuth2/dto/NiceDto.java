package com.example.OAuth2.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * packageName   : com.example.OAuth2.dto
 * fileName  : NiceDto
 * author    : jiseung-gu
 * date  : 2023/09/05
 * description :
 **/
@Getter
@Setter
public class NiceDto {

  private Long seq;
  private String name;
  private Date birthDate;
  private String socialNumber;
  private String CI;
  private String type;
  private String token;
  private String uuid;
  private String pushkeyToken;

  @Override
  public String toString() {
    return "NiceDto{" +
            "seq=" + seq +
            ", name='" + name + '\'' +
            ", birthDate='" + birthDate + '\'' +
            ", socialNumber='" + socialNumber + '\'' +
            ", CI='" + CI + '\'' +
            ", type='" + type + '\'' +
            ", token='" + token + '\'' +
            ", uuid='" + uuid + '\'' +
            ", pushkeyToken='" + pushkeyToken + '\'' +
            '}';
  }
}
