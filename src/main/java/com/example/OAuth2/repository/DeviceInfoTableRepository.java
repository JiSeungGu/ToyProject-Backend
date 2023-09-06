package com.example.OAuth2.repository;

import com.example.OAuth2.entity.DeviceInfoEntity;
import com.example.OAuth2.entity.JwtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * packageName   : com.example.OAuth2.repository
 * fileName  : deviceInfoTableRepository
 * author    : jiseung-gu
 * date  : 2023/09/04
 * description :
 **/
@Repository
public interface DeviceInfoTableRepository extends JpaRepository<DeviceInfoEntity, Long> {
}
