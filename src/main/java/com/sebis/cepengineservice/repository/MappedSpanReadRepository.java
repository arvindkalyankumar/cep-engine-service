package com.sebis.cepengineservice.repository;

import com.sebis.cepengineservice.dto.Query;
import com.sebis.cepengineservice.dto.QueryResult;

import java.util.Collection;

public interface MappedSpanReadRepository {
    Collection<QueryResult> findByFilter(Query query, long fromTimestamp, long tillTimestamp);
}
