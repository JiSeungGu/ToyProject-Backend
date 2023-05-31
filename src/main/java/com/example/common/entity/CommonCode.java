package com.example.common.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/*
CREATE TABLE `common_code` (
  `codeid` varchar(10) NOT NULL,
  `codegroup` varchar(5) NOT NULL,
  `codename` varchar(1000) NOT NULL,
  `sort` int(11) NOT NULL DEFAULT 0,
  `showyn` char(1) NOT NULL DEFAULT 'Y',
  `desc1` varchar(1000) DEFAULT NULL,
  `desc2` varchar(1000) DEFAULT NULL,
  `desc3` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`codeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "common_code")
@Proxy(lazy = false)
public class CommonCode implements Serializable {

    @Id
    @Column(nullable = false, length = 10)
    private String codeid;

    @Column(nullable = false, length = 5)
    private String codegroup;

    @Column(nullable = false, length = 1000)
    private String codename;

    @Column() private int sort;

    @Column(nullable = false, length = 1)
    private String showyn;

    @Column(length = 1000)
    private String desc1;

    @Column(length = 1000)
    private String desc2;

    @Column(length = 1000)
    private String desc3;

    @Builder
    public CommonCode(String codeid, String codename, String codegroup) {
        this.codeid = codeid;
        this.codename = codename;
        this.codegroup = codegroup;
    }
}
