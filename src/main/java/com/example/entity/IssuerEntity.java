package com.example.entity;

/**
 * packageName   : com.example.entity
 * fileName  : IssuerEntity
 * author    : jiseung-gu
 * date  : 2023/04/20
 * description :
 **/
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_issuer")
public class IssuerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "issuerseq")
  private int issuerSeq;

  @Column(name = "issuername", nullable = false, length = 20)
  private String issuerName;

  @Column(name = "issuerdid", nullable = false, length = 85)
  private String issuerDid;

  @Column(name = "issuerpk", nullable = false, length = 255)
  private String issuerPk;

  @Column(name = "issuerpubk", nullable = false, length = 2048)
  private String issuerPubk;

  @Column(name = "regdate", nullable = false, updatable = false)
  private Timestamp regDate;

  @Column(name = "upddate", nullable = false)
  private Timestamp updDate;
  @PrePersist
  public void onCreate() {
    regDate = new Timestamp(System.currentTimeMillis());
    updDate = new Timestamp(System.currentTimeMillis());; // 추가: 초기화 시 updDate도 설정
  }

  @PreUpdate
  public void onUpdate() {
    updDate = new Timestamp(System.currentTimeMillis());
  }
  @Builder
  public IssuerEntity(int issuerSeq, String issuerName, String issuerDid, String issuerPk, String issuerPubk) {
    this.issuerSeq = issuerSeq;
    this.issuerName = issuerName;
    this.issuerDid = issuerDid;
    this.issuerPk = issuerPk;
    this.issuerPubk = issuerPubk;
  }

  @Override
  public String toString() {
    return "IssuerEntity{" +
            "issuerSeq=" + issuerSeq +
            ", issuerName='" + issuerName + '\'' +
            ", issuerDid='" + issuerDid + '\'' +
            ", issuerPk='" + issuerPk + '\'' +
            ", issuerPubk='" + issuerPubk + '\'' +
            '}';
  }
}
