package com.sebis.cepengineservice.repository;

import com.sebis.cepengineservice.dto.QueryDTO;
import com.sebis.cepengineservice.entity.MappedSpan;

import java.util.Collection;

public interface MappedSpanReadRepository {
    Collection<MappedSpan> findByFilter(QueryDTO queryDTO);
}
