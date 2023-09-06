package com.example.OAuth2.repository;

import com.example.OAuth2.entity.JwtEntity;
import com.example.entity.IssuerEntity;
import io.jsonwebtoken.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * packageName   : com.example.OAuth2.repository
 * fileName  : UserTableRepository
 * author    : jiseung-gu
 * date  : 2023/08/08
 * description :
 **/
@Repository
public interface JwtTableRepository  extends JpaRepository<JwtEntity, Long>{

}
