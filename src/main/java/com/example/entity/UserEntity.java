package com.example.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.sql.Timestamp;
/**
 * packageName   : com.example.domain.aws.entity
 * fileName  : UserTable
 * author    : jiseung-gu
 * date  : 2023/03/29
 * description :
 **/

@Entity
@Getter
@Setter
@Table(name = "t_user")
@NoArgsConstructor
@Slf4j
//public class UserTable extends CommonDateEntity implements Serializable {
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int userseq;

  @Column(name = "userid", nullable = false)
  private String userId;

  @Column(name = "userpass", nullable = false)
  private String userPass;

  @Column(name = "username", nullable = false)
  private String userName;

  @Column(name = "userphoneno", nullable = false)
  private String userPhoneNo;

  @Column(name = "status", nullable = false)
  private String status = "0";

  @Column(name = "userdid", nullable = false)
  private String userDid;

  @Column(name = "regdate", nullable = false, updatable = false)
  private Timestamp regDate;

  @Column(name = "upddate", nullable = false)
  private Timestamp updDate;

  @Column(name = "privatekey", nullable = false)
  private String privateKey;
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
  public UserEntity(String userId, String userPass, String userName, String userPhoneNo, String privateKey,String userDid) {
    this.userId = userId;
    this.userPass = userPass;
    this.userName = userName;
    this.userPhoneNo = userPhoneNo;
    this.privateKey = privateKey;
    this.userDid = userDid;
  }

  @Override
  public String toString() {
    return "UserTable{" +
      "userseq=" + userseq +
      ", userId='" + userId + '\'' +
      ", userPass='" + userPass + '\'' +
      ", userName='" + userName + '\'' +
      ", userPhoneNo='" + userPhoneNo + '\'' +
      ", status=" + status +
      ", regDate=" + regDate +
      ", updDate=" + updDate +
      ", privateKey='" + privateKey + '\'' +
      ", userDid='" + userDid + '\'' +
      '}';
  }

}
