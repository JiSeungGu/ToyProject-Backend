package com.example.common.repository;


import com.example.common.entity.CommonCode;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface CommonCodeJpaRepo extends JpaRepository<CommonCode, Long> {

    Optional<CommonCode> findByCodeid(String codeid);

}
