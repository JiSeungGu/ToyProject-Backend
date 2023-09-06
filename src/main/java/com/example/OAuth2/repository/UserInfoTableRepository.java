package com.example.OAuth2.repository;

import com.example.OAuth2.entity.JwtEntity;
import com.example.OAuth2.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * packageName   : com.example.OAuth2.repository
 * fileName  : userInfoTableRepository
 * author    : jiseung-gu
 * date  : 2023/09/04
 * description :
 **/
@Repository
public interface UserInfoTableRepository extends JpaRepository<UserInfoEntity, Long> {

  Optional<UserInfoEntity> findByCI(String CI);
}
