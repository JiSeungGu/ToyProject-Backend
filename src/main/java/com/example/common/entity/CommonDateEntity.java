package com.example.common.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
public abstract class CommonDateEntity implements Serializable { // 날짜 필드 상속 처리

  @CreatedDate // Entity 생성시 자동으로 날짜세팅
  //@Temporal(TemporalType.TIMESTAMP)
  //@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Column(name = "create_dt", updatable = false)
  private LocalDateTime create_dt;

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @LastModifiedDate // Entity 수정시 자동으로 날짜세팅
  @Column(name = "updated_dt", insertable = false)
  private LocalDateTime updated_dt;

}