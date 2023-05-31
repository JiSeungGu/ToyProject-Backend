package com.example.common.mapper;


import com.example.common.dto.FabQueryDto;
import com.example.common.entity.FabQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FabQueryMapper {
  FabQueryMapper INSTANCE = Mappers.getMapper(FabQueryMapper.class);

  public FabQuery fabQueryDtoToEntity(FabQueryDto fabQueryDto);

  public FabQueryDto entityToFabQuery(FabQuery fabQuery);
}
