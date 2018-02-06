package com.sebis.cepengineservice.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sebis.cepengineservice.dto.QueryDto;
import com.sebis.cepengineservice.dto.QueryResultDto;

public interface MappedSpanReadRepository {
    Collection<QueryResultDto> findByFilter(QueryDto query, long fromTimestamp, long tillTimestamp);
    List<Map<String, Object>> findBySql(String query);
}
