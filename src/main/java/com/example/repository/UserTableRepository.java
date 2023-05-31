package com.example.repository;

import com.example.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * packageName   : com.example.domain.aws.repository
 * fileName  : UserTableRepository
 * author    : jiseung-gu
 * date  : 2023/03/29
 * description :
 **/
@Repository
public interface UserTableRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByuserId(String userid);

  Optional<UserEntity> findByuserDid(String userDid);

}
