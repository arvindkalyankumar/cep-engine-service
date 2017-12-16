package com.sebis.cepengineservice.repository;

import com.sebis.cepengineservice.dto.QueryDTO;
import com.sebis.cepengineservice.dto.QueryResult;

import java.util.Collection;

public interface MappedSpanReadRepository {
    Collection<QueryResult> findByFilter(QueryDTO queryDTO);
}
