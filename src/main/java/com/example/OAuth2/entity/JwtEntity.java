package com.example.OAuth2.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "user")
@NoArgsConstructor
@Slf4j
//public class UserTable extends CommonDateEntity implements Serializable {
public class JwtEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int userseq;

  @Column(name = "userid", nullable = false)
  private String userId;

  @Column(name = "deviceid", nullable = false)
  private String deviceId;

  @Column(name = "kakao", nullable = false)
  private String Kakao;

  @Column(name = "apple", nullable = false)
  private String Apple;

  @Column(name = "google", nullable = false)
  private String Google;

  @Column(name = "regdate", nullable = false, updatable = false)
  private Timestamp regDate;

  @Column(name = "upddate", nullable = false)
  private Timestamp updDate;

  @PrePersist
  public void onCreate() {
    log.info("UserEntity onCreate called");
    regDate = new Timestamp(System.currentTimeMillis());
    updDate = new Timestamp(System.currentTimeMillis());; // 추가: 초기화 시 updDate도 설정
  }

  @PreUpdate
  public void onUpdate() {
    log.info("UserEntity onUpdate called");
    updDate = new Timestamp(System.currentTimeMillis());
  }

  @Builder
  public JwtEntity(String userId, String deviceId, String kakao, String apple, String google) {
    this.userId = userId;
    this.deviceId = deviceId;
    this.Kakao = kakao;
    this.Apple = apple;
    this.Google = google;
  }

  @Override
  public String toString() {
    return "JwtEntity{" +
            "userseq=" + userseq +
            ", userId='" + userId + '\'' +
            ", deviceId='" + deviceId + '\'' +
            ", Kakao='" + Kakao + '\'' +
            ", Apple='" + Apple + '\'' +
            ", Google='" + Google + '\'' +
            ", regDate=" + regDate +
            ", updDate=" + updDate +
            '}';
  }
}
