package com.example.OAuth2.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

/**
 * packageName   : com.example.OAuth2.entity
 * fileName  : deviceInfoEntity
 * author    : jiseung-gu
 * date  : 2023/09/04
 * description :
 **/
@Entity
@Getter
@Setter
@Table(name = "device_info")
@NoArgsConstructor
@Slf4j
public class DeviceInfoEntity implements Serializable {

  @MapsId
  @OneToOne
  @JoinColumn(name = "seq", referencedColumnName = "seq")
  private UserInfoEntity userInfo;

  @Id
  private Long seq;  // UserInfoEntity의 seq와 동일한 값을 가질 것입니다.

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "pushkey_token")
  private String pushkeyToken;

  @Builder
  public DeviceInfoEntity(UserInfoEntity userInfo,Long seq, String uuid, String pushkeyToken) {
    this.userInfo = userInfo;
    this.seq = seq;
    this.uuid = uuid;
    this.pushkeyToken = pushkeyToken;
  }
}
