package com.sebis.cepengineservice.service.mapper;

import com.sebis.cepengineservice.dto.StoredQueryDto;
import com.sebis.cepengineservice.entity.StoredQuery;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class StoredQueryMapper {
    public StoredQueryDto map(StoredQuery entity) {
        if (entity == null) {
            return null;
        }
        return StoredQueryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .query(entity.getQuery())
                .build();
    }

    public Collection<StoredQueryDto> map(Collection<StoredQuery> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(StoredQueryMapper::map).collect(Collectors.toList());
    }
}
