package com.example.OAuth2.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "sns_keys")
@NoArgsConstructor
@Slf4j
public class SnsKeysEntity implements Serializable {
  @MapsId
  @OneToOne
  @JoinColumn(name = "seq", referencedColumnName = "seq")
  private UserInfoEntity userInfo;

  @Id
  private Long seq;  // UserInfoEntity의 seq와 동일한 값을 가질 것입니다.

  @Column(name = "kakao")
  private String kakao;

  @Column(name = "apple")
  private String apple;

  @Column(name = "google")
  private String google;

  @Builder
  public SnsKeysEntity(UserInfoEntity userInfo,Long seq, String kakao, String apple, String google) {
    this.userInfo = userInfo;
    this.seq = seq;
    this.kakao = kakao;
    this.apple = apple;
    this.google = google;
  }
}
