package com.sebis.cepengineservice.repository;

import com.sebis.cepengineservice.dto.QueryDto;
import com.sebis.cepengineservice.dto.QueryResultDto;

import java.util.Collection;

public interface MappedSpanReadRepository {
    Collection<QueryResultDto> findByFilter(QueryDto query, long fromTimestamp, long tillTimestamp);
}
