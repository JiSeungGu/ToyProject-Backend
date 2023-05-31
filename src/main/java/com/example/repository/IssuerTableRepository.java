package com.example.repository;

import com.example.entity.IssuerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * packageName   : com.example.repository
 * fileName  : IssuerTableRepository
 * author    : jiseung-gu
 * date  : 2023/04/20
 * description :
 **/
@Repository
public interface IssuerTableRepository extends JpaRepository<IssuerEntity, Long> {

  Optional<IssuerEntity> findByissuerName(String issuerName);
}
