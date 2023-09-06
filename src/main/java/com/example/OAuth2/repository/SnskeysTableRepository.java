package com.example.OAuth2.repository;

import com.example.OAuth2.entity.JwtEntity;
import com.example.OAuth2.entity.SnsKeysEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * packageName   : com.example.OAuth2.repository
 * fileName  : snskeysTableRepository
 * author    : jiseung-gu
 * date  : 2023/09/04
 * description :
 **/
@Repository
public interface SnskeysTableRepository extends JpaRepository<SnsKeysEntity, Long> {

  Optional<SnsKeysEntity> findBykakao(String kakao);

  Optional<SnsKeysEntity> findBygoogle(String google);

  Optional<SnsKeysEntity> findByapple(String apple);

  Optional<SnsKeysEntity> findByseq(Long seq);


}
