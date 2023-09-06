package com.example.OAuth2.service;

import com.example.OAuth2.entity.JwtEntity;
import com.example.OAuth2.entity.SnsKeysEntity;
import com.example.OAuth2.repository.JwtTableRepository;
import com.example.OAuth2.repository.SnskeysTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * packageName   : com.example.OAuth2.service
 * fileName  : JpaService
 * author    : jiseung-gu
 * date  : 2023/09/04
 * description :
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaService {

  private final SnskeysTableRepository snsKeysTableservice;
  public  Optional<SnsKeysEntity> processKakao(String data) {
    // kakao 데이터 처리
    return snsKeysTableservice.findBykakao(data);
  }
  public Optional<SnsKeysEntity> processGoogle(String data) {
    // google 데이터 처리
    return snsKeysTableservice.findBygoogle(data);
  }
  public Optional<SnsKeysEntity> processApple(String data) {
    // apple 데이터 처리
    return snsKeysTableservice.findByapple(data);
  }

  public void UpdateKakao(Long seq,String token) {
    // kakao 토큰 업데이트 처리
    snsKeysTableservice.findByseq(seq).ifPresent(snsKeysEntity -> {
      snsKeysEntity.setKakao(token);
      snsKeysTableservice.save(snsKeysEntity);
    });
  }

  public void UpdateGoogle(Long seq,String token) {
    // google 토큰 업데이트 처리
    snsKeysTableservice.findByseq(seq).ifPresent(snsKeysEntity -> {
      snsKeysEntity.setGoogle(token);
      snsKeysTableservice.save(snsKeysEntity);
    });
  }

  public void UpdateApple(Long seq,String token) {
    // apple 토큰 업데이트 처리
    snsKeysTableservice.findByseq(seq).ifPresent(snsKeysEntity -> {
      snsKeysEntity.setApple(token);
      snsKeysTableservice.save(snsKeysEntity);
    });
  }
}

