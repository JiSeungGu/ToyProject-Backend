package com.example.relay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
/**
 * packageName   : com.example.issuer.dto
 * fileName  : IssuerDTO
 * author    : jiseung-gu
 * date  : 2023/03/27
 * description :
 **/
@Getter
@Setter
@Builder
public class RelayDTO {

  @JsonProperty(value = "holderid")
  @NotEmpty
  private String holderid;

  @JsonProperty(value = "vp")
  @NotEmpty
  private String vp;

  @Builder
  public RelayDTO(String holderid, String vp) {
    this.holderid = holderid;
    this.vp = vp;
  }
  @Override
  public String toString() {
    return "RelayDTO{" +
        "holderid='" + holderid + '\'' +
        ", vp='" + vp + '\'' +
        '}';
  }
}
