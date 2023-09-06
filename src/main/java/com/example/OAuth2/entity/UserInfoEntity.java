package com.example.OAuth2.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * packageName   : com.example.OAuth2.entity
 * fileName  : userInfoEntity
 * author    : jiseung-gu
 * date  : 2023/09/04
 * description :
 **/
@Entity
@Getter
@Setter
@Table(name = "user_info")
@NoArgsConstructor
@Slf4j
public class UserInfoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long seq;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "birth_date", nullable = false)
  private Date birthDate;

  @Column(name = "social_number")  // 주의: 암호화 필요
  private String socialNumber;

  @Column(name = "CI")
  private String CI;

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
  public UserInfoEntity(String name, Date birthDate, String socialNumber, String CI) {
    this.name = name;
    this.birthDate = birthDate;
    this.socialNumber = socialNumber;
    this.CI = CI;
  }

  @Override
  public String toString() {
    return "UserInfoEntity{" +
            "seq=" + seq +
            ", name='" + name + '\'' +
            ", birthDate=" + birthDate +
            ", socialNumber='" + socialNumber + '\'' +
            ", CI='" + CI + '\'' +
            ", regDate=" + regDate +
            ", updDate=" + updDate +
            '}';
  }
}
