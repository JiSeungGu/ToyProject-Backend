package com.example.common.mapper;

import com.example.common.dto.CommonCodeDto;
import com.example.common.entity.CommonCode;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-28T10:05:18+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.15 (Amazon.com Inc.)"
)
public class CommonCodeMapperImpl implements CommonCodeMapper {

    @Override
    public CommonCodeDto entityToCommonCodeDto(CommonCode commonCode) {
        if ( commonCode == null ) {
            return null;
        }

        CommonCodeDto commonCodeDto = new CommonCodeDto();

        commonCodeDto.setCodegroup( commonCode.getCodegroup() );
        commonCodeDto.setCodename( commonCode.getCodename() );

        return commonCodeDto;
    }
}
