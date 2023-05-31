package com.example.common.mapper;

import com.example.common.dto.CommonCodeDto;
import com.example.common.entity.CommonCode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommonCodeMapper {
    CommonCodeMapper INSTANCE = Mappers.getMapper(CommonCodeMapper.class);

    public CommonCodeDto entityToCommonCodeDto(CommonCode commonCode);
}
