package com.example.common.mapper;

import com.example.common.dto.FabQueryDto;
import com.example.common.entity.FabQuery;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-28T10:05:18+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.15 (Amazon.com Inc.)"
)
public class FabQueryMapperImpl implements FabQueryMapper {

    @Override
    public FabQuery fabQueryDtoToEntity(FabQueryDto fabQueryDto) {
        if ( fabQueryDto == null ) {
            return null;
        }

        FabQuery.FabQueryBuilder fabQuery = FabQuery.builder();

        fabQuery.channel( fabQueryDto.getChannel() );
        fabQuery.codenm( fabQueryDto.getCodenm() );
        fabQuery.funcnm( fabQueryDto.getFuncnm() );
        fabQuery.params( fabQueryDto.getParams() );

        return fabQuery.build();
    }

    @Override
    public FabQueryDto entityToFabQuery(FabQuery fabQuery) {
        if ( fabQuery == null ) {
            return null;
        }

        FabQueryDto.FabQueryDtoBuilder fabQueryDto = FabQueryDto.builder();

        fabQueryDto.codenm( fabQuery.getCodenm() );
        fabQueryDto.funcnm( fabQuery.getFuncnm() );
        fabQueryDto.params( fabQuery.getParams() );

        return fabQueryDto.build();
    }
}
